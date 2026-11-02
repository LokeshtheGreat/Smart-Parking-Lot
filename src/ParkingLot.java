import java.sql.SQLException;
import java.util.List;

public class ParkingLot
{
    private double totalCollection = 0.0;

    private static final int MAX_CAPACITY = 20;

    private final Vehicle[] slots = new Vehicle[MAX_CAPACITY];
    private final Ticket[] tickets = new Ticket[MAX_CAPACITY];

    private final ParkingDAO dao = new ParkingDAO();

    private static final String LINE =
        "================================================";

    public ParkingLot() throws SQLException
    {
        restoreParkingState();
    }

    private void restoreParkingState() throws SQLException
    {
        List<ActiveParkingRecord> records =
            dao.getActiveParkingRecords();

        for (ActiveParkingRecord record : records)
        {
            Vehicle vehicle = createVehicle(
                record.getVehicleType(),
                record.getNumberPlate());

            int slotNumber = record.getSlotNumber();

            int slotsNeeded =
                record.getVehicleType().getSlotsNeeded();

            if (slotNumber < 1 ||
                slotNumber + slotsNeeded - 1 > MAX_CAPACITY)
            {
                throw new SQLException(
                    "Invalid slot assignment for ticket: " +
                    record.getTicketNumber());
            }

            for (int i = 0; i < slotsNeeded; i++)
            {
                if (slots[slotNumber - 1 + i] != null)
                {
                    throw new SQLException(
                        "Conflicting slot assignment for ticket: " +
                        record.getTicketNumber());
                }
            }

            int ticketIndex = findEmptyTicketIndex();

            if (ticketIndex == -1)
            {
                throw new SQLException(
                    "Too many active tickets in the database.");
            }

            Ticket ticket = new Ticket(
                vehicle,
                slotNumber,
                record.getTicketNumber());

            tickets[ticketIndex] = ticket;

            for (int i = 0; i < slotsNeeded; i++)
            {
                slots[slotNumber - 1 + i] = vehicle;
            }
        }

        totalCollection = dao.getTotalCollection();
    }

    private Vehicle createVehicle(
        VehicleType type,
        String numberPlate)
    {
        switch (type)
        {
            case BIKE:
                return new Bike(VehicleType.BIKE, numberPlate);

            case CAR:
                return new Car(VehicleType.CAR, numberPlate);

            case TRUCK:
                return new Truck(VehicleType.TRUCK, numberPlate);

            default:
                throw new IllegalArgumentException(
                    "Unknown vehicle type.");
        }
    }

    private int findEmptyTicketIndex()
    {
        for (int i = 0; i < tickets.length; i++)
        {
            if (tickets[i] == null)
            {
                return i;
            }
        }

        return -1;
    }

    public boolean isDuplicate(String numberPlate)
    {
        for (Vehicle slot : slots)
        {
            if (slot != null &&
                slot.getNumberPlate().equalsIgnoreCase(numberPlate))
            {
                return true;
            }
        }

        return false;
    }

    public int findAvailableSlot(VehicleType type)
        throws ParkingFullException
    {
        int slotsNeeded = type.getSlotsNeeded();

        for (int i = 0; i <= slots.length - slotsNeeded; i++)
        {
            boolean available = true;

            for (int j = 0; j < slotsNeeded; j++)
            {
                if (slots[i + j] != null)
                {
                    available = false;
                    break;
                }
            }

            if (available)
            {
                return i + 1;
            }
        }

        throw new ParkingFullException(
            "No suitable parking slot is available.");
    }

    public Ticket findTicket(String ticketNumber)
    {
        for (Ticket ticket : tickets)
        {
            if (ticket != null &&
                ticket.getTicketNumber().equalsIgnoreCase(ticketNumber))
            {
                return ticket;
            }
        }

        return null;
    }

    public Ticket parkVehicle(Vehicle vehicle)
        throws ParkingFullException, DuplicateVehicleException
    {
        if (isDuplicate(vehicle.getNumberPlate()))
        {
            throw new DuplicateVehicleException(
                "Vehicle with number plate " +
                vehicle.getNumberPlate() + " is already parked.");
        }

        int ticketIndex = findEmptyTicketIndex();

        if (ticketIndex == -1)
        {
            throw new ParkingFullException(
                "No ticket entry is available.");
        }

        int slotNumber = findAvailableSlot(
            vehicle.getVehicleType());

        String ticketNumber;

        try
        {
            ticketNumber = dao.parkVehicle(vehicle, slotNumber);
        }
        catch (SQLException e)
        {
            e.printStackTrace();

            throw new IllegalStateException(
                "Could not save parking session to database.", e);
        }

        Ticket ticket = new Ticket(
            vehicle,
            slotNumber,
            ticketNumber);

        tickets[ticketIndex] = ticket;

        int slotsNeeded =
            vehicle.getVehicleType().getSlotsNeeded();

        for (int i = 0; i < slotsNeeded; i++)
        {
            slots[slotNumber - 1 + i] = vehicle;
        }

        System.out.println("\n" + LINE);
        System.out.println("             PARKING SUCCESSFUL");
        System.out.println(LINE);
        System.out.printf("Vehicle Type       : %s%n",
            vehicle.getVehicleType());
        System.out.printf("Number Plate       : %s%n",
            vehicle.getNumberPlate());
        System.out.printf("Ticket Number      : %s%n",
            ticketNumber);
        System.out.printf("Assigned Slot      : %d%n",
            slotNumber);

        if (slotsNeeded > 1)
        {
            System.out.printf("Slots Occupied     : %d - %d%n",
                slotNumber, slotNumber + slotsNeeded - 1);
        }

        System.out.println(LINE);

        return ticket;
    }

    public Receipt exitVehicle(String ticketNumber, double hours)
    {
        Ticket currentTicket = findTicket(ticketNumber);

        if (currentTicket == null ||
            hours <= 0 ||
            !Double.isFinite(hours))
        {
            return null;
        }

        int roundedHours = (int) Math.ceil(hours);

        int chargeableHours = Math.min(roundedHours, 8);

        boolean wasCapped = roundedHours > 8;

        double amount = currentTicket.getVehicle()
            .calculateCharge(chargeableHours);

        try
        {
            dao.completeParking(
                currentTicket.getTicketNumber(),
                hours,
                chargeableHours,
                amount,
                wasCapped);
        }
        catch (SQLException e)
        {
            System.err.println(
                "\nDatabase update failed: " + e.getMessage());

            return null;
        }

        Receipt receipt = new Receipt(
            currentTicket.getTicketNumber(),
            currentTicket.getVehicle(),
            hours,
            chargeableHours,
            amount,
            wasCapped);

        int slotsUsed = currentTicket.getVehicle()
            .getVehicleType().getSlotsNeeded();

        for (int i = 0; i < slotsUsed; i++)
        {
            slots[currentTicket.getSlotNumber() - 1 + i] = null;
        }

        for (int i = 0; i < tickets.length; i++)
        {
            if (currentTicket == tickets[i])
            {
                tickets[i] = null;
                totalCollection += amount;
                return receipt;
            }
        }

        return null;
    }

    public double getTotalCollection()
    {
        return totalCollection;
    }

    public int getTotalParkedVehicles()
    {
        int count = 0;

        for (Ticket ticket : tickets)
        {
            if (ticket != null)
            {
                count++;
            }
        }

        return count;
    }

    public int getAvailableSlots()
    {
        int count = 0;

        for (Vehicle slot : slots)
        {
            if (slot == null)
            {
                count++;
            }
        }

        return count;
    }

    public void displayParkingStatus()
    {
        System.out.println("\n" + LINE);
        System.out.println("                 PARKING STATUS");
        System.out.println(LINE);

        for (int i = 0; i < MAX_CAPACITY; i++)
        {
            Vehicle vehicle = slots[i];

            if (vehicle == null)
            {
                System.out.printf("Slot %-2d : EMPTY%n", i + 1);
            }
            else
            {
                Ticket ticket = null;

                for (Ticket t : tickets)
                {
                    if (t != null &&
                        t.getVehicle().getNumberPlate().equalsIgnoreCase(
                            vehicle.getNumberPlate()))
                    {
                        ticket = t;
                        break;
                    }
                }

                if (vehicle.getVehicleType() == VehicleType.TRUCK)
                {
                    if (i > 0 && slots[i - 1] == vehicle)
                    {
                        System.out.printf(
                            "Slot %-2d : TRUCK - %s (Continued)%n",
                            i + 1,
                            vehicle.getNumberPlate());
                    }
                    else
                    {
                        System.out.printf(
                            "Slot %-2d : TRUCK - %s%n",
                            i + 1,
                            vehicle.getNumberPlate());

                        if (ticket != null)
                        {
                            System.out.printf(
                                "           Ticket Number : %s%n",
                                ticket.getTicketNumber());
                        }
                    }
                }
                else
                {
                    System.out.printf(
                        "Slot %-2d : %-5s - %s%n",
                        i + 1,
                        vehicle.getVehicleType(),
                        vehicle.getNumberPlate());

                    if (ticket != null)
                    {
                        System.out.printf(
                            "           Ticket Number : %s%n",
                            ticket.getTicketNumber());
                    }
                }
            }
        }

        System.out.println(LINE);
        System.out.printf("Total Parked Vehicles : %d%n",
            getTotalParkedVehicles());
        System.out.printf("Available Slots       : %d / %d%n",
            getAvailableSlots(), MAX_CAPACITY);
        System.out.printf("Total Collection      : Rs. %.2f%n",
            totalCollection);
        System.out.println(LINE);
    }
}

