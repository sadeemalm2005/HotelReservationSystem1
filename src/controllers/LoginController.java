package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import hotelreservationsystem1.models.User;
import hotelreservationsystem1.models.Guest;
import hotelreservationsystem1.models.Receptionist;
import hotelreservationsystem1.models.Admin;
import hotelreservationsystem1.exceptions.AuthenticationException;
import hotelreservationsystem1.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DatabaseConnection;

/**
 * LoginController handles user authentication
 * Demonstrates: JDBC, PreparedStatement, Exception Handling, Polymorphism
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    /**
     * Handles login button click
     * Demonstrates JDBC connection and PreparedStatement usage
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Input validation
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password");
            return;
        }

        try {
            // Authenticate user
            User user = authenticateUser(username, password);

            if (user != null) {
                // Successful login - redirect based on role
                redirectToDashboard(user);
            } else {
                throw new AuthenticationException("Invalid username or password");
            }

        } catch (AuthenticationException e) {
            errorLabel.setText(e.getMessage());
        } catch (DatabaseException e) {
            errorLabel.setText("Database error: " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Authenticates user against database
     * Demonstrates: JDBC, PreparedStatement, ResultSet processing
     *
     * @param username the username
     * @param password the password
     * @return User object if authentication successful, null otherwise
     * @throws DatabaseException if database error occurs
     */
    private User authenticateUser(String username, String password) throws DatabaseException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            // Step 1: Get database connection using JDBC
            connection = DatabaseConnection.getConnection();

            // Step 2: Prepare SQL statement (PreparedStatement prevents SQL injection)
            String sql = "SELECT u.USER_ID, u.USERNAME, u.EMAIL, u.PHONE, u.ROLE, u.CREATED_DATE, " +
                    "g.GUEST_ID, g.FIRST_NAME, g.LAST_NAME, g.ID_NUMBER, g.ADDRESS " +
                    "FROM USERS u " +
                    "LEFT JOIN GUESTS g ON u.USER_ID = g.USER_ID " +
                    "WHERE u.USERNAME = ? AND u.PASSWORD = ?";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            // Step 3: Execute query
            rs = pstmt.executeQuery();

            // Step 4: Process ResultSet
            if (rs.next()) {
                String role = rs.getString("ROLE");

                // Polymorphism - create appropriate User subclass based on role
                if ("GUEST".equals(role)) {
                    Guest guest = new Guest();
                    guest.setUserId(rs.getInt("USER_ID"));
                    guest.setUsername(rs.getString("USERNAME"));
                    guest.setEmail(rs.getString("EMAIL"));
                    guest.setPhone(rs.getString("PHONE"));
                    guest.setGuestId(rs.getInt("GUEST_ID"));
                    guest.setFirstName(rs.getString("FIRST_NAME"));
                    guest.setLastName(rs.getString("LAST_NAME"));
                    guest.setIdNumber(rs.getString("ID_NUMBER"));
                    guest.setAddress(rs.getString("ADDRESS"));
                    user = guest;

                } else if ("RECEPTIONIST".equals(role)) {
                    Receptionist receptionist = new Receptionist();
                    receptionist.setUserId(rs.getInt("USER_ID"));
                    receptionist.setUsername(rs.getString("USERNAME"));
                    receptionist.setEmail(rs.getString("EMAIL"));
                    receptionist.setPhone(rs.getString("PHONE"));
                    user = receptionist;

                } else if ("ADMIN".equals(role)) {
                    Admin admin = new Admin();
                    admin.setUserId(rs.getInt("USER_ID"));
                    admin.setUsername(rs.getString("USERNAME"));
                    admin.setEmail(rs.getString("EMAIL"));
                    admin.setPhone(rs.getString("PHONE"));
                    user = admin;
                }
            }

        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Database driver not found: " + e.getMessage());
        } catch (SQLException e) {
            throw new DatabaseException("Database error during authentication: " + e.getMessage());
        } finally {
            // Always close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (connection != null) DatabaseConnection.closeConnection(connection);
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return user;
    }

    /**
     * Redirects user to appropriate dashboard based on role
     * Demonstrates polymorphism - calling abstract method getRole()
     */
    private void redirectToDashboard(User user) {
        try {
            String fxmlFile = "";
            String role = user.getRole(); // Polymorphism - calls overridden method

            // Determine which dashboard to load
            switch (role) {
                case "GUEST":
                    fxmlFile = "/views/GuestDashboard.fxml";
                    break;
                case "RECEPTIONIST":
                    fxmlFile = "/views/ReceptionistDashboard.fxml";
                    break;
                case "ADMIN":
                    fxmlFile = "/views/AdminDashboard.fxml";
                    break;
            }

            // Load new scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Pass the user object to the appropriate controller
            switch (role) {
                case "GUEST":
                    GuestDashboardController guestController = loader.getController();
                    guestController.setCurrentGuest((Guest) user);
                    break;
                case "RECEPTIONIST":
                    ReceptionistDashboardController receptionistController = loader.getController();
                    receptionistController.setCurrentReceptionist((Receptionist) user);
                    break;
                case "ADMIN":
                    AdminDashboardController adminController = loader.getController();
                    adminController.setCurrentAdmin((Admin) user);
                    break;
            }

            // Get current stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            errorLabel.setText("Error loading dashboard: " + e.getMessage());
        }
    }

    /**
     * Clears error message when user starts typing
     */
    @FXML
    private void initialize() {
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorLabel.setText("");
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            errorLabel.setText("");
        });
    }
}
