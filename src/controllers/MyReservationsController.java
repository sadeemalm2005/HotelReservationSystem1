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

import hotelreservationsystem1.models.Guest;
import hotelreservationsystem1.models.Reservation;
import hotelreservationsystem1.models.Service;
import hotelreservationsystem1.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import database.DatabaseConnection;

/**
 * MyReservationsController manages guest's reservations
 * Demonstrates: Collections (ArrayList, HashMap, LinkedList), Iterator, ListIterator
 */
public class MyReservationsController {

    @FXML
    private TableView<Reservation> reservationsTable;

    @FXML
    private TableColumn<Reservation, Integer> reservationIdColumn;

    @FXML
    private TableColumn<Reservation, String> roomNumberColumn;

    @FXML
    private TableColumn<Reservation, Date> checkInColumn;

    @FXML
    private TableColumn<Reservation, Date> checkOutColumn;

    @FXML
    private TableColumn<Reservation, String> statusColumn;

    @FXML
    private TableColumn<Reservation, Double> totalPriceColumn;

    @FXML
    private ListView<String> servicesListView;

    @FXML
    private Label detailsLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button backButton;

    private Guest currentGuest;
    private Reservation selectedReservation;

    // Demonstrates HashMap - maps reservation ID to list of services
    private HashMap<Integer, LinkedList<Service>> reservationServicesMap;

    /**
     * Sets the current guest
     */
    public void setCurrentGuest(Guest guest) {
        this.currentGuest = guest;
        loadReservations();
    }

    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        // Set up table columns
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        // Initialize HashMap (demonstrates Map usage)
        reservationServicesMap = new HashMap<>();

        // Listen for table selection
        reservationsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                selectedReservation = newValue;
                if (newValue != null) {
                    displayReservationDetails(newValue);
                    cancelButton.setDisable(!newValue.canBeCancelled());
                } else {
                    detailsLabel.setText("");
                    servicesListView.getItems().clear();
                    cancelButton.setDisable(true);
                }
            }
        );

        cancelButton.setDisable(true);
    }

    /**
     * Loads guest reservations from database
     * Demonstrates: JDBC, Collections (ArrayList, LinkedList, HashMap)
     */
    private void loadReservations() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Load reservations
            String sql = "SELECT r.RESERVATION_ID, r.ROOM_ID, rm.ROOM_NUMBER, " +
                    "r.CHECK_IN_DATE, r.CHECK_OUT_DATE, r.STATUS, r.TOTAL_PRICE, r.BOOKING_DATE " +
                    "FROM RESERVATIONS r " +
                    "JOIN ROOMS rm ON r.ROOM_ID = rm.ROOM_ID " +
                    "WHERE r.GUEST_ID = ? " +
                    "ORDER BY r.CHECK_IN_DATE DESC";

            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, currentGuest.getGuestId());
            rs = pstmt.executeQuery();

            // Use ArrayList to store reservations
            ArrayList<Reservation> reservationsList = new ArrayList<>();

            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt("RESERVATION_ID"));
                reservation.setCheckInDate(rs.getDate("CHECK_IN_DATE"));
                reservation.setCheckOutDate(rs.getDate("CHECK_OUT_DATE"));
                reservation.setStatus(rs.getString("STATUS"));
                reservation.setTotalPrice(rs.getDouble("TOTAL_PRICE"));
                reservation.setBookingDate(rs.getDate("BOOKING_DATE"));

                // Create and set Room object with room number for display
                hotelreservationsystem1.models.Room room = new hotelreservationsystem1.models.Room();
                room.setRoomNumber(rs.getString("ROOM_NUMBER"));
                reservation.setRoom(room);

                reservationsList.add(reservation);

                // Load services for this reservation
                LinkedList<Service> services = loadReservationServices(connection, reservation.getReservationId());

                // Store in HashMap (demonstrates Map usage)
                reservationServicesMap.put(reservation.getReservationId(), services);
            }

            // Convert ArrayList to ObservableList
            ObservableList<Reservation> observableList = FXCollections.observableArrayList(reservationsList);
            reservationsTable.setItems(observableList);

        } catch (ClassNotFoundException | SQLException e) {
            showError("Error loading reservations: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Loads services for a specific reservation
     * Demonstrates: LinkedList usage, JDBC
     */
    private LinkedList<Service> loadReservationServices(Connection connection, int reservationId) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LinkedList<Service> services = new LinkedList<>();  // Demonstrates LinkedList

        try {
            String sql = "SELECT s.SERVICE_ID, s.SERVICE_NAME, s.DESCRIPTION, s.PRICE " +
                    "FROM SERVICES s " +
                    "JOIN RESERVATION_SERVICES rs ON s.SERVICE_ID = rs.SERVICE_ID " +
                    "WHERE rs.RESERVATION_ID = ?";

            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Service service = new Service();
                service.setServiceId(rs.getInt("SERVICE_ID"));
                service.setServiceName(rs.getString("SERVICE_NAME"));
                service.setDescription(rs.getString("DESCRIPTION"));
                service.setPrice(rs.getDouble("PRICE"));

                services.add(service);  // Add to LinkedList
            }

        } catch (SQLException e) {
            System.err.println("Error loading services: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

        return services;
    }

    /**
     * Displays reservation details
     * Demonstrates: Iterator and ListIterator usage
     */
    private void displayReservationDetails(Reservation reservation) {
        // Build details text
        StringBuilder details = new StringBuilder();
        details.append("Reservation ID: ").append(reservation.getReservationId()).append("\n");
        details.append("Status: ").append(reservation.getStatus()).append("\n");
        details.append("Nights: ").append(reservation.getNumberOfNights()).append("\n");
        details.append("Total Price: ").append(String.format("%.2f", reservation.getTotalPrice())).append(" SAR");

        detailsLabel.setText(details.toString());

        // Get services from HashMap
        LinkedList<Service> services = reservationServicesMap.get(reservation.getReservationId());

        if (services != null && !services.isEmpty()) {
            // Demonstrate Iterator usage - iterate through services
            ArrayList<String> serviceDescriptions = new ArrayList<>();

            // Using Iterator to traverse LinkedList
            Iterator<Service> iterator = services.iterator();
            while (iterator.hasNext()) {
                Service service = iterator.next();
                serviceDescriptions.add(service.getServiceName() + " - " +
                    String.format("%.2f", service.getPrice()) + " SAR");
            }

            // Demonstrate ListIterator - can traverse in both directions
            ListIterator<String> listIterator = serviceDescriptions.listIterator(serviceDescriptions.size());

            // Traverse backwards to reverse the list (demonstrates ListIterator)
            ArrayList<String> reversedList = new ArrayList<>();
            while (listIterator.hasPrevious()) {
                reversedList.add(listIterator.previous());
            }

            // For display, use original order
            ObservableList<String> serviceList = FXCollections.observableArrayList(serviceDescriptions);
            servicesListView.setItems(serviceList);
        } else {
            servicesListView.setItems(FXCollections.observableArrayList("No additional services"));
        }
    }

    /**
     * Handles cancel button click
     * Demonstrates: JDBC update operations
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        if (selectedReservation == null) {
            showError("Please select a reservation to cancel");
            return;
        }

        if (!selectedReservation.canBeCancelled()) {
            showError("This reservation cannot be cancelled");
            return;
        }

        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Reservation");
        confirmAlert.setHeaderText("Are you sure you want to cancel this reservation?");
        confirmAlert.setContentText("Reservation ID: " + selectedReservation.getReservationId());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cancelReservation(selectedReservation.getReservationId());
        }
    }

    /**
     * Cancels a reservation
     * Demonstrates: JDBC PreparedStatement update
     */
    private void cancelReservation(int reservationId) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "UPDATE RESERVATIONS SET STATUS = 'CANCELLED' WHERE RESERVATION_ID = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, reservationId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                showInfo("Reservation cancelled successfully");
                loadReservations();  // Reload reservations
            } else {
                showError("Failed to cancel reservation");
            }

        } catch (ClassNotFoundException | SQLException e) {
            showError("Error cancelling reservation: " + e.getMessage());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/GuestDashboard.fxml"));
            Parent root = loader.load();

            GuestDashboardController controller = loader.getController();
            controller.setCurrentGuest(currentGuest);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            showError("Error going back: " + e.getMessage());
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

    /**
     * Shows error alert
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows info alert
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
