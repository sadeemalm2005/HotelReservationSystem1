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
 * AdminDashboardController manages admin dashboard
 * Demonstrates: JDBC queries, database statistics
 */
public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label totalGuestsLabel;

    @FXML
    private Label totalRoomsLabel;

    @FXML
    private Label occupiedRoomsLabel;

    @FXML
    private Label activeReservationsLabel;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private Button userManagementButton;

    @FXML
    private Button reportsButton;

    @FXML
    private Button roomTypeManagementButton;

    @FXML
    private Button serviceManagementButton;

    @FXML
    private Button logoutButton;

    private Admin currentAdmin;

    /**
     * Sets the current admin
     */
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        welcomeLabel.setText("Welcome, Administrator " + admin.getUsername());
        loadStatistics();
    }

    /**
     * Loads system statistics
     * Demonstrates: JDBC aggregate queries
     */
    private void loadStatistics() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Count total guests
            String sql1 = "SELECT COUNT(*) FROM GUESTS";
            pstmt = connection.prepareStatement(sql1);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                totalGuestsLabel.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

            // Count total rooms
            String sql2 = "SELECT COUNT(*) FROM ROOMS";
            pstmt = connection.prepareStatement(sql2);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                totalRoomsLabel.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

            // Count occupied rooms
            String sql3 = "SELECT COUNT(*) FROM ROOMS WHERE STATUS = 'OCCUPIED'";
            pstmt = connection.prepareStatement(sql3);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                occupiedRoomsLabel.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

            // Count active reservations
            String sql4 = "SELECT COUNT(*) FROM RESERVATIONS WHERE STATUS IN ('CONFIRMED', 'CHECKED_IN')";
            pstmt = connection.prepareStatement(sql4);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                activeReservationsLabel.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();
            pstmt.close();

            // Calculate total revenue from payments
            String sql5 = "SELECT NVL(SUM(AMOUNT), 0) FROM PAYMENTS";
            pstmt = connection.prepareStatement(sql5);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                totalRevenueLabel.setText(String.format("%.2f", rs.getDouble(1)) + " SAR");
            }

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error loading statistics: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Handles user management button click
     */
    @FXML
    private void handleUserManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserManagement.fxml"));
            Parent root = loader.load();

            UserManagementController controller = loader.getController();
            controller.setCurrentAdmin(currentAdmin);

            Stage stage = (Stage) userManagementButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error opening user management: " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open User Management");
            alert.setContentText("Error: " + e.getMessage() + "\n\nPlease rebuild the project (Shift+F11)");
            alert.showAndWait();
        }
    }

    /**
     * Handles reports button click
     */
    @FXML
    private void handleReports(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Reports.fxml"));
            Parent root = loader.load();

            ReportsController controller = loader.getController();
            controller.setCurrentAdmin(currentAdmin);

            Stage stage = (Stage) reportsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error opening reports: " + e.getMessage());
        }
    }

    /**
     * Handles room type management button click
     */
    @FXML
    private void handleRoomTypeManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RoomTypeManagement.fxml"));
            Parent root = loader.load();

            RoomTypeManagementController controller = loader.getController();
            controller.setCurrentAdmin(currentAdmin);

            Stage stage = (Stage) roomTypeManagementButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error opening room type management: " + e.getMessage());
        }
    }

    /**
     * Handles service management button click
     */
    @FXML
    private void handleServiceManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ServiceManagement.fxml"));
            Parent root = loader.load();

            ServiceManagementController controller = loader.getController();
            controller.setCurrentAdmin(currentAdmin);

            Stage stage = (Stage) serviceManagementButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error opening service management: " + e.getMessage());
        }
    }

    /**
     * Handles logout button click
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
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
