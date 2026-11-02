public class Car extends Vehicle implements Chargeable
{
    public Car(VehicleType vehicleType, String numberPlate)
    {
        super(vehicleType, numberPlate);
    }
    @Override
    public double calculateCharge(int hours)
    {
        return getVehicleType().getHourlyRate() * hours;
    }
}