public class Receipt {

    private final String ticketNumber;
    private final Vehicle vehicle;
    private final String numberPlate;
    private final double hours;
    private final int chargeableHours;
    private final double amount;
    private final boolean wasCapped;

    public Receipt(
            String ticketNumber,
            Vehicle vehicle,
            double hours,
            int chargeableHours,
            double amount,
            boolean wasCapped) {

        this.ticketNumber = ticketNumber;
        this.vehicle = vehicle;
        this.numberPlate = vehicle.getNumberPlate();
        this.hours = hours;
        this.chargeableHours = chargeableHours;
        this.amount = amount;
        this.wasCapped = wasCapped;
    }

    public void printReceipt() {

        System.out.println();
        System.out.println("================================================");
        System.out.println("                    PARKEASE");
        System.out.println("                 PARKING RECEIPT");
        System.out.println("================================================");

        System.out.printf("%-24s: %s%n", "Ticket Number", ticketNumber);
        System.out.printf("%-24s: %s%n", "Vehicle Type", vehicle.getVehicleType());
        System.out.printf("%-24s: %s%n", "Number Plate", numberPlate);
        System.out.printf("%-24s: %.1f hours%n", "Parking Duration", hours);
        System.out.printf("%-24s: %d hours%n", "Chargeable Hours", chargeableHours);
        System.out.printf("%-24s: 8 hours%n", "Maximum Billable Hours");
        System.out.printf("%-24s: %s%n", "Cap Applied", wasCapped ? "YES" : "NO");

        System.out.println("------------------------------------------------");
        System.out.println("                 BILLING DETAILS");
        System.out.println("------------------------------------------------");

        double hourlyRate = vehicle.getVehicleType().getHourlyRate();

        System.out.printf("%-24s: Rs. %.2f%n", "Hourly Rate", hourlyRate);
        System.out.printf("%-24s: Rs. %.2f x %d%n",
                "Calculation", hourlyRate, chargeableHours);
        System.out.printf("%-24s: Rs. %.2f%n", "Final Amount", amount);

        System.out.println("================================================");
        System.out.println("             THANK YOU! VISIT AGAIN!");
        System.out.println("================================================");
        System.out.println();
    }
}