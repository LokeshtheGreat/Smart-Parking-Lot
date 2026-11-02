public class Bike extends Vehicle implements Chargeable
{
    public Bike(VehicleType vehicleType, String numberPlate)
    {
        super(vehicleType, numberPlate);
    }
    @Override
    public double calculateCharge(int hours)
    {
        return getVehicleType().getHourlyRate() * hours;
    }
}