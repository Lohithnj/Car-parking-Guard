package Parking_Project;

import java.sql.*;

public class ParkingLotManager {

    // Initialize parking slots for two-wheelers and four-wheelers
    public void initializeParkingSlots(int twoWheelerSlots, int fourWheelerSlots) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Step 1: Delete all records in BookingHistory and BookingDetails
            String deleteBookingHistoryQuery = "DELETE FROM BookingHistory";
            String deleteBookingDetailsQuery = "DELETE FROM BookingDetails";

            PreparedStatement deleteHistoryStmt = connection.prepareStatement(deleteBookingHistoryQuery);
            PreparedStatement deleteDetailsStmt = connection.prepareStatement(deleteBookingDetailsQuery);
            deleteHistoryStmt.executeUpdate();
            deleteDetailsStmt.executeUpdate();

            // Step 2: Delete all records in ParkingLot_TwoWheeler and ParkingLot_FourWheeler
            String deleteTwoWheelerQuery = "DELETE FROM ParkingLot_TwoWheeler ";
            String deleteFourWheelerQuery = "DELETE FROM ParkingLot_FourWheeler ";

            PreparedStatement deleteTwoWheelerStmt = connection.prepareStatement(deleteTwoWheelerQuery);
            PreparedStatement deleteFourWheelerStmt = connection.prepareStatement(deleteFourWheelerQuery);
            deleteTwoWheelerStmt.executeUpdate();
            deleteFourWheelerStmt.executeUpdate();

            //ALTER TABLE parkinglot_twowheeler AUTO_INCREMENT = 1
            //This step is to Reset the Auto increment to 1,this is because even after you deleted all row the AI value will remains same
            String reset2AItoZer0 = "ALTER TABLE parkinglot_twowheeler AUTO_INCREMENT = 1";
            String reset4AItoZer0= "ALTER TABLE parkinglot_fourwheeler AUTO_INCREMENT = 1";

            PreparedStatement pre1 = connection.prepareStatement(reset2AItoZer0);
            PreparedStatement pre2 = connection.prepareStatement(reset4AItoZer0);
            pre1.executeUpdate();
            pre2.executeUpdate();

            // Step 3: Insert new parking slots for two-wheelers
            String insertTwoWheelerQuery = "INSERT INTO ParkingLot_TwoWheeler (is_occupied) VALUES (FALSE)";
            PreparedStatement insertTwoWheelerStmt = connection.prepareStatement(insertTwoWheelerQuery);
            for (int i = 0; i < twoWheelerSlots; i++) {
                insertTwoWheelerStmt.executeUpdate();
            }

            // Step 4: Insert new parking slots for four-wheelers
            String insertFourWheelerQuery = "INSERT INTO ParkingLot_FourWheeler (is_occupied) VALUES (FALSE)";
            PreparedStatement insertFourWheelerStmt = connection.prepareStatement(insertFourWheelerQuery);
            for (int i = 0; i < fourWheelerSlots; i++) {
                insertFourWheelerStmt.executeUpdate();
            }

            System.out.println("Parking slots initialized: " + twoWheelerSlots + " two-wheelers, " + fourWheelerSlots + " four-wheelers.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Check the availability of parking slots for a given vehicle type
    public void getAvailableSlots() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Count available two-wheeler slots
            String countTwoWheelerQuery = "SELECT COUNT(*) FROM ParkingLot_TwoWheeler WHERE is_occupied = FALSE";
            String countFourWheelerQuery = "SELECT COUNT(*) FROM ParkingLot_FourWheeler WHERE is_occupied = FALSE";

            PreparedStatement countTwoWheelerStmt = connection.prepareStatement(countTwoWheelerQuery);
            PreparedStatement countFourWheelerStmt = connection.prepareStatement(countFourWheelerQuery);

            ResultSet twoWheelerResult = countTwoWheelerStmt.executeQuery();
            ResultSet fourWheelerResult = countFourWheelerStmt.executeQuery();

            if (twoWheelerResult.next()) {
                int twoWheelerCount = twoWheelerResult.getInt(1);
                System.out.println("Available Two-Wheeler Slots: " + twoWheelerCount);
            }

            if (fourWheelerResult.next()) {
                int fourWheelerCount = fourWheelerResult.getInt(1);
                System.out.println("Available Four-Wheeler Slots: " + fourWheelerCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

