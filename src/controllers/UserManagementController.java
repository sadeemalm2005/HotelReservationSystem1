package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import hotelreservationsystem1.models.Admin;
import hotelreservationsystem1.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import database.DatabaseConnection;

/**
 * UserManagementController manages system users
 * Demonstrates: JDBC CRUD operations, Collections (HashSet for uniqueness)
 */
public class UserManagementController {

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Integer> userIdColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    // Removed: roleFilterComboBox - no longer needed
    // @FXML
    // private ComboBox<String> roleFilterComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    @FXML
    private TextArea infoArea;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    private Admin currentAdmin;
    private User selectedUser;

    // Demonstrates HashSet - to store unique usernames (prevent duplicates)
    private HashSet<String> existingUsernames;

    /**
     * Sets the current admin
     */
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        loadUsers();
    }

    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        // Set up table columns
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Initialize HashSet for unique usernames
        existingUsernames = new HashSet<>();

        // Listen for table selection - populate form fields
        usersTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                selectedUser = newValue;
                deleteButton.setDisable(newValue == null);
                updateButton.setDisable(newValue == null);
                populateForm(newValue);
            }
        );

        deleteButton.setDisable(true);
        updateButton.setDisable(true);
    }

    /**
     * Populates form fields with selected user data
     */
    private void populateForm(User user) {
        if (user == null) {
            // Clear form
            usernameField.clear();
            passwordField.clear();
            firstNameField.clear();
            lastNameField.clear();
            emailField.clear();
            phoneField.clear();
        } else {
            // Fill form with user data
            usernameField.setText(user.getUsername());
            passwordField.setText(""); // Don't show password

            // Show first and last name only for GUEST users
            if (user instanceof hotelreservationsystem1.models.Guest) {
                hotelreservationsystem1.models.Guest guest = (hotelreservationsystem1.models.Guest) user;
                firstNameField.setText(guest.getFirstName() != null ? guest.getFirstName() : "");
                lastNameField.setText(guest.getLastName() != null ? guest.getLastName() : "");
            } else {
                // Clear name fields for Admin and Receptionist users
                firstNameField.clear();
                lastNameField.clear();
            }

            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
        }
    }

    /**
     * Loads users from database
     * Demonstrates: JDBC, Collections (ArrayList, HashSet)
     */
    private void loadUsers() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Build SQL query - Only load GUEST users with their names
            String sql = "SELECT u.USER_ID, u.USERNAME, u.EMAIL, u.PHONE, u.ROLE, " +
                    "g.FIRST_NAME, g.LAST_NAME " +
                    "FROM USERS u " +
                    "INNER JOIN GUESTS g ON u.USER_ID = g.USER_ID " +
                    "WHERE u.ROLE = 'GUEST' " +
                    "ORDER BY u.USER_ID";

            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            // Use ArrayList to collect users
            ArrayList<User> usersList = new ArrayList<>();

            // Clear existing usernames HashSet
            existingUsernames.clear();

            while (rs.next()) {
                // Create appropriate User subclass based on role
                String role = rs.getString("ROLE");
                User user = null;

                if ("GUEST".equals(role)) {
                    hotelreservationsystem1.models.Guest guest = new hotelreservationsystem1.models.Guest();
                    guest.setFirstName(rs.getString("FIRST_NAME"));
                    guest.setLastName(rs.getString("LAST_NAME"));
                    user = guest;
                } else if ("RECEPTIONIST".equals(role)) {
                    user = new hotelreservationsystem1.models.Receptionist();
                } else if ("ADMIN".equals(role)) {
                    user = new hotelreservationsystem1.models.Admin();
                }

                if (user != null) {
                    user.setUserId(rs.getInt("USER_ID"));
                    user.setUsername(rs.getString("USERNAME"));
                    user.setEmail(rs.getString("EMAIL"));
                    user.setPhone(rs.getString("PHONE"));

                    usersList.add(user);

                    // Add username to HashSet (demonstrates Set uniqueness)
                    existingUsernames.add(rs.getString("USERNAME"));
                }
            }

            // Convert to ObservableList
            ObservableList<User> observableUsers = FXCollections.observableArrayList(usersList);
            usersTable.setItems(observableUsers);

            infoArea.setText("Loaded " + usersList.size() + " user(s)\n" +
                    "Unique usernames: " + existingUsernames.size());

        } catch (ClassNotFoundException | SQLException e) {
            infoArea.setText("Error loading users: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Handles search button click
     * Demonstrates: JDBC query with LIKE
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            loadUsers();
            return;
        }

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Search only GUEST users with their names
            String sql = "SELECT u.USER_ID, u.USERNAME, u.EMAIL, u.PHONE, u.ROLE, " +
                    "g.FIRST_NAME, g.LAST_NAME " +
                    "FROM USERS u " +
                    "INNER JOIN GUESTS g ON u.USER_ID = g.USER_ID " +
                    "WHERE u.ROLE = 'GUEST' AND " +
                    "(LOWER(u.USERNAME) LIKE ? OR LOWER(u.EMAIL) LIKE ? OR " +
                    "LOWER(g.FIRST_NAME) LIKE ? OR LOWER(g.LAST_NAME) LIKE ?) " +
                    "ORDER BY u.USER_ID";

            pstmt = connection.prepareStatement(sql);
            String searchPattern = "%" + searchText.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            rs = pstmt.executeQuery();

            ArrayList<User> usersList = new ArrayList<>();

            while (rs.next()) {
                String role = rs.getString("ROLE");

                if ("GUEST".equals(role)) {
                    hotelreservationsystem1.models.Guest guest = new hotelreservationsystem1.models.Guest();
                    guest.setUserId(rs.getInt("USER_ID"));
                    guest.setUsername(rs.getString("USERNAME"));
                    guest.setEmail(rs.getString("EMAIL"));
                    guest.setPhone(rs.getString("PHONE"));
                    guest.setFirstName(rs.getString("FIRST_NAME"));
                    guest.setLastName(rs.getString("LAST_NAME"));

                    usersList.add(guest);

                    // Add username to HashSet for consistency
                    existingUsernames.add(rs.getString("USERNAME"));
                }
            }

            ObservableList<User> observableUsers = FXCollections.observableArrayList(usersList);
            usersTable.setItems(observableUsers);

            infoArea.setText("Found " + usersList.size() + " guest(s) matching '" + searchText + "'");

        } catch (ClassNotFoundException | SQLException e) {
            infoArea.setText("Error searching users: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Handles add button click
     * Demonstrates: JDBC insert operations
     */
    @FXML
    private void handleAdd(ActionEvent event) {
        // Validate form inputs
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = "GUEST"; // All users are GUEST users

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            infoArea.setText("Please fill in all required fields (username, password, email)");
            return;
        }

        // First and last names are required
        if (firstName.isEmpty() || lastName.isEmpty()) {
            infoArea.setText("First name and last name are required");
            return;
        }

        // Check if username already exists using HashSet
        if (existingUsernames.contains(username)) {
            infoArea.setText("Username '" + username + "' already exists. Please choose a different username.");
            return;
        }

        // Validate email format (basic check)
        if (!email.contains("@") || !email.contains(".")) {
            infoArea.setText("Please enter a valid email address");
            return;
        }

        // Confirm addition
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Add User");
        confirmAlert.setHeaderText("Add new user " + username + "?");
        confirmAlert.setContentText("Role: " + role + "\nEmail: " + email);

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            addUser(username, password, firstName, lastName, email, phone, role);
        }
    }

    /**
     * Handles update button click
     * Demonstrates: JDBC update operations
     */
    @FXML
    private void handleUpdate(ActionEvent event) {
        if (selectedUser == null) {
            infoArea.setText("Please select a user to update");
            return;
        }

        // Validate form inputs
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = "GUEST"; // All users are GUEST users

        if (username.isEmpty() || email.isEmpty()) {
            infoArea.setText("Please fill in all required fields (username, email)");
            return;
        }

        // First and last names are required
        if (firstName.isEmpty() || lastName.isEmpty()) {
            infoArea.setText("First name and last name are required");
            return;
        }

        // Check if username changed and already exists
        if (!username.equals(selectedUser.getUsername()) && existingUsernames.contains(username)) {
            infoArea.setText("Username '" + username + "' already exists. Please choose a different username.");
            return;
        }

        // Confirm update
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Update User");
        confirmAlert.setHeaderText("Update user " + selectedUser.getUsername() + "?");
        confirmAlert.setContentText("This will update the user's information.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateUser(selectedUser.getUserId(), username, password, firstName, lastName, email, phone, role);
        }
    }

    /**
     * Handles delete button click
     * Demonstrates: JDBC delete operations
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedUser == null) {
            infoArea.setText("Please select a user to delete");
            return;
        }

        // Prevent admin from deleting themselves
        if (selectedUser.getUserId() == currentAdmin.getUserId()) {
            infoArea.setText("You cannot delete your own account");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete User");
        confirmAlert.setHeaderText("Delete user " + selectedUser.getUsername() + "?");
        confirmAlert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteUser(selectedUser.getUserId());
        }
    }

    /**
     * Adds a new user to the database
     * Demonstrates: JDBC insert operations, using sequences for ID generation, transactions
     */
    private void addUser(String username, String password, String firstName, String lastName, String email, String phone, String role) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Insert new user - USER_ID will be auto-generated by the sequence
            String sql = "INSERT INTO USERS (USER_ID, USERNAME, PASSWORD, EMAIL, PHONE, ROLE, CREATED_DATE) " +
                         "VALUES (user_seq.NEXTVAL, ?, ?, ?, ?, ?, SYSDATE)";

            pstmt = connection.prepareStatement(sql, new String[]{"USER_ID"});
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, role);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get the generated USER_ID
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int newUserId = rs.getInt(1);

                    // Insert into GUESTS table (only for GUEST role)
                    if ("GUEST".equals(role)) {
                        String guestSql = "INSERT INTO GUESTS (GUEST_ID, USER_ID, FIRST_NAME, LAST_NAME) " +
                                         "VALUES (guest_seq.NEXTVAL, ?, ?, ?)";
                        pstmt2 = connection.prepareStatement(guestSql);
                        pstmt2.setInt(1, newUserId);
                        pstmt2.setString(2, firstName);
                        pstmt2.setString(3, lastName);
                        pstmt2.executeUpdate();
                    }
                    // ADMIN and RECEPTIONIST don't need separate tables
                }

                connection.commit(); // Commit transaction
                infoArea.setText("User '" + username + "' added successfully");
                populateForm(null); // Clear form
                loadUsers();  // Reload users
            } else {
                connection.rollback();
                infoArea.setText("Failed to add user");
            }

        } catch (ClassNotFoundException | SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback error: " + ex.getMessage());
            }
            infoArea.setText("Error adding user: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt2 != null) pstmt2.close();
                if (pstmt != null) pstmt.close();
                if (connection != null) {
                    connection.setAutoCommit(true);
                    DatabaseConnection.closeConnection(connection);
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Updates a user in the database
     * Demonstrates: JDBC update operations, transactions
     */
    private void updateUser(int userId, String username, String password, String firstName, String lastName, String email, String phone, String role) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Build UPDATE query - update password only if provided
            String sql;
            if (password.isEmpty()) {
                sql = "UPDATE USERS SET USERNAME = ?, EMAIL = ?, PHONE = ?, ROLE = ? WHERE USER_ID = ?";
            } else {
                sql = "UPDATE USERS SET USERNAME = ?, PASSWORD = ?, EMAIL = ?, PHONE = ?, ROLE = ? WHERE USER_ID = ?";
            }

            pstmt = connection.prepareStatement(sql);

            if (password.isEmpty()) {
                pstmt.setString(1, username);
                pstmt.setString(2, email);
                pstmt.setString(3, phone);
                pstmt.setString(4, role);
                pstmt.setInt(5, userId);
            } else {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, email);
                pstmt.setString(4, phone);
                pstmt.setString(5, role);
                pstmt.setInt(6, userId);
            }

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update GUESTS table only for GUEST role
                if ("GUEST".equals(role)) {
                    // Check if guest record exists
                    String checkSql = "SELECT GUEST_ID FROM GUESTS WHERE USER_ID = ?";
                    pstmt2 = connection.prepareStatement(checkSql);
                    pstmt2.setInt(1, userId);
                    ResultSet rs = pstmt2.executeQuery();

                    if (rs.next()) {
                        // Update existing guest record
                        rs.close();
                        pstmt2.close();
                        String updateSql = "UPDATE GUESTS SET FIRST_NAME = ?, LAST_NAME = ? WHERE USER_ID = ?";
                        pstmt2 = connection.prepareStatement(updateSql);
                        pstmt2.setString(1, firstName);
                        pstmt2.setString(2, lastName);
                        pstmt2.setInt(3, userId);
                        pstmt2.executeUpdate();
                    } else {
                        // Insert new guest record if it doesn't exist
                        rs.close();
                        pstmt2.close();
                        String insertSql = "INSERT INTO GUESTS (GUEST_ID, USER_ID, FIRST_NAME, LAST_NAME) " +
                                          "VALUES (guest_seq.NEXTVAL, ?, ?, ?)";
                        pstmt2 = connection.prepareStatement(insertSql);
                        pstmt2.setInt(1, userId);
                        pstmt2.setString(2, firstName);
                        pstmt2.setString(3, lastName);
                        pstmt2.executeUpdate();
                    }
                }
                // ADMIN and RECEPTIONIST don't need separate tables

                connection.commit(); // Commit transaction
                infoArea.setText("User updated successfully");
                populateForm(null); // Clear form
                loadUsers();  // Reload users
            } else {
                connection.rollback();
                infoArea.setText("Failed to update user");
            }

        } catch (ClassNotFoundException | SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback error: " + ex.getMessage());
            }
            infoArea.setText("Error updating user: " + e.getMessage());
        } finally {
            try {
                if (pstmt2 != null) pstmt2.close();
                if (pstmt != null) pstmt.close();
                if (connection != null) {
                    connection.setAutoCommit(true);
                    DatabaseConnection.closeConnection(connection);
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Deletes a user from database
     * Demonstrates: JDBC delete operations
     */
    private void deleteUser(int userId) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "DELETE FROM USERS WHERE USER_ID = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                infoArea.setText("User deleted successfully");
                loadUsers();  // Reload users
            } else {
                infoArea.setText("Failed to delete user");
            }

        } catch (ClassNotFoundException | SQLException e) {
            infoArea.setText("Error deleting user: " + e.getMessage());
        } finally {
            closeResources(null, pstmt, connection);
        }
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
            infoArea.setText("Error going back: " + e.getMessage());
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
