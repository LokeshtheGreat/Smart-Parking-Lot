public abstract class Vehicle implements Chargeable
{
    private final VehicleType vehicleType;
    private final String numberPlate;
    public Vehicle(VehicleType vehicleType, String numberPlate)
    {
        this.vehicleType = vehicleType;
        this.numberPlate = numberPlate;
    }
    public VehicleType getVehicleType()
    {
        return vehicleType;
    }
    public String getNumberPlate()
    {
        return numberPlate;
    }
    @Override
    public abstract double calculateCharge(int hours);
}