package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import hotelreservationsystem1.models.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DatabaseConnection;

/**
 * ReportsController generates system reports
 * Demonstrates: JDBC aggregate queries, reporting logic
 */
public class ReportsController {

    @FXML
    private ComboBox<String> reportTypeComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button generateButton;

    @FXML
    private TextArea reportTextArea;

    @FXML
    private Button backButton;

    private Admin currentAdmin;

    /**
     * Sets the current admin
     */
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
    }

    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        // Set up report types
        reportTypeComboBox.setItems(javafx.collections.FXCollections.observableArrayList(
            "Revenue Report",
            "Occupancy Report",
            "Guest Statistics",
            "Room Status Summary"
        ));
        reportTypeComboBox.setValue("Revenue Report");
    }

    /**
     * Handles generate button click
     */
    @FXML
    private void handleGenerate(ActionEvent event) {
        String reportType = reportTypeComboBox.getValue();

        if (reportType == null) {
            reportTextArea.setText("Please select a report type");
            return;
        }

        switch (reportType) {
            case "Revenue Report":
                generateRevenueReport();
                break;
            case "Occupancy Report":
                generateOccupancyReport();
                break;
            case "Guest Statistics":
                generateGuestStatistics();
                break;
            case "Room Status Summary":
                generateRoomStatusSummary();
                break;
        }
    }

    /**
     * Generates revenue report
     * Demonstrates: JDBC aggregate queries (SUM, COUNT)
     */
    private void generateRevenueReport() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            StringBuilder report = new StringBuilder();
            report.append("=== REVENUE REPORT ===\n\n");

            // Total revenue from payments
            String sql1 = "SELECT NVL(SUM(AMOUNT), 0) as TOTAL, COUNT(*) as COUNT FROM PAYMENTS";
            pstmt = connection.prepareStatement(sql1);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                report.append("Total Revenue: ").append(String.format("%.2f", rs.getDouble("TOTAL"))).append(" SAR\n");
                report.append("Total Payments: ").append(rs.getInt("COUNT")).append("\n\n");
            }
            rs.close();
            pstmt.close();

            // Revenue by month (last 6 months)
            String sql2 = "SELECT TO_CHAR(PAYMENT_DATE, 'YYYY-MM') as MONTH, " +
                    "NVL(SUM(AMOUNT), 0) as REVENUE " +
                    "FROM PAYMENTS " +
                    "WHERE PAYMENT_DATE >= ADD_MONTHS(SYSDATE, -6) " +
                    "GROUP BY TO_CHAR(PAYMENT_DATE, 'YYYY-MM') " +
                    "ORDER BY MONTH DESC";

            pstmt = connection.prepareStatement(sql2);
            rs = pstmt.executeQuery();

            report.append("Monthly Revenue (Last 6 Months):\n");
            report.append("--------------------------------\n");

            while (rs.next()) {
                report.append(rs.getString("MONTH")).append(": ")
                        .append(String.format("%.2f", rs.getDouble("REVENUE"))).append(" SAR\n");
            }

            reportTextArea.setText(report.toString());

        } catch (ClassNotFoundException | SQLException e) {
            reportTextArea.setText("Error generating revenue report: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Generates occupancy report
     * Demonstrates: JDBC queries with date filtering
     */
    private void generateOccupancyReport() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            StringBuilder report = new StringBuilder();
            report.append("=== OCCUPANCY REPORT ===\n\n");

            // Current room status counts
            String sql1 = "SELECT STATUS, COUNT(*) as COUNT FROM ROOMS GROUP BY STATUS";
            pstmt = connection.prepareStatement(sql1);
            rs = pstmt.executeQuery();

            report.append("Current Room Status:\n");
            report.append("--------------------\n");

            int totalRooms = 0;
            int occupiedRooms = 0;

            while (rs.next()) {
                String status = rs.getString("STATUS");
                int count = rs.getInt("COUNT");
                report.append(status).append(": ").append(count).append("\n");
                totalRooms += count;
                if ("OCCUPIED".equals(status)) {
                    occupiedRooms = count;
                }
            }

            double occupancyRate = totalRooms > 0 ? (occupiedRooms * 100.0 / totalRooms) : 0;
            report.append("\nTotal Rooms: ").append(totalRooms).append("\n");
            report.append("Occupancy Rate: ").append(String.format("%.2f", occupancyRate)).append("%\n");

            reportTextArea.setText(report.toString());

        } catch (ClassNotFoundException | SQLException e) {
            reportTextArea.setText("Error generating occupancy report: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Generates guest statistics
     * Demonstrates: JDBC aggregate queries with JOIN
     */
    private void generateGuestStatistics() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            StringBuilder report = new StringBuilder();
            report.append("=== GUEST STATISTICS ===\n\n");

            // Total guests
            String sql1 = "SELECT COUNT(*) as COUNT FROM GUESTS";
            pstmt = connection.prepareStatement(sql1);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                report.append("Total Registered Guests: ").append(rs.getInt("COUNT")).append("\n\n");
            }
            rs.close();
            pstmt.close();

            // Top 5 guests by number of reservations
            String sql2 = "SELECT g.FIRST_NAME, g.LAST_NAME, COUNT(r.RESERVATION_ID) as BOOKINGS " +
                    "FROM GUESTS g " +
                    "LEFT JOIN RESERVATIONS r ON g.GUEST_ID = r.GUEST_ID " +
                    "GROUP BY g.GUEST_ID, g.FIRST_NAME, g.LAST_NAME " +
                    "ORDER BY BOOKINGS DESC " +
                    "FETCH FIRST 5 ROWS ONLY";

            pstmt = connection.prepareStatement(sql2);
            rs = pstmt.executeQuery();

            report.append("Top 5 Guests by Bookings:\n");
            report.append("-------------------------\n");

            int rank = 1;
            while (rs.next()) {
                report.append(rank++).append(". ")
                        .append(rs.getString("FIRST_NAME")).append(" ")
                        .append(rs.getString("LAST_NAME")).append(" - ")
                        .append(rs.getInt("BOOKINGS")).append(" booking(s)\n");
            }

            reportTextArea.setText(report.toString());

        } catch (ClassNotFoundException | SQLException e) {
            reportTextArea.setText("Error generating guest statistics: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Generates room status summary
     * Demonstrates: JDBC GROUP BY queries
     */
    private void generateRoomStatusSummary() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            StringBuilder report = new StringBuilder();
            report.append("=== ROOM STATUS SUMMARY ===\n\n");

            // Room count by type and status
            String sql = "SELECT rt.TYPE_NAME, r.STATUS, COUNT(*) as COUNT " +
                    "FROM ROOMS r " +
                    "JOIN ROOM_TYPES rt ON r.TYPE_ID = rt.TYPE_ID " +
                    "GROUP BY rt.TYPE_NAME, r.STATUS " +
                    "ORDER BY rt.TYPE_NAME, r.STATUS";

            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            String currentType = "";
            while (rs.next()) {
                String type = rs.getString("TYPE_NAME");
                if (!type.equals(currentType)) {
                    if (!currentType.isEmpty()) {
                        report.append("\n");
                    }
                    report.append(type).append(":\n");
                    currentType = type;
                }
                report.append("  ").append(rs.getString("STATUS"))
                        .append(": ").append(rs.getInt("COUNT")).append("\n");
            }

            reportTextArea.setText(report.toString());

        } catch (ClassNotFoundException | SQLException e) {
            reportTextArea.setText("Error generating room status summary: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Handles export button click
     */
    @FXML
    private void handleExport(ActionEvent event) {
        reportTextArea.appendText("\n\n[Export functionality - To be implemented]\n");
        reportTextArea.appendText("Report would be exported to CSV/PDF here.\n");
    }

    /**
     * Handles back button click
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AdminDashboard.fxml"));
            Parent root = loader.load();

            AdminDashboardController controller = loader.getController();
            controller.setCurrentAdmin(currentAdmin);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            reportTextArea.setText("Error going back: " + e.getMessage());
        }
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
