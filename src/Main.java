import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try (Scanner sc = new Scanner(System.in)) {

            ParkingLot parkingLot;

            // Initialize parking lot and restore saved state
            try {
                parkingLot = new ParkingLot();

                System.out.println();
                System.out.println("Parking state restored successfully.");
            }
            catch (SQLException e) {
                System.err.println();
                System.err.println("Unable to initialize ParkEase.");
                System.err.println("Reason : " + e.getMessage());
                return;
            }

            while (true) {

                System.out.println();
                System.out.println("================================================");
                System.out.println("                    PARKEASE");
                System.out.println("               SMART PARKING SYSTEM");
                System.out.println("================================================");
                System.out.println("  1. Park Vehicle");
                System.out.println("  2. Exit Vehicle");
                System.out.println("  3. Display Parking Status");
                System.out.println("  4. Exit Application");
                System.out.println("------------------------------------------------");
                System.out.print("  Enter your choice : ");

                int choice;

                try {
                    choice = Integer.parseInt(sc.nextLine().trim());
                }
                catch (NumberFormatException e) {
                    System.out.println();
                    System.out.println("Invalid input. Please enter a number from 1 to 4.");
                    continue;
                }

                switch (choice) {

                    // PARK VEHICLE
                    case 1: {

                        System.out.println();
                        System.out.println("------------------------------------------------");
                        System.out.println("                  PARK VEHICLE");
                        System.out.println("------------------------------------------------");

                        System.out.printf("%-42s : ", "  Enter vehicle type (BIKE / CAR / TRUCK)");
                        String type = sc.nextLine().trim();

                        System.out.printf("%-42s : ", "  Enter number plate");
                        String numberPlate = sc.nextLine().trim().toUpperCase();

                        if (numberPlate.isEmpty()) {
                            System.out.println();
                            System.out.println("Number plate cannot be empty.");
                            break;
                        }

                        Vehicle vehicle;

                        if (type.equalsIgnoreCase("BIKE")) {
                            vehicle = new Bike(VehicleType.BIKE, numberPlate);
                        }
                        else if (type.equalsIgnoreCase("CAR")) {
                            vehicle = new Car(VehicleType.CAR, numberPlate);
                        }
                        else if (type.equalsIgnoreCase("TRUCK")) {
                            vehicle = new Truck(VehicleType.TRUCK, numberPlate);
                        }
                        else {
                            System.out.println();
                            System.out.println("Invalid vehicle type. Please try again.");
                            break;
                        }

                        try {
                            Ticket ticket = parkingLot.parkVehicle(vehicle);

                            System.out.println();
                            System.out.println("Vehicle parked successfully!");
                            System.out.println("Ticket Number : " + ticket.getTicketNumber());
                            System.out.println("Starting Slot : " + ticket.getSlotNumber());
                        }
                        catch (ParkingFullException | DuplicateVehicleException e) {
                            System.out.println();
                            System.out.println(e.getMessage());
                        }
                        catch (IllegalStateException e) {
                            System.out.println();
                            System.out.println("Parking failed : " + e.getMessage());
                        }

                        break;
                    }

                    // EXIT VEHICLE
                    case 2: {

                        System.out.println();
                        System.out.println("------------------------------------------------");
                        System.out.println("                  EXIT VEHICLE");
                        System.out.println("------------------------------------------------");

                        System.out.print("  Enter ticket number             : ");
                        String ticketNumber = sc.nextLine().trim();

                        System.out.print("  Enter parking duration (hours)  : ");

                        double hours;

                        try {
                            hours = Double.parseDouble(sc.nextLine().trim());
                        }
                        catch (NumberFormatException e) {
                            System.out.println();
                            System.out.println("Invalid parking duration. Please enter a valid number.");
                            break;
                        }

                        Receipt receipt = parkingLot.exitVehicle(ticketNumber, hours);

                        if (receipt != null) {
                            receipt.printReceipt();
                        }
                        else {
                            System.out.println();
                            System.out.println("Vehicle exit failed.");
                            System.out.println("Please check the ticket number, duration, and database connection.");
                        }

                        break;
                    }

                    // DISPLAY PARKING STATUS
                    case 3:

                        System.out.println();
                        parkingLot.displayParkingStatus();
                        break;

                    // EXIT APPLICATION
                    case 4:

                        System.out.println();
                        System.out.println("================================================");
                        System.out.println("          THANK YOU FOR USING PARKEASE!");
                        System.out.println("================================================");
                        return;

                    // INVALID OPTION
                    default:

                        System.out.println();
                        System.out.println("Invalid option. Please enter a number from 1 to 4.");
                }
            }
        }
    }
}