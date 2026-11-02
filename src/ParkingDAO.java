import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ParkingDAO
{
    public String parkVehicle(
        Vehicle vehicle,
        int slotNumber) throws SQLException
    {
        String ticketNumber = null;

        try (Connection con = DatabaseConnection.getConnection())
        {
            con.setAutoCommit(false);

            try
            {
                int vehicleId = findOrCreateVehicle(con, vehicle);

                String temporaryNumber =
                    "PENDING-" + java.util.UUID.randomUUID();

                String insertTicket =
                    "INSERT INTO tickets " +
                    "(ticket_number, vehicle_id, slot_number, status) " +
                    "VALUES (?, ?, ?, 'ACTIVE')";

                int ticketId;

                try (PreparedStatement ps = con.prepareStatement(
                         insertTicket, Statement.RETURN_GENERATED_KEYS))
                {
                    ps.setString(1, temporaryNumber);
                    ps.setInt(2, vehicleId);
                    ps.setInt(3, slotNumber);

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys())
                    {
                        if (!keys.next())
                        {
                            throw new SQLException(
                                "Unable to generate ticket ID.");
                        }

                        ticketId = keys.getInt(1);
                    }
                }

                ticketNumber = "T-" + (1000L + ticketId);

                String updateTicket =
                    "UPDATE tickets SET ticket_number = ? " +
                    "WHERE ticket_id = ?";

                try (PreparedStatement ps = con.prepareStatement(updateTicket))
                {
                    ps.setString(1, ticketNumber);
                    ps.setInt(2, ticketId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE vehicles SET status = 'PARKED' " +
                    "WHERE vehicle_id = ?"))
                {
                    ps.setInt(1, vehicleId);
                    ps.executeUpdate();
                }

                con.commit();
            }
            catch (SQLException e)
            {
                con.rollback();
                throw e;
            }
            finally
            {
                con.setAutoCommit(true);
            }
        }

        return ticketNumber;
    }

    private int findOrCreateVehicle(
        Connection con,
        Vehicle vehicle) throws SQLException
    {
        String select =
            "SELECT vehicle_id, status FROM vehicles " +
            "WHERE number_plate = ? FOR UPDATE";

        try (PreparedStatement ps = con.prepareStatement(select))
        {
            ps.setString(1, vehicle.getNumberPlate());

            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    int vehicleId = rs.getInt("vehicle_id");
                    String status = rs.getString("status");

                    if ("PARKED".equals(status))
                    {
                        throw new SQLException(
                            "This number plate is already parked.");
                    }

                    String update =
                        "UPDATE vehicles SET vehicle_type = ?, status = 'PARKED' " +
                        "WHERE vehicle_id = ?";

                    try (PreparedStatement ups = con.prepareStatement(update))
                    {
                        ups.setString(
                            1, vehicle.getVehicleType().name());
                        ups.setInt(2, vehicleId);
                        ups.executeUpdate();
                    }

                    return vehicleId;
                }
            }
        }

        String insert =
            "INSERT INTO vehicles (number_plate, vehicle_type, status) " +
            "VALUES (?, ?, 'PARKED')";

        try (PreparedStatement ps = con.prepareStatement(
            insert, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, vehicle.getNumberPlate());
            ps.setString(2, vehicle.getVehicleType().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys())
            {
                if (keys.next())
                {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Unable to register vehicle.");
    }

    public List<ActiveParkingRecord> getActiveParkingRecords()
        throws SQLException
    {
        List<ActiveParkingRecord> records = new ArrayList<>();

        String sql =
            "SELECT t.ticket_number, t.slot_number, " +
            "v.number_plate, v.vehicle_type " +
            "FROM tickets t " +
            "JOIN vehicles v ON t.vehicle_id = v.vehicle_id " +
            "WHERE t.status = 'ACTIVE' " +
            "ORDER BY t.slot_number";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                records.add(new ActiveParkingRecord(
                    rs.getString("ticket_number"),
                    rs.getString("number_plate"),
                    VehicleType.valueOf(rs.getString("vehicle_type")),
                    rs.getInt("slot_number")
                ));
            }
        }

        return records;
    }

    public double getTotalCollection() throws SQLException
    {
        String sql =
            "SELECT COALESCE(SUM(amount), 0) AS total FROM payments";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            if (rs.next())
            {
                return rs.getDouble("total");
            }
        }

        return 0.0;
    }

    public void completeParking(
        String ticketNumber,
        double hours,
        int chargeableHours,
        double amount,
        boolean wasCapped) throws SQLException
    {
        try (Connection con = DatabaseConnection.getConnection())
        {
            con.setAutoCommit(false);

            try
            {
                int ticketId;
                int vehicleId;

                String findTicket =
                    "SELECT ticket_id, vehicle_id FROM tickets " +
                    "WHERE ticket_number = ? AND status = 'ACTIVE' " +
                    "FOR UPDATE";

                try (PreparedStatement ps = con.prepareStatement(findTicket))
                {
                    ps.setString(1, ticketNumber);

                    try (ResultSet rs = ps.executeQuery())
                    {
                        if (!rs.next())
                        {
                            throw new SQLException(
                                "Active ticket not found.");
                        }

                        ticketId = rs.getInt("ticket_id");
                        vehicleId = rs.getInt("vehicle_id");
                    }
                }

                String insertPayment =
                    "INSERT INTO payments " +
                    "(ticket_id, parking_hours, chargeable_hours, amount, was_capped) " +
                    "VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(insertPayment))
                {
                    ps.setInt(1, ticketId);
                    ps.setDouble(2, hours);
                    ps.setInt(3, chargeableHours);
                    ps.setDouble(4, amount);
                    ps.setBoolean(5, wasCapped);

                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE tickets SET status = 'COMPLETED', " +
                    "exit_time = CURRENT_TIMESTAMP WHERE ticket_id = ?"))
                {
                    ps.setInt(1, ticketId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE vehicles SET status = 'EXITED' " +
                    "WHERE vehicle_id = ?"))
                {
                    ps.setInt(1, vehicleId);
                    ps.executeUpdate();
                }

                con.commit();
            }
            catch (SQLException e)
            {
                con.rollback();
                throw e;
            }
            finally
            {
                con.setAutoCommit(true);
            }
        }
    }
}