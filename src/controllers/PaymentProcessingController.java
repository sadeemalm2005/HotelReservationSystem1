package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import hotelreservationsystem1.models.Payment;
import hotelreservationsystem1.models.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DatabaseConnection;

/**
 * PaymentProcessingController handles payment processing
 * Demonstrates: JDBC operations, Payment class usage
 * Note: This is a standalone payment processing screen (not heavily used in current flow)
 */
public class PaymentProcessingController {

    @FXML
    private TextField reservationIdField;

    @FXML
    private Label guestNameLabel;

    @FXML
    private Label totalAmountLabel;

    @FXML
    private ComboBox<String> paymentMethodComboBox;

    @FXML
    private TextField amountPaidField;

    @FXML
    private Button searchButton;

    @FXML
    private Button processButton;

    @FXML
    private TextArea receiptArea;

    private Reservation currentReservation;

    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        // Set up payment methods
        paymentMethodComboBox.setItems(javafx.collections.FXCollections.observableArrayList(
            "CASH", "CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER"
        ));
        paymentMethodComboBox.setValue("CASH");

        processButton.setDisable(true);
    }

    /**
     * Handles search button click
     * Demonstrates: JDBC query
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String reservationIdText = reservationIdField.getText().trim();

        if (reservationIdText.isEmpty()) {
            receiptArea.setText("Please enter a reservation ID");
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdText);
            loadReservation(reservationId);

        } catch (NumberFormatException e) {
            receiptArea.setText("Invalid reservation ID");
        }
    }

    /**
     * Loads reservation details
     * Demonstrates: JDBC PreparedStatement
     */
    private void loadReservation(int reservationId) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "SELECT r.RESERVATION_ID, r.TOTAL_PRICE, r.STATUS, " +
                    "g.FIRST_NAME, g.LAST_NAME " +
                    "FROM RESERVATIONS r " +
                    "JOIN GUESTS g ON r.GUEST_ID = g.GUEST_ID " +
                    "WHERE r.RESERVATION_ID = ?";

            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                guestNameLabel.setText(rs.getString("FIRST_NAME") + " " + rs.getString("LAST_NAME"));
                double totalAmount = rs.getDouble("TOTAL_PRICE");
                totalAmountLabel.setText(String.format("%.2f", totalAmount) + " SAR");
                amountPaidField.setText(String.valueOf(totalAmount));

                // Create reservation object
                currentReservation = new Reservation();
                currentReservation.setReservationId(reservationId);
                currentReservation.setTotalPrice(totalAmount);
                currentReservation.setStatus(rs.getString("STATUS"));

                processButton.setDisable(false);
                receiptArea.setText("Reservation found. Enter payment details.");

            } else {
                receiptArea.setText("Reservation not found");
                clearDisplay();
            }

        } catch (ClassNotFoundException | SQLException e) {
            receiptArea.setText("Error loading reservation: " + e.getMessage());
            clearDisplay();
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Handles process button click
     * Demonstrates: Payment class usage with processPayment() and generateReceipt()
     */
    @FXML
    private void handleProcess(ActionEvent event) {
        if (currentReservation == null) {
            receiptArea.setText("Please search for a reservation first");
            return;
        }

        try {
            double amountPaid = Double.parseDouble(amountPaidField.getText());

            if (amountPaid < currentReservation.getTotalPrice()) {
                receiptArea.setText("Insufficient payment amount");
                return;
            }

            // Create Payment object
            Payment payment = new Payment();
            payment.setReservation(currentReservation);
            payment.setAmount(amountPaid);
            payment.setPaymentMethod(paymentMethodComboBox.getValue());

            // Process payment using Payment class method
            if (payment.processPayment()) {
                // Save payment to database
                if (savePayment(payment)) {
                    // Generate receipt using Payment class method
                    String receipt = payment.generateReceipt();
                    receiptArea.setText("Payment processed successfully!\n\n" + receipt);

                    processButton.setDisable(true);
                } else {
                    receiptArea.setText("Payment validated but failed to save to database");
                }
            } else {
                receiptArea.setText("Payment validation failed");
            }

        } catch (NumberFormatException e) {
            receiptArea.setText("Invalid amount entered");
        }
    }

    /**
     * Saves payment to database
     * Demonstrates: JDBC INSERT operations
     */
    private boolean savePayment(Payment payment) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "INSERT INTO PAYMENTS (PAYMENT_ID, RESERVATION_ID, AMOUNT, PAYMENT_METHOD, PAYMENT_DATE) " +
                    "VALUES (PAYMENT_SEQ.NEXTVAL, ?, ?, ?, SYSDATE)";

            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, currentReservation.getReservationId());
            pstmt.setDouble(2, payment.getAmount());
            pstmt.setString(3, payment.getPaymentMethod());

            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (ClassNotFoundException | SQLException e) {
            receiptArea.setText("Error saving payment: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, connection);
        }
    }

    /**
     * Clears the display
     */
    private void clearDisplay() {
        guestNameLabel.setText("-");
        totalAmountLabel.setText("-");
        amountPaidField.clear();
        processButton.setDisable(true);
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
