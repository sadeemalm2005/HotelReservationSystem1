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
import hotelreservationsystem1.models.RoomType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import database.DatabaseConnection;

/**
 * RoomTypeManagementController manages room types
 * Demonstrates: JDBC CRUD operations, Comparable interface usage
 */
public class RoomTypeManagementController {

    @FXML
    private TableView<RoomType> roomTypesTable;

    @FXML
    private TableColumn<RoomType, Integer> typeIdColumn;

    @FXML
    private TableColumn<RoomType, String> typeNameColumn;

    @FXML
    private TableColumn<RoomType, Double> basePriceColumn;

    @FXML
    private TableColumn<RoomType, Integer> capacityColumn;

    @FXML
    private TableColumn<RoomType, String> descriptionColumn;

    @FXML
    private TextField typeNameField;

    @FXML
    private TextField basePriceField;

    @FXML
    private TextField capacityField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    @FXML
    private Label messageLabel;

    private Admin currentAdmin;
    private RoomType selectedRoomType;

    /**
     * Sets the current admin
     */
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        loadRoomTypes();
    }

    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        // Set up table columns
        typeIdColumn.setCellValueFactory(new PropertyValueFactory<>("typeId"));
        typeNameColumn.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        basePriceColumn.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Listen for table selection
        roomTypesTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                selectedRoomType = newValue;
                if (newValue != null) {
                    // Populate form with selected room type data
                    typeNameField.setText(newValue.getTypeName());
                    basePriceField.setText(String.valueOf(newValue.getBasePrice()));
                    capacityField.setText(String.valueOf(newValue.getCapacity()));
                    descriptionArea.setText(newValue.getDescription() != null ? newValue.getDescription() : "");

                    updateButton.setDisable(false);
                    deleteButton.setDisable(false);
                    messageLabel.setText("Selected: " + newValue.getTypeName() + " - Click 'Update' to modify or 'Add Type' to create new");
                } else {
                    // Clear form when nothing is selected
                    updateButton.setDisable(true);
                    deleteButton.setDisable(true);
                    messageLabel.setText("Fill the form and click 'Add Type' to create a new room type");
                }
            }
        );

        // Add listeners to form fields to clear selection when user starts typing
        typeNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedRoomType != null && !newValue.equals(selectedRoomType.getTypeName())) {
                messageLabel.setText("Modified - Click 'Add Type' to create new, or select from table to update existing");
            }
        });

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    /**
     * Loads room types from database
     * Demonstrates: JDBC, Collections with Comparable (TreeSet for automatic sorting)
     */
    private void loadRoomTypes() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "SELECT TYPE_ID, TYPE_NAME, DESCRIPTION, BASE_PRICE, CAPACITY " +
                    "FROM ROOM_TYPES " +
                    "ORDER BY TYPE_NAME";

            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            // Use ArrayList to collect room types
            ArrayList<RoomType> roomTypesList = new ArrayList<>();

            while (rs.next()) {
                RoomType roomType = new RoomType();
                roomType.setTypeId(rs.getInt("TYPE_ID"));
                roomType.setTypeName(rs.getString("TYPE_NAME"));
                roomType.setDescription(rs.getString("DESCRIPTION"));
                roomType.setBasePrice(rs.getDouble("BASE_PRICE"));
                roomType.setCapacity(rs.getInt("CAPACITY"));

                roomTypesList.add(roomType);
            }

            // Demonstrate Comparable - RoomType implements Comparable<RoomType>
            // Use TreeSet to automatically sort by price (using compareTo method)
            TreeSet<RoomType> sortedRoomTypes = new TreeSet<>(roomTypesList);

            // Convert to ObservableList
            ObservableList<RoomType> observableList = FXCollections.observableArrayList(sortedRoomTypes);
            roomTypesTable.setItems(observableList);

            messageLabel.setText("Loaded " + roomTypesList.size() + " room type(s) - sorted by price");

        } catch (ClassNotFoundException | SQLException e) {
            messageLabel.setText("Error loading room types: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Handles add button click
     * Demonstrates: JDBC INSERT operations
     */
    @FXML
    private void handleAdd(ActionEvent event) {
        // Clear selection first to ensure we're in "add" mode, not "update" mode
        selectedRoomType = null;
        roomTypesTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        if (!validateInput()) {
            return;
        }

        // Check for duplicate type name
        String newTypeName = typeNameField.getText().trim();
        for (RoomType rt : roomTypesTable.getItems()) {
            if (rt.getTypeName().equalsIgnoreCase(newTypeName)) {
                messageLabel.setText("Error: Room type '" + newTypeName + "' already exists!");
                return;
            }
        }

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "INSERT INTO ROOM_TYPES (TYPE_ID, TYPE_NAME, DESCRIPTION, BASE_PRICE, CAPACITY) " +
                    "VALUES (ROOM_TYPE_SEQ.NEXTVAL, ?, ?, ?, ?)";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, newTypeName);
            pstmt.setString(2, descriptionArea.getText().trim());
            pstmt.setDouble(3, Double.parseDouble(basePriceField.getText()));
            pstmt.setInt(4, Integer.parseInt(capacityField.getText()));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                messageLabel.setText("Room type '" + newTypeName + "' added successfully!");
                clearFields();
                loadRoomTypes();
            } else {
                messageLabel.setText("Failed to add room type");
            }

        } catch (ClassNotFoundException | SQLException e) {
            messageLabel.setText("Error adding room type: " + e.getMessage());
        } finally {
            closeResources(null, pstmt, connection);
        }
    }

    /**
     * Handles update button click
     * Demonstrates: JDBC UPDATE operations
     */
    @FXML
    private void handleUpdate(ActionEvent event) {
        if (selectedRoomType == null) {
            messageLabel.setText("Please select a room type from the table to update");
            return;
        }

        if (!validateInput()) {
            return;
        }

        // Check for duplicate type name (excluding current type)
        String newTypeName = typeNameField.getText().trim();
        for (RoomType rt : roomTypesTable.getItems()) {
            if (rt.getTypeId() != selectedRoomType.getTypeId() &&
                rt.getTypeName().equalsIgnoreCase(newTypeName)) {
                messageLabel.setText("Error: Room type '" + newTypeName + "' already exists!");
                return;
            }
        }

        // Confirm update
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Update Room Type");
        confirmAlert.setHeaderText("Update room type '" + selectedRoomType.getTypeName() + "'?");
        confirmAlert.setContentText("This will update the room type information.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (!result.isPresent() || result.get() != ButtonType.OK) {
            return;
        }

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "UPDATE ROOM_TYPES SET TYPE_NAME = ?, DESCRIPTION = ?, BASE_PRICE = ?, CAPACITY = ? " +
                    "WHERE TYPE_ID = ?";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, newTypeName);
            pstmt.setString(2, descriptionArea.getText().trim());
            pstmt.setDouble(3, Double.parseDouble(basePriceField.getText()));
            pstmt.setInt(4, Integer.parseInt(capacityField.getText()));
            pstmt.setInt(5, selectedRoomType.getTypeId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                messageLabel.setText("Room type '" + newTypeName + "' updated successfully!");
                clearFields();
                loadRoomTypes();
            } else {
                messageLabel.setText("Failed to update room type");
            }

        } catch (ClassNotFoundException | SQLException e) {
            messageLabel.setText("Error updating room type: " + e.getMessage());
        } finally {
            closeResources(null, pstmt, connection);
        }
    }

    /**
     * Handles delete button click
     * Demonstrates: JDBC DELETE operations
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedRoomType == null) {
            messageLabel.setText("Please select a room type to delete");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Room Type");
        confirmAlert.setHeaderText("Delete room type " + selectedRoomType.getTypeName() + "?");
        confirmAlert.setContentText("This may affect existing rooms.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Connection connection = null;
            PreparedStatement pstmt = null;

            try {
                connection = DatabaseConnection.getConnection();

                String sql = "DELETE FROM ROOM_TYPES WHERE TYPE_ID = ?";
                pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, selectedRoomType.getTypeId());

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    messageLabel.setText("Room type deleted successfully");
                    clearFields();
                    loadRoomTypes();
                } else {
                    messageLabel.setText("Failed to delete room type");
                }

            } catch (ClassNotFoundException | SQLException e) {
                messageLabel.setText("Error deleting room type: " + e.getMessage());
            } finally {
                closeResources(null, pstmt, connection);
            }
        }
    }

    /**
     * Validates input fields
     */
    private boolean validateInput() {
        if (typeNameField.getText().trim().isEmpty()) {
            messageLabel.setText("Please enter a type name");
            return false;
        }

        try {
            double price = Double.parseDouble(basePriceField.getText());
            if (price <= 0) {
                messageLabel.setText("Base price must be positive");
                return false;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid base price");
            return false;
        }

        try {
            int capacity = Integer.parseInt(capacityField.getText());
            if (capacity <= 0) {
                messageLabel.setText("Capacity must be positive");
                return false;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid capacity");
            return false;
        }

        return true;
    }

    /**
     * Clears input fields
     */
    private void clearFields() {
        typeNameField.clear();
        basePriceField.clear();
        capacityField.clear();
        descriptionArea.clear();
        selectedRoomType = null;
        roomTypesTable.getSelectionModel().clearSelection();
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
            messageLabel.setText("Error going back: " + e.getMessage());
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
