package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
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
import hotelreservationsystem1.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import database.DatabaseConnection;

/**
 * GuestDashboardController manages guest dashboard view
 * Demonstrates: Collections (ArrayList), TableView, JDBC
 */
public class GuestDashboardController {

    @FXML
    private Label welcomeLabel;

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
    private TableColumn<Reservation, Double> totalAmountColumn;

    @FXML
    private Button searchRoomsButton;

    @FXML
    private Button myReservationsButton;

    @FXML
    private Button logoutButton;

    private Guest currentGuest;

    /**
     * Sets the current guest and loads their data
     */
    public void setCurrentGuest(Guest guest) {
        this.currentGuest = guest;
        welcomeLabel.setText("Welcome, " + guest.getFirstName() + " " + guest.getLastName());
        loadGuestReservations();
    }

    /**
     * Initializes the table columns
     */
    @FXML
    private void initialize() {
        // Set up table columns
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
    }

    /**
     * Loads guest reservations from database
     * Demonstrates: JDBC, PreparedStatement, Collections (ArrayList)
     */
    private void loadGuestReservations() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Get database connection
            connection = DatabaseConnection.getConnection();

            // Prepare SQL statement
            String sql = "SELECT r.RESERVATION_ID, r.ROOM_ID, rm.ROOM_NUMBER, " +
                    "r.CHECK_IN_DATE, r.CHECK_OUT_DATE, r.STATUS, r.TOTAL_PRICE " +
                    "FROM RESERVATIONS r " +
                    "JOIN ROOMS rm ON r.ROOM_ID = rm.ROOM_ID " +
                    "WHERE r.GUEST_ID = ? " +
                    "ORDER BY r.CHECK_IN_DATE DESC";

            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, currentGuest.getGuestId());

            // Execute query
            rs = pstmt.executeQuery();

            // Process ResultSet and store in ArrayList (demonstrates Collection usage)
            ArrayList<Reservation> reservationsList = new ArrayList<>();

            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt("RESERVATION_ID"));
                reservation.setCheckInDate(rs.getDate("CHECK_IN_DATE"));
                reservation.setCheckOutDate(rs.getDate("CHECK_OUT_DATE"));
                reservation.setStatus(rs.getString("STATUS"));
                reservation.setTotalPrice(rs.getDouble("TOTAL_PRICE"));

                // Create and set Room object with room number for display
                hotelreservationsystem1.models.Room room = new hotelreservationsystem1.models.Room();
                room.setRoomNumber(rs.getString("ROOM_NUMBER"));
                reservation.setRoom(room);

                reservationsList.add(reservation);
            }

            // Convert ArrayList to ObservableList for TableView
            ObservableList<Reservation> observableList = FXCollections.observableArrayList(reservationsList);
            reservationsTable.setItems(observableList);

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error loading reservations: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (connection != null) DatabaseConnection.closeConnection(connection);
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Handles search rooms button click
     */
    @FXML
    private void handleSearchRooms(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RoomSearch.fxml"));
            Parent root = loader.load();

            // Pass current guest to next controller
            RoomSearchController controller = loader.getController();
            controller.setCurrentGuest(currentGuest);

            Stage stage = (Stage) searchRoomsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error opening room search: " + e.getMessage());
        }
    }

    /**
     * Handles my reservations button click
     */
    @FXML
    private void handleMyReservations(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MyReservations.fxml"));
            Parent root = loader.load();

            // Pass current guest to next controller
            MyReservationsController controller = loader.getController();
            controller.setCurrentGuest(currentGuest);

            Stage stage = (Stage) myReservationsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error opening my reservations: " + e.getMessage());
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
}
