package Parking_Project;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ParkingSlotBooking bookingSystem = new ParkingSlotBooking();
        ParkingLotManager lotManager = new ParkingLotManager();

        while (true) {
            System.out.println("\n===== Parking Management System =====");
            System.out.println("1. Initialize Parking Slots");
            System.out.println("2. Check Availability");
            System.out.println("3. Book Parking Slot");
            System.out.println("4. Free Parking Slot");
            System.out.println("5. View Current Parking");
            System.out.println("6. View Booking History");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // Initialize parking slots
                    System.out.print("Enter number of two-wheeler slots: ");
                    int twoWheelerSlots = scanner.nextInt();
                    System.out.print("Enter number of four-wheeler slots: ");
                    int fourWheelerSlots = scanner.nextInt();
                    lotManager.initializeParkingSlots(twoWheelerSlots, fourWheelerSlots);
                    break;

                case 2:
                    // Check availability
                    lotManager.getAvailableSlots();
                    break;

                case 3:
                    // Book a parking slot
                    System.out.print("Enter User ID: ");
                    int userId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter User Name: ");
                    String userName = scanner.nextLine();
                    System.out.print("Enter Vehicle Type (two_wheeler/four_wheeler): ");
                    String vehicleType = scanner.nextLine();
                    System.out.print("Enter Vehicle Number: ");
                    String vehicleNumber = scanner.nextLine();
                    System.out.print("Enter User Email: ");
                    String userEmail = scanner.nextLine();
                    System.out.print("Enter User Phone: ");
                    String userPhone = scanner.nextLine();
                    bookingSystem.bookParkingSlot(userId, userName, vehicleType, vehicleNumber, userEmail, userPhone);
                    break;

                case 4:
                    // Free a parking slot
                    System.out.println("Enter vehicle type (two_wheeler / four_wheeler): ");
                    String vehicle_Type = scanner.next();

                    System.out.println("Enter your PassCode: ");
                    int passCode = scanner.nextInt();

                    bookingSystem.freeParkingSlot(vehicle_Type, passCode);
                    break;

                case 5:
                    // View current parking status
                    bookingSystem.viewCurrentParking();
                    break;

                case 6:
                    // View booking history
                    bookingSystem.viewParkingHistory();
                    break;

                case 7:
                    // Exit the program
                    System.out.println("Exiting Parking Management System...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice! Please try again.");
                    break;
            }
        }
    }
}
