public class Ticket
{
    private final String ticketNumber;
    private final Vehicle vehicle;
    private final int slotNumber;

    public Ticket(Vehicle vehicle, int slotNumber, String ticketNumber)
    {
        this.ticketNumber = ticketNumber;
        this.vehicle = vehicle;
        this.slotNumber = slotNumber;
    }

    public String getTicketNumber()
    {
        return ticketNumber;
    }

    public Vehicle getVehicle()
    {
        return vehicle;
    }

    public int getSlotNumber()
    {
        return slotNumber;
    }
}