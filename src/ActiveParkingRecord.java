public class ActiveParkingRecord
{
    private final String ticketNumber;
    private final String numberPlate;
    private final VehicleType vehicleType;
    private final int slotNumber;

    public ActiveParkingRecord(
        String ticketNumber,
        String numberPlate,
        VehicleType vehicleType,
        int slotNumber)
    {
        this.ticketNumber = ticketNumber;
        this.numberPlate = numberPlate;
        this.vehicleType = vehicleType;
        this.slotNumber = slotNumber;
    }

    public String getTicketNumber()
    {
        return ticketNumber;
    }

    public String getNumberPlate()
    {
        return numberPlate;
    }

    public VehicleType getVehicleType()
    {
        return vehicleType;
    }

    public int getSlotNumber()
    {
        return slotNumber;
    }
}