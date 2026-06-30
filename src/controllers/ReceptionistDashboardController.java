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

import hotelreservationsystem1.models.Receptionist;
import hotelreservationsystem1.models.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import database.DatabaseConnection;

/**
 * ReceptionistDashboardController manages receptionist dashboard
 * Demonstrates: Collections, Queue for task management, JDBC
 */
public class ReceptionistDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<Reservation> todayCheckInsTable;

    @FXML
    private TableColumn<Reservation, Integer> checkInIdColumn;

    @FXML
    private TableColumn<Reservation, String> checkInGuestColumn;

    @FXML
    private TableColumn<Reservation, String> checkInRoomColumn;

    @FXML
    private TableView<Reservation> todayCheckOutsTable;

    @FXML
    private TableColumn<Reservation, Integer> checkOutIdColumn;

    @FXML
    private TableColumn<Reservation, String> checkOutGuestColumn;

    @FXML
    private TableColumn<Reservation, String> checkOutRoomColumn;

    @FXML
    private ListView<String> pendingTasksListView;

    @FXML
    private Button checkInOutButton;

    @FXML
    private Button roomManagementButton;

    @FXML
    private Button logoutButton;

    private Receptionist currentReceptionist;

    // Demonstrates Queue - for managing pending tasks (FIFO)
    private Queue<String> pendingTasksQueue;

    /**
     * Sets the current receptionist
     */
    public void setCurrentReceptionist(Receptionist receptionist) {
        this.currentReceptionist = receptionist;
        welcomeLabel.setText("Welcome, Receptionist " + receptionist.getUsername());
        loadDashboardData();
    }

    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        // Set up check-in table columns
        checkInIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        checkInGuestColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        checkInRoomColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        // Set up check-out table columns
        checkOutIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        checkOutGuestColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        checkOutRoomColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        // Initialize Queue (demonstrates Queue usage - LinkedList implements Queue)
        pendingTasksQueue = new LinkedList<>();
    }

    /**
     * Loads dashboard data
     * Demonstrates: JDBC, Collections (ArrayList), Queue
     */
    private void loadDashboardData() {
        loadTodayCheckIns();
        loadTodayCheckOuts();
        loadPendingTasks();
    }

    /**
     * Loads today's check-ins
     * Demonstrates: JDBC, PreparedStatement
     */
    private void loadTodayCheckIns() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "SELECT r.RESERVATION_ID, r.ROOM_ID, rm.ROOM_NUMBER, " +
                    "g.FIRST_NAME, g.LAST_NAME, r.CHECK_IN_DATE " +
                    "FROM RESERVATIONS r " +
                    "JOIN ROOMS rm ON r.ROOM_ID = rm.ROOM_ID " +
                    "JOIN GUESTS g ON r.GUEST_ID = g.GUEST_ID " +
                    "WHERE TRUNC(r.CHECK_IN_DATE) = TRUNC(SYSDATE) " +
                    "AND r.STATUS = 'CONFIRMED' " +
                    "ORDER BY r.CHECK_IN_DATE";

            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            ArrayList<Reservation> checkInsList = new ArrayList<>();

            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt("RESERVATION_ID"));
                reservation.setCheckInDate(rs.getDate("CHECK_IN_DATE"));

                // Create and set Guest object
                hotelreservationsystem1.models.Guest guest = new hotelreservationsystem1.models.Guest();
                guest.setFirstName(rs.getString("FIRST_NAME"));
                guest.setLastName(rs.getString("LAST_NAME"));
                reservation.setGuest(guest);

                // Create and set Room object
                hotelreservationsystem1.models.Room room = new hotelreservationsystem1.models.Room();
                room.setRoomNumber(rs.getString("ROOM_NUMBER"));
                reservation.setRoom(room);

                checkInsList.add(reservation);
            }

            ObservableList<Reservation> observableList = FXCollections.observableArrayList(checkInsList);
            todayCheckInsTable.setItems(observableList);

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error loading check-ins: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Loads today's check-outs
     * Demonstrates: JDBC, PreparedStatement
     */
    private void loadTodayCheckOuts() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "SELECT r.RESERVATION_ID, r.ROOM_ID, rm.ROOM_NUMBER, " +
                    "g.FIRST_NAME, g.LAST_NAME, r.CHECK_OUT_DATE " +
                    "FROM RESERVATIONS r " +
                    "JOIN ROOMS rm ON r.ROOM_ID = rm.ROOM_ID " +
                    "JOIN GUESTS g ON r.GUEST_ID = g.GUEST_ID " +
                    "WHERE TRUNC(r.CHECK_OUT_DATE) = TRUNC(SYSDATE) " +
                    "AND r.STATUS = 'CHECKED_IN' " +
                    "ORDER BY r.CHECK_OUT_DATE";

            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            ArrayList<Reservation> checkOutsList = new ArrayList<>();

            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt("RESERVATION_ID"));
                reservation.setCheckOutDate(rs.getDate("CHECK_OUT_DATE"));

                // Create and set Guest object
                hotelreservationsystem1.models.Guest guest = new hotelreservationsystem1.models.Guest();
                guest.setFirstName(rs.getString("FIRST_NAME"));
                guest.setLastName(rs.getString("LAST_NAME"));
                reservation.setGuest(guest);

                // Create and set Room object
                hotelreservationsystem1.models.Room room = new hotelreservationsystem1.models.Room();
                room.setRoomNumber(rs.getString("ROOM_NUMBER"));
                reservation.setRoom(room);

                checkOutsList.add(reservation);
            }

            ObservableList<Reservation> observableList = FXCollections.observableArrayList(checkOutsList);
            todayCheckOutsTable.setItems(observableList);

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error loading check-outs: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Loads pending tasks
     * Demonstrates: Queue usage (FIFO - First In First Out)
     */
    private void loadPendingTasks() {
        // Clear existing queue
        pendingTasksQueue.clear();

        // Add tasks to Queue (demonstrates Queue operations)
        int checkInsCount = todayCheckInsTable.getItems().size();
        int checkOutsCount = todayCheckOutsTable.getItems().size();

        if (checkInsCount > 0) {
            pendingTasksQueue.offer("Process " + checkInsCount + " check-in(s)");  // Queue offer() method
        }

        if (checkOutsCount > 0) {
            pendingTasksQueue.offer("Process " + checkOutsCount + " check-out(s)");  // Queue offer() method
        }

        // Add some sample tasks
        pendingTasksQueue.offer("Review room maintenance requests");
        pendingTasksQueue.offer("Update room status");
        pendingTasksQueue.offer("Process pending payments");

        // Display tasks from Queue
        ArrayList<String> tasksList = new ArrayList<>();

        // Demonstrate Queue iteration (peek and poll)
        // Use a temporary queue to preserve original
        Queue<String> tempQueue = new LinkedList<>(pendingTasksQueue);

        int taskNumber = 1;
        while (!tempQueue.isEmpty()) {
            String task = tempQueue.poll();  // Queue poll() method - removes and returns head
            tasksList.add(taskNumber + ". " + task);
            taskNumber++;
        }

        ObservableList<String> observableTasks = FXCollections.observableArrayList(tasksList);
        pendingTasksListView.setItems(observableTasks);
    }

    /**
     * Handles check-in/out button click
     */
    @FXML
    private void handleCheckInOut(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CheckInOut.fxml"));
            Parent root = loader.load();

            CheckInOutController controller = loader.getController();
            controller.setCurrentReceptionist(currentReceptionist);

            Stage stage = (Stage) checkInOutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error opening check-in/out: " + e.getMessage());
        }
    }

    /**
     * Handles room management button click
     */
    @FXML
    private void handleRoomManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RoomManagement.fxml"));
            Parent root = loader.load();

            RoomManagementController controller = loader.getController();
            controller.setCurrentReceptionist(currentReceptionist);

            Stage stage = (Stage) roomManagementButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error opening room management: " + e.getMessage());
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
