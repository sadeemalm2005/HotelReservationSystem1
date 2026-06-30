package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import hotelreservationsystem1.models.Receptionist;
import hotelreservationsystem1.models.Reservation;
import hotelreservationsystem1.models.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DatabaseConnection;

/**
 * CheckInOutController handles check-in and check-out operations
 * Demonstrates: JDBC operations, Exception handling
 */
public class CheckInOutController {

    @FXML
    private TextField reservationIdField;

    @FXML
    private Label guestNameLabel;

    @FXML
    private Label roomNumberLabel;

    @FXML
    private Label checkInDateLabel;

    @FXML
    private Label checkOutDateLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private Button searchButton;

    @FXML
    private Button checkInButton;

    @FXML
    private Button checkOutButton;

    @FXML
    private Button backButton;

    @FXML
    private TextArea messageArea;

    private Receptionist currentReceptionist;
    private Reservation currentReservation;

    /**
     * Sets the current receptionist
     */
    public void setCurrentReceptionist(Receptionist receptionist) {
        this.currentReceptionist = receptionist;
    }

    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        checkInButton.setDisable(true);
        checkOutButton.setDisable(true);
    }

    /**
     * Handles search button click
     * Demonstrates: JDBC query operations
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String reservationIdText = reservationIdField.getText().trim();

        if (reservationIdText.isEmpty()) {
            messageArea.setText("Please enter a reservation ID");
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdText);
            loadReservation(reservationId);

        } catch (NumberFormatException e) {
            messageArea.setText("Invalid reservation ID. Please enter a number.");
        }
    }

    /**
     * Loads reservation details
     * Demonstrates: JDBC, PreparedStatement
     */
    private void loadReservation(int reservationId) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "SELECT r.RESERVATION_ID, r.CHECK_IN_DATE, r.CHECK_OUT_DATE, " +
                    "r.STATUS, r.TOTAL_PRICE, " +
                    "g.FIRST_NAME, g.LAST_NAME, rm.ROOM_NUMBER " +
                    "FROM RESERVATIONS r " +
                    "JOIN GUESTS g ON r.GUEST_ID = g.GUEST_ID " +
                    "JOIN ROOMS rm ON r.ROOM_ID = rm.ROOM_ID " +
                    "WHERE r.RESERVATION_ID = ?";

            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Display reservation details
                guestNameLabel.setText(rs.getString("FIRST_NAME") + " " + rs.getString("LAST_NAME"));
                roomNumberLabel.setText(rs.getString("ROOM_NUMBER"));
                checkInDateLabel.setText(rs.getDate("CHECK_IN_DATE").toString());
                checkOutDateLabel.setText(rs.getDate("CHECK_OUT_DATE").toString());
                statusLabel.setText(rs.getString("STATUS"));
                totalAmountLabel.setText(String.format("%.2f", rs.getDouble("TOTAL_PRICE")) + " SAR");

                String status = rs.getString("STATUS");

                // Enable appropriate buttons based on status
                checkInButton.setDisable(!"CONFIRMED".equals(status));
                checkOutButton.setDisable(!"CHECKED_IN".equals(status));

                // Create reservation object
                currentReservation = new Reservation();
                currentReservation.setReservationId(reservationId);
                currentReservation.setStatus(status);
                currentReservation.setTotalPrice(rs.getDouble("TOTAL_PRICE"));

                messageArea.setText("Reservation found. Select an action.");

            } else {
                messageArea.setText("Reservation not found.");
                clearDisplay();
            }

        } catch (ClassNotFoundException | SQLException e) {
            messageArea.setText("Error loading reservation: " + e.getMessage());
            clearDisplay();
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Handles check-in button click
     * Demonstrates: JDBC update operations
     */
    @FXML
    private void handleCheckIn(ActionEvent event) {
        if (currentReservation == null) {
            messageArea.setText("Please search for a reservation first");
            return;
        }

        Connection connection = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Update reservation status
            String sql1 = "UPDATE RESERVATIONS SET STATUS = 'CHECKED_IN' WHERE RESERVATION_ID = ?";
            pstmt1 = connection.prepareStatement(sql1);
            pstmt1.setInt(1, currentReservation.getReservationId());

            int rowsAffected = pstmt1.executeUpdate();

            if (rowsAffected > 0) {
                // Update room status to OCCUPIED
                String sql2 = "UPDATE ROOMS SET STATUS = 'OCCUPIED' " +
                        "WHERE ROOM_ID = (SELECT ROOM_ID FROM RESERVATIONS WHERE RESERVATION_ID = ?)";
                pstmt2 = connection.prepareStatement(sql2);
                pstmt2.setInt(1, currentReservation.getReservationId());
                pstmt2.executeUpdate();

                messageArea.setText("Check-in completed successfully!");
                statusLabel.setText("CHECKED_IN");
                checkInButton.setDisable(true);
                checkOutButton.setDisable(false);

            } else {
                messageArea.setText("Failed to complete check-in");
            }

        } catch (ClassNotFoundException | SQLException e) {
            messageArea.setText("Error during check-in: " + e.getMessage());
        } finally {
            closeResources(null, pstmt1, connection);
            try {
                if (pstmt2 != null) pstmt2.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
        }
    }

    /**
     * Handles check-out button click
     * Demonstrates: JDBC update operations, Payment processing
     */
    @FXML
    private void handleCheckOut(ActionEvent event) {
        if (currentReservation == null) {
            messageArea.setText("Please search for a reservation first");
            return;
        }

        // Process payment first
        Payment payment = new Payment();
        payment.setReservation(currentReservation);
        payment.setAmount(currentReservation.getTotalPrice());
        payment.setPaymentMethod("CASH");

        if (payment.processPayment()) {
            // Update database
            performCheckOut();

            // Generate receipt
            String receipt = payment.generateReceipt();
            messageArea.setText("Check-out completed successfully!\n\n" + receipt);

        } else {
            messageArea.setText("Payment processing failed. Check-out cancelled.");
        }
    }

    /**
     * Performs check-out database operations
     * Demonstrates: JDBC update operations
     */
    private void performCheckOut() {
        Connection connection = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Update reservation status
            String sql1 = "UPDATE RESERVATIONS SET STATUS = 'CHECKED_OUT' WHERE RESERVATION_ID = ?";
            pstmt1 = connection.prepareStatement(sql1);
            pstmt1.setInt(1, currentReservation.getReservationId());
            pstmt1.executeUpdate();

            // Update room status to CLEANING
            String sql2 = "UPDATE ROOMS SET STATUS = 'CLEANING' " +
                    "WHERE ROOM_ID = (SELECT ROOM_ID FROM RESERVATIONS WHERE RESERVATION_ID = ?)";
            pstmt2 = connection.prepareStatement(sql2);
            pstmt2.setInt(1, currentReservation.getReservationId());
            pstmt2.executeUpdate();

            // Insert payment record
            String sql3 = "INSERT INTO PAYMENTS (PAYMENT_ID, RESERVATION_ID, AMOUNT, PAYMENT_METHOD, PAYMENT_DATE) " +
                    "VALUES (PAYMENT_SEQ.NEXTVAL, ?, ?, 'CASH', SYSDATE)";
            pstmt3 = connection.prepareStatement(sql3);
            pstmt3.setInt(1, currentReservation.getReservationId());
            pstmt3.setDouble(2, currentReservation.getTotalPrice());
            pstmt3.executeUpdate();

            statusLabel.setText("CHECKED_OUT");
            checkInButton.setDisable(true);
            checkOutButton.setDisable(true);

        } catch (ClassNotFoundException | SQLException e) {
            messageArea.setText("Error during check-out: " + e.getMessage());
        } finally {
            closeResources(null, pstmt1, connection);
            try {
                if (pstmt2 != null) pstmt2.close();
                if (pstmt3 != null) pstmt3.close();
            } catch (SQLException e) {
                System.err.println("Error closing statements: " + e.getMessage());
            }
        }
    }

    /**
     * Handles back button click
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ReceptionistDashboard.fxml"));
            Parent root = loader.load();

            ReceptionistDashboardController controller = loader.getController();
            controller.setCurrentReceptionist(currentReceptionist);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            messageArea.setText("Error going back: " + e.getMessage());
        }
    }

    /**
     * Clears the display
     */
    private void clearDisplay() {
        guestNameLabel.setText("-");
        roomNumberLabel.setText("-");
        checkInDateLabel.setText("-");
        checkOutDateLabel.setText("-");
        statusLabel.setText("-");
        totalAmountLabel.setText("-");
        checkInButton.setDisable(true);
        checkOutButton.setDisable(true);
        currentReservation = null;
    }

    /**
     * Helper method to close database resources
     */
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection connection) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (connection != null) DatabaseConnection.closeConnection(connection);
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}