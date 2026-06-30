package hotelreservationsystem1.models;

import java.util.Date;

/**
 * Payment class
 * Demonstrates Composition - contains Reservation reference
 */
public class Payment {
    private int paymentId;
    private Reservation reservation;  // Composition - Payment HAS-A Reservation
    private double amount;
    private Date paymentDate;
    private String paymentMethod;

    // Constants for payment methods
    public static final String METHOD_CASH = "CASH";
    public static final String METHOD_CARD = "CARD";
    public static final String METHOD_ONLINE = "ONLINE";

    // Constructors
    public Payment() {
        this.paymentDate = new Date();
    }

    public Payment(Reservation reservation, double amount, String paymentMethod) {
        this.reservation = reservation;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = new Date();
    }

    /**
     * Process payment for the reservation
     * Returns true if payment is successful
     */
    public boolean processPayment() {
        if (reservation == null || amount <= 0) {
            return false;
        }

        // Check if payment method is valid
        if (!isValidPaymentMethod(paymentMethod)) {
            return false;
        }

        // Check if amount matches reservation total
        if (amount < reservation.getTotalPrice()) {
            return false;
        }

        // Payment is valid
        this.paymentDate = new Date();
        return true;
    }

    /**
     * Validate payment method
     */
    private boolean isValidPaymentMethod(String method) {
        return METHOD_CASH.equals(method) ||
               METHOD_CARD.equals(method) ||
               METHOD_ONLINE.equals(method);
    }

    /**
     * Generate payment receipt
     */
    public String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("========== PAYMENT RECEIPT ==========\n");
        receipt.append("Payment ID: ").append(paymentId).append("\n");
        receipt.append("Reservation ID: ").append(reservation != null ? reservation.getReservationId() : "N/A").append("\n");
        receipt.append("Guest: ").append(reservation != null && reservation.getGuest() != null ?
                                        reservation.getGuest().getFullName() : "N/A").append("\n");
        receipt.append("Amount Paid: $").append(String.format("%.2f", amount)).append("\n");
        receipt.append("Payment Method: ").append(paymentMethod).append("\n");
        receipt.append("Payment Date: ").append(paymentDate).append("\n");
        receipt.append("====================================\n");
        return receipt.toString();
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", reservationId=" + (reservation != null ? reservation.getReservationId() : "N/A") +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
