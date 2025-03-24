package Parking_Project;
import java.util.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParkingSlotBooking {

    public void bookParkingSlot(int userId, String userName, String vehicleType, String vehicleNumber, String userEmail, String userPhone) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String availableSlotQuery = "";
            String updateParkingSlotQuery = "";

            if (vehicleType.equalsIgnoreCase("two_wheeler")) {
                availableSlotQuery = "SELECT * FROM ParkingLot_TwoWheeler WHERE is_occupied = FALSE LIMIT 1";
                updateParkingSlotQuery = "UPDATE ParkingLot_TwoWheeler SET is_occupied = TRUE WHERE parking_id = ?";
            } else if (vehicleType.equalsIgnoreCase("four_wheeler")) {
                availableSlotQuery = "SELECT * FROM ParkingLot_FourWheeler WHERE is_occupied = FALSE LIMIT 1";
                updateParkingSlotQuery = "UPDATE ParkingLot_FourWheeler SET is_occupied = TRUE WHERE parking_id = ?";
            } else {
                System.out.println("Invalid vehicle type.");
                return;
            }

            PreparedStatement slotStmt = connection.prepareStatement(availableSlotQuery);
            ResultSet slotResult = slotStmt.executeQuery();

            if (slotResult.next()) {
                int parkingId = slotResult.getInt("parking_id");

                // Generate a 4-digit PassCode
                int passCode = new Random().nextInt(9000) + 1000;

                PreparedStatement updateSlotStmt = connection.prepareStatement(updateParkingSlotQuery);
                updateSlotStmt.setInt(1, parkingId);
                updateSlotStmt.executeUpdate();

                String insertBookingDetailsQuery = "INSERT INTO BookingDetails (user_id, user_name, user_email, user_phone, vehicle_type, vehicle_number, parking_id, pass_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertBookingDetailsStmt = connection.prepareStatement(insertBookingDetailsQuery);
                insertBookingDetailsStmt.setInt(1, userId);
                insertBookingDetailsStmt.setString(2, userName);
                insertBookingDetailsStmt.setString(3, userEmail);
                insertBookingDetailsStmt.setString(4, userPhone);
                insertBookingDetailsStmt.setString(5, vehicleType);
                insertBookingDetailsStmt.setString(6, vehicleNumber);
                insertBookingDetailsStmt.setInt(7, parkingId);
                insertBookingDetailsStmt.setInt(8, passCode);
                insertBookingDetailsStmt.executeUpdate();

                System.out.println("Parking slot booked successfully!");
                System.out.println("Your parking slot ID is: " + parkingId);

                // Send email with PassCode
                EmailService.sendEmail(userEmail, passCode);
            } else {
                System.out.println("No available parking slots for " + vehicleType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeParkingSlot(String vehicleType, int passCode) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String selectBookingDetailsQuery = "SELECT * FROM BookingDetails WHERE vehicle_type = ? AND pass_code = ?";
            PreparedStatement selectBookingStmt = connection.prepareStatement(selectBookingDetailsQuery);
            selectBookingStmt.setString(1, vehicleType);
            selectBookingStmt.setInt(2, passCode);
            ResultSet bookingDetailsResult = selectBookingStmt.executeQuery();

            if (bookingDetailsResult.next()) {
                int userId = bookingDetailsResult.getInt("user_id");
                String userName = bookingDetailsResult.getString("user_name");
                String userEmail = bookingDetailsResult.getString("user_email");
                String userPhone = bookingDetailsResult.getString("user_phone");
                String vehicleTypeString = bookingDetailsResult.getString("vehicle_type");
                String vehicleNumber = bookingDetailsResult.getString("vehicle_number");
                int parkingId = bookingDetailsResult.getInt("parking_id");

                String freedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                String insertBookingHistoryQuery = "INSERT INTO BookingHistory (user_id, user_name, user_email, user_phone, vehicle_type, vehicle_number, parking_id, freed_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertBookingHistoryStmt = connection.prepareStatement(insertBookingHistoryQuery);
                insertBookingHistoryStmt.setInt(1, userId);
                insertBookingHistoryStmt.setString(2, userName);
                insertBookingHistoryStmt.setString(3, userEmail);
                insertBookingHistoryStmt.setString(4, userPhone);
                insertBookingHistoryStmt.setString(5, vehicleTypeString);
                insertBookingHistoryStmt.setString(6, vehicleNumber);
                insertBookingHistoryStmt.setInt(7, parkingId);
                insertBookingHistoryStmt.setString(8, freedTime);
                insertBookingHistoryStmt.executeUpdate();

                String updateParkingSlotQuery = "";
                if (vehicleType.equalsIgnoreCase("two_wheeler")) {
                    updateParkingSlotQuery = "UPDATE ParkingLot_TwoWheeler SET is_occupied = FALSE WHERE parking_id = ?";
                } else if (vehicleType.equalsIgnoreCase("four_wheeler")) {
                    updateParkingSlotQuery = "UPDATE ParkingLot_FourWheeler SET is_occupied = FALSE WHERE parking_id = ?";
                }

                PreparedStatement updateParkingSlotStmt = connection.prepareStatement(updateParkingSlotQuery);
                updateParkingSlotStmt.setInt(1, parkingId);
                updateParkingSlotStmt.executeUpdate();

                String deleteBookingDetailsQuery = "DELETE FROM BookingDetails WHERE vehicle_type = ? AND pass_code = ?";
                PreparedStatement deleteBookingStmt = connection.prepareStatement(deleteBookingDetailsQuery);
                deleteBookingStmt.setString(1, vehicleType);
                deleteBookingStmt.setInt(2, passCode);
                deleteBookingStmt.executeUpdate();

                System.out.println("Parking slot freed, and booking moved to history.");
            } else {
                System.out.println("Invalid PassCode or vehicle type.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View the current parking status and bookings
    public void viewCurrentParking() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Step 1: Fetch current bookings from BookingDetails
            String selectBookingDetailsQuery = "SELECT * FROM BookingDetails";
            PreparedStatement selectBookingDetailsStmt = connection.prepareStatement(selectBookingDetailsQuery);
            ResultSet bookingDetailsResult = selectBookingDetailsStmt.executeQuery();

            System.out.println("===== Current Parking Vehicles (BookingDetails) =====");
            System.out.println("User ID | User Name | Vehicle Type | Vehicle Number | Slot ID");
            while (bookingDetailsResult.next()) {
                int userId = bookingDetailsResult.getInt("user_id");
                String userName = bookingDetailsResult.getString("user_name");
                String vehicleType = bookingDetailsResult.getString("vehicle_type");
                String vehicleNumber = bookingDetailsResult.getString("vehicle_number");
                int parkingId = bookingDetailsResult.getInt("parking_id");

                System.out.println(userId + " | " + userName + " | " + vehicleType + " | " + vehicleNumber + " | " + parkingId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View parking history from BookingHistory
    public void viewParkingHistory() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Fetch historical bookings
            String selectBookingHistoryQuery = "SELECT * FROM BookingHistory";
            PreparedStatement selectBookingHistoryStmt = connection.prepareStatement(selectBookingHistoryQuery);
            ResultSet bookingHistoryResult = selectBookingHistoryStmt.executeQuery();

            System.out.println("===== Parking History =====");
            System.out.println("User ID | User Name | Vehicle Type | Vehicle Number | Slot ID | Freed Time");
            while (bookingHistoryResult.next()) {
                int userId = bookingHistoryResult.getInt("user_id");
                String userName = bookingHistoryResult.getString("user_name");
                String vehicleType = bookingHistoryResult.getString("vehicle_type");
                String vehicleNumber = bookingHistoryResult.getString("vehicle_number");
                int parkingId = bookingHistoryResult.getInt("parking_id");
                String freedTime = bookingHistoryResult.getString("freed_time");

                System.out.println(userId + " | " + userName + " | " + vehicleType + " | " + vehicleNumber + " | " + parkingId + " | " + freedTime);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
