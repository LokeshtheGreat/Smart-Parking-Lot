public class Truck extends Vehicle implements Chargeable
{
    public Truck(VehicleType vehicleType, String numberPlate)
    {
        super(vehicleType, numberPlate);
    }
    @Override
    public double calculateCharge(int hours)
    {
        return getVehicleType().getHourlyRate() * hours;
    }
}