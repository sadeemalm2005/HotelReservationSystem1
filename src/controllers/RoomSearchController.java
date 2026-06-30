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
import hotelreservationsystem1.models.Room;
import hotelreservationsystem1.models.RoomType;
import hotelreservationsystem1.models.Reservation;
import hotelreservationsystem1.exceptions.DatabaseException;
import hotelreservationsystem1.exceptions.InvalidDateException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import database.DatabaseConnection;

/**
 * RoomSearchController handles room search and booking
 * Demonstrates: Collections (ArrayList, TreeSet for sorting), Comparator, JDBC
 */
public class RoomSearchController {

    @FXML
    private DatePicker checkInDatePicker;

    @FXML
    private DatePicker checkOutDatePicker;

    @FXML
    private ComboBox<String> roomTypeComboBox;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<Room> roomsTable;

    @FXML
    private TableColumn<Room, String> roomNumberColumn;

    @FXML
    private TableColumn<Room, String> roomTypeColumn;

    @FXML
    private TableColumn<Room, Double> priceColumn;

    @FXML
    private TableColumn<Room, String> statusColumn;

    @FXML
    private Button bookButton;

    @FXML
    private Button backButton;

    private Guest currentGuest;
    private Room selectedRoom;

    /**
     * Sets the current guest
     */
    public void setCurrentGuest(Guest guest) {
        this.currentGuest = guest;
        // Reload room types in case initialize() failed or ran too early
        if (roomTypeComboBox.getItems().isEmpty()) {
            loadRoomTypes();
        }
    }

    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        // Set up table columns
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomTypeName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load room types
        loadRoomTypes();

        // Listen for table selection
        roomsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                selectedRoom = newValue;
                bookButton.setDisable(newValue == null);
            }
        );

        bookButton.setDisable(true);
    }

    /**
     * Loads room types from database
     * Demonstrates: JDBC, PreparedStatement
     */
    private void loadRoomTypes() {
        if (roomTypeComboBox == null) {
            System.err.println("Warning: roomTypeComboBox is null, cannot load room types");
            return;
        }

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "SELECT TYPE_NAME FROM ROOM_TYPES ORDER BY TYPE_NAME";
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            // Use ArrayList to store room types
            ArrayList<String> roomTypes = new ArrayList<>();
            roomTypes.add("All");  // Add "All" option

            while (rs.next()) {
                roomTypes.add(rs.getString("TYPE_NAME"));
            }

            // Convert to ObservableList for ComboBox
            ObservableList<String> options = FXCollections.observableArrayList(roomTypes);
            roomTypeComboBox.setItems(options);
            roomTypeComboBox.setValue("All");

            System.out.println("Loaded " + (roomTypes.size() - 1) + " room types successfully");

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error loading room types: " + e.getMessage());
            e.printStackTrace();
            // Don't show error dialog during initialization, just log it
            if (roomTypeComboBox != null && roomTypeComboBox.getScene() != null) {
                showError("Error loading room types: " + e.getMessage());
            }
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Handles search button click
     * Demonstrates: JDBC, Collections (ArrayList), TreeSet with Comparator for sorting
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        try {
            // Validate dates
            if (checkInDatePicker.getValue() == null || checkOutDatePicker.getValue() == null) {
                throw new InvalidDateException("Please select check-in and check-out dates");
            }

            java.time.LocalDate checkIn = checkInDatePicker.getValue();
            java.time.LocalDate checkOut = checkOutDatePicker.getValue();

            if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
                throw new InvalidDateException("Check-out date must be after check-in date");
            }

            // Search for available rooms
            List<Room> availableRooms = searchAvailableRooms(checkIn, checkOut, roomTypeComboBox.getValue());

            // Demonstrate TreeSet with Comparator - sort rooms by price (ascending)
            Comparator<Room> priceComparator = new Comparator<Room>() {
                @Override
                public int compare(Room r1, Room r2) {
                    int priceCompare = Double.compare(r1.getPrice(), r2.getPrice());
                    if (priceCompare != 0) {
                        return priceCompare;
                    }
                    // If prices are equal, compare by room number to maintain uniqueness in TreeSet
                    return r1.getRoomNumber().compareTo(r2.getRoomNumber());
                }
            };

            // Use TreeSet with custom Comparator for automatic sorting
            TreeSet<Room> sortedRooms = new TreeSet<>(priceComparator);
            sortedRooms.addAll(availableRooms);

            // Convert TreeSet to ObservableList
            ObservableList<Room> roomList = FXCollections.observableArrayList(sortedRooms);
            roomsTable.setItems(roomList);

            if (roomList.isEmpty()) {
                showError("No available rooms found for the selected dates and criteria");
            }

        } catch (InvalidDateException e) {
            showError(e.getMessage());
        } catch (DatabaseException e) {
            showError("Database error: " + e.getMessage());
        } catch (Exception e) {
            showError("Error searching rooms: " + e.getMessage());
        }
    }

    /**
     * Searches for available rooms
     * Demonstrates: JDBC, PreparedStatement, ArrayList
     */
    private List<Room> searchAvailableRooms(java.time.LocalDate checkIn, java.time.LocalDate checkOut, String roomType) throws DatabaseException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<Room> rooms = new ArrayList<>();

        try {
            connection = DatabaseConnection.getConnection();

            // SQL to find available rooms (not reserved for the given dates)
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT r.ROOM_ID, r.ROOM_NUMBER, r.FLOOR, r.STATUS, ");
            sql.append("rt.TYPE_NAME, rt.BASE_PRICE, rt.CAPACITY ");
            sql.append("FROM ROOMS r ");
            sql.append("JOIN ROOM_TYPES rt ON r.TYPE_ID = rt.TYPE_ID ");
            sql.append("WHERE r.STATUS = 'AVAILABLE' ");

            // Add room type filter if not "All"
            if (!"All".equals(roomType)) {
                sql.append("AND rt.TYPE_NAME = ? ");
            }

            // Check for no overlapping reservations
            sql.append("AND r.ROOM_ID NOT IN ( ");
            sql.append("  SELECT ROOM_ID FROM RESERVATIONS ");
            sql.append("  WHERE STATUS IN ('CONFIRMED', 'CHECKED_IN') ");
            sql.append("  AND NOT (CHECK_OUT_DATE <= ? OR CHECK_IN_DATE >= ?) ");
            sql.append(") ");
            sql.append("ORDER BY rt.BASE_PRICE, r.ROOM_NUMBER");

            pstmt = connection.prepareStatement(sql.toString());

            int paramIndex = 1;
            if (!"All".equals(roomType)) {
                pstmt.setString(paramIndex++, roomType);
            }

            // Convert LocalDate to java.sql.Date
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(checkIn));
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(checkOut));

            rs = pstmt.executeQuery();

            while (rs.next()) {
                // Create RoomType object (demonstrates Composition)
                RoomType type = new RoomType();
                type.setTypeName(rs.getString("TYPE_NAME"));
                type.setBasePrice(rs.getDouble("BASE_PRICE"));
                type.setCapacity(rs.getInt("CAPACITY"));

                // Create Room object with RoomType
                Room room = new Room();
                room.setRoomId(rs.getInt("ROOM_ID"));
                room.setRoomNumber(rs.getString("ROOM_NUMBER"));
                room.setFloor(rs.getInt("FLOOR"));
                room.setStatus(rs.getString("STATUS"));
                room.setRoomType(type);  // Composition - Room HAS-A RoomType

                rooms.add(room);
            }

        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseException("Error searching rooms: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }

        return rooms;
    }

    /**
     * Handles book button click
     */
    @FXML
    private void handleBook(ActionEvent event) {
        if (selectedRoom == null) {
            showError("Please select a room to book");
            return;
        }

        try {
            // Create reservation
            java.time.LocalDate checkIn = checkInDatePicker.getValue();
            java.time.LocalDate checkOut = checkOutDatePicker.getValue();

            int reservationId = createReservation(selectedRoom, checkIn, checkOut);

            if (reservationId > 0) {
                showInfo("Reservation created successfully! Reservation ID: " + reservationId);

                // Go back to dashboard
                handleBack(event);
            } else {
                showError("Failed to create reservation");
            }

        } catch (Exception e) {
            showError("Error creating reservation: " + e.getMessage());
        }
    }

    /**
     * Creates a new reservation
     * Demonstrates: JDBC, PreparedStatement
     */
    private int createReservation(Room room, java.time.LocalDate checkIn, java.time.LocalDate checkOut) throws DatabaseException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int reservationId = 0;

        try {
            connection = DatabaseConnection.getConnection();

            // Calculate total amount
            long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
            double totalAmount = nights * room.getPrice();

            String sql = "INSERT INTO RESERVATIONS (RESERVATION_ID, GUEST_ID, ROOM_ID, " +
                    "CHECK_IN_DATE, CHECK_OUT_DATE, TOTAL_PRICE, STATUS, BOOKING_DATE) " +
                    "VALUES (RESERVATION_SEQ.NEXTVAL, ?, ?, ?, ?, ?, 'CONFIRMED', SYSDATE)";

            pstmt = connection.prepareStatement(sql, new String[]{"RESERVATION_ID"});
            pstmt.setInt(1, currentGuest.getGuestId());
            pstmt.setInt(2, room.getRoomId());
            pstmt.setDate(3, java.sql.Date.valueOf(checkIn));
            pstmt.setDate(4, java.sql.Date.valueOf(checkOut));
            pstmt.setDouble(5, totalAmount);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Get generated reservation ID
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    reservationId = rs.getInt(1);
                }
            }

        } catch (ClassNotFoundException | SQLException e) {
            throw new DatabaseException("Error creating reservation: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }

        return reservationId;
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
