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
import hotelreservationsystem1.models.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import database.DatabaseConnection;

/**
 * ServiceManagementController manages hotel services
 * Demonstrates: JDBC CRUD operations, Collections
 */
public class ServiceManagementController {

    @FXML
    private TableView<Service> servicesTable;

    @FXML
    private TableColumn<Service, Integer> serviceIdColumn;

    @FXML
    private TableColumn<Service, String> serviceNameColumn;

    @FXML
    private TableColumn<Service, Double> priceColumn;

    @FXML
    private TableColumn<Service, String> descriptionColumn;

    @FXML
    private TextField serviceNameField;

    @FXML
    private TextField priceField;

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
    private Service selectedService;

    /**
     * Sets the current admin
     */
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        loadServices();
    }

    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        // Set up table columns
        serviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Listen for table selection
        servicesTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                selectedService = newValue;
                if (newValue != null) {
                    // Populate form with selected service data
                    serviceNameField.setText(newValue.getServiceName());
                    priceField.setText(String.valueOf(newValue.getPrice()));
                    descriptionArea.setText(newValue.getDescription() != null ? newValue.getDescription() : "");

                    updateButton.setDisable(false);
                    deleteButton.setDisable(false);
                    messageLabel.setText("Selected: " + newValue.getServiceName() + " - Click 'Update' to modify or 'Add Service' to create new");
                } else {
                    // Clear form when nothing is selected
                    updateButton.setDisable(true);
                    deleteButton.setDisable(true);
                    messageLabel.setText("Fill the form and click 'Add Service' to create a new service");
                }
            }
        );

        // Add listeners to form fields to detect modifications
        serviceNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedService != null && !newValue.equals(selectedService.getServiceName())) {
                messageLabel.setText("Modified - Click 'Add Service' to create new, or select from table to update existing");
            }
        });

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    /**
     * Loads services from database
     * Demonstrates: JDBC, Collections (ArrayList)
     */
    private void loadServices() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "SELECT SERVICE_ID, SERVICE_NAME, DESCRIPTION, PRICE " +
                    "FROM SERVICES " +
                    "ORDER BY SERVICE_NAME";

            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            // Use ArrayList to collect services
            ArrayList<Service> servicesList = new ArrayList<>();

            while (rs.next()) {
                Service service = new Service();
                service.setServiceId(rs.getInt("SERVICE_ID"));
                service.setServiceName(rs.getString("SERVICE_NAME"));
                service.setDescription(rs.getString("DESCRIPTION"));
                service.setPrice(rs.getDouble("PRICE"));

                servicesList.add(service);
            }

            // Convert to ObservableList
            ObservableList<Service> observableList = FXCollections.observableArrayList(servicesList);
            servicesTable.setItems(observableList);

            messageLabel.setText("Loaded " + servicesList.size() + " service(s)");

        } catch (ClassNotFoundException | SQLException e) {
            messageLabel.setText("Error loading services: " + e.getMessage());
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
        selectedService = null;
        servicesTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        if (!validateInput()) {
            return;
        }

        // Check for duplicate service name
        String newServiceName = serviceNameField.getText().trim();
        for (Service s : servicesTable.getItems()) {
            if (s.getServiceName().equalsIgnoreCase(newServiceName)) {
                messageLabel.setText("Error: Service '" + newServiceName + "' already exists!");
                return;
            }
        }

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "INSERT INTO SERVICES (SERVICE_ID, SERVICE_NAME, DESCRIPTION, PRICE) " +
                    "VALUES (SERVICE_SEQ.NEXTVAL, ?, ?, ?)";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, newServiceName);
            pstmt.setString(2, descriptionArea.getText().trim());
            pstmt.setDouble(3, Double.parseDouble(priceField.getText()));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                messageLabel.setText("Service '" + newServiceName + "' added successfully!");
                clearFields();
                loadServices();
            } else {
                messageLabel.setText("Failed to add service");
            }

        } catch (ClassNotFoundException | SQLException e) {
            messageLabel.setText("Error adding service: " + e.getMessage());
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
        if (selectedService == null) {
            messageLabel.setText("Please select a service from the table to update");
            return;
        }

        if (!validateInput()) {
            return;
        }

        // Check for duplicate service name (excluding current service)
        String newServiceName = serviceNameField.getText().trim();
        for (Service s : servicesTable.getItems()) {
            if (s.getServiceId() != selectedService.getServiceId() &&
                s.getServiceName().equalsIgnoreCase(newServiceName)) {
                messageLabel.setText("Error: Service '" + newServiceName + "' already exists!");
                return;
            }
        }

        // Confirm update
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Update Service");
        confirmAlert.setHeaderText("Update service '" + selectedService.getServiceName() + "'?");
        confirmAlert.setContentText("This will update the service information.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (!result.isPresent() || result.get() != ButtonType.OK) {
            return;
        }

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "UPDATE SERVICES SET SERVICE_NAME = ?, DESCRIPTION = ?, PRICE = ? " +
                    "WHERE SERVICE_ID = ?";

            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, newServiceName);
            pstmt.setString(2, descriptionArea.getText().trim());
            pstmt.setDouble(3, Double.parseDouble(priceField.getText()));
            pstmt.setInt(4, selectedService.getServiceId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                messageLabel.setText("Service '" + newServiceName + "' updated successfully!");
                clearFields();
                loadServices();
            } else {
                messageLabel.setText("Failed to update service");
            }

        } catch (ClassNotFoundException | SQLException e) {
            messageLabel.setText("Error updating service: " + e.getMessage());
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
        if (selectedService == null) {
            messageLabel.setText("Please select a service to delete");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Service");
        confirmAlert.setHeaderText("Delete service " + selectedService.getServiceName() + "?");
        confirmAlert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Connection connection = null;
            PreparedStatement pstmt = null;

            try {
                connection = DatabaseConnection.getConnection();

                String sql = "DELETE FROM SERVICES WHERE SERVICE_ID = ?";
                pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, selectedService.getServiceId());

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    messageLabel.setText("Service deleted successfully");
                    clearFields();
                    loadServices();
                } else {
                    messageLabel.setText("Failed to delete service");
                }

            } catch (ClassNotFoundException | SQLException e) {
                messageLabel.setText("Error deleting service: " + e.getMessage());
            } finally {
                closeResources(null, pstmt, connection);
            }
        }
    }

    /**
     * Validates input fields
     */
    private boolean validateInput() {
        if (serviceNameField.getText().trim().isEmpty()) {
            messageLabel.setText("Please enter a service name");
            return false;
        }

        try {
            double price = Double.parseDouble(priceField.getText());
            if (price < 0) {
                messageLabel.setText("Price must be non-negative");
                return false;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Invalid price - please enter a valid number");
            return false;
        }

        return true;
    }

    /**
     * Clears input fields
     */
    private void clearFields() {
        serviceNameField.clear();
        priceField.clear();
        descriptionArea.clear();
        selectedService = null;
        servicesTable.getSelectionModel().clearSelection();
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
