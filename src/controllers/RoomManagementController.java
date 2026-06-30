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
import hotelreservationsystem1.models.Room;
import hotelreservationsystem1.models.RoomType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import database.DatabaseConnection;

/**
 * RoomManagementController manages room status and maintenance
 * Demonstrates: Collections (TreeSet for sorting), Comparator, JDBC
 */
public class RoomManagementController {

    @FXML
    private TableView<Room> roomsTable;

    @FXML
    private TableColumn<Room, String> roomNumberColumn;

    @FXML
    private TableColumn<Room, String> roomTypeColumn;

    @FXML
    private TableColumn<Room, Integer> floorColumn;

    @FXML
    private TableColumn<Room, String> statusColumn;

    @FXML
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private ComboBox<String> newStatusComboBox;

    @FXML
    private Button updateStatusButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button backButton;

    @FXML
    private TextArea notesArea;

    private Receptionist currentReceptionist;
    private Room selectedRoom;

    /**
     * Sets the current receptionist
     */
    public void setCurrentReceptionist(Receptionist receptionist) {
        this.currentReceptionist = receptionist;
        loadRooms();
    }

    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        // Set up table columns
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomTypeName"));
        floorColumn.setCellValueFactory(new PropertyValueFactory<>("floor"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Initialize status filter combo box
        ObservableList<String> statusFilters = FXCollections.observableArrayList(
            "All",
            Room.STATUS_AVAILABLE,
            Room.STATUS_OCCUPIED,
            Room.STATUS_MAINTENANCE,
            Room.STATUS_CLEANING
        );
        statusFilterComboBox.setItems(statusFilters);
        statusFilterComboBox.setValue("All");

        // Initialize new status combo box
        ObservableList<String> statusOptions = FXCollections.observableArrayList(
            Room.STATUS_AVAILABLE,
            Room.STATUS_OCCUPIED,
            Room.STATUS_MAINTENANCE,
            Room.STATUS_CLEANING
        );
        newStatusComboBox.setItems(statusOptions);

        // Listen for table selection
        roomsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                selectedRoom = newValue;
                if (newValue != null) {
                    newStatusComboBox.setValue(newValue.getStatus());
                    updateStatusButton.setDisable(false);
                } else {
                    updateStatusButton.setDisable(true);
                }
            }
        );

        // Listen for status filter changes
        statusFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadRooms();
        });

        updateStatusButton.setDisable(true);
    }

    /**
     * Loads rooms from database
     * Demonstrates: JDBC, Collections (TreeSet with Comparator for sorting)
     */
    private void loadRooms() {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();

            // Build SQL query based on filter
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT r.ROOM_ID, r.ROOM_NUMBER, r.FLOOR, r.STATUS, ");
            sql.append("rt.TYPE_NAME, rt.BASE_PRICE ");
            sql.append("FROM ROOMS r ");
            sql.append("JOIN ROOM_TYPES rt ON r.TYPE_ID = rt.TYPE_ID ");

            String statusFilter = statusFilterComboBox.getValue();
            if (statusFilter != null && !"All".equals(statusFilter)) {
                sql.append("WHERE r.STATUS = ? ");
            }

            sql.append("ORDER BY r.FLOOR, r.ROOM_NUMBER");

            pstmt = connection.prepareStatement(sql.toString());

            if (statusFilter != null && !"All".equals(statusFilter)) {
                pstmt.setString(1, statusFilter);
            }

            rs = pstmt.executeQuery();

            // Use ArrayList to collect rooms
            ArrayList<Room> roomsList = new ArrayList<>();

            while (rs.next()) {
                // Create RoomType
                RoomType roomType = new RoomType();
                roomType.setTypeName(rs.getString("TYPE_NAME"));
                roomType.setBasePrice(rs.getDouble("BASE_PRICE"));

                // Create Room
                Room room = new Room();
                room.setRoomId(rs.getInt("ROOM_ID"));
                room.setRoomNumber(rs.getString("ROOM_NUMBER"));
                room.setFloor(rs.getInt("FLOOR"));
                room.setStatus(rs.getString("STATUS"));
                room.setRoomType(roomType);

                roomsList.add(room);
            }

            // Demonstrate TreeSet with custom Comparator - sort by floor then room number
            Comparator<Room> floorAndRoomComparator = new Comparator<Room>() {
                @Override
                public int compare(Room r1, Room r2) {
                    // First compare by floor
                    int floorCompare = Integer.compare(r1.getFloor(), r2.getFloor());
                    if (floorCompare != 0) {
                        return floorCompare;
                    }
                    // If same floor, compare by room number
                    return r1.getRoomNumber().compareTo(r2.getRoomNumber());
                }
            };

            // Use TreeSet with custom Comparator for automatic sorting
            TreeSet<Room> sortedRooms = new TreeSet<>(floorAndRoomComparator);
            sortedRooms.addAll(roomsList);

            // Convert to ObservableList for TableView
            ObservableList<Room> observableRooms = FXCollections.observableArrayList(sortedRooms);
            roomsTable.setItems(observableRooms);

            notesArea.setText("Loaded " + observableRooms.size() + " room(s)");

        } catch (ClassNotFoundException | SQLException e) {
            notesArea.setText("Error loading rooms: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, connection);
        }
    }

    /**
     * Handles update status button click
     * Demonstrates: JDBC update operations
     */
    @FXML
    private void handleUpdateStatus(ActionEvent event) {
        if (selectedRoom == null) {
            notesArea.setText("Please select a room");
            return;
        }

        String newStatus = newStatusComboBox.getValue();
        if (newStatus == null || newStatus.isEmpty()) {
            notesArea.setText("Please select a new status");
            return;
        }

        // Confirm status change
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Update Room Status");
        confirmAlert.setHeaderText("Change room " + selectedRoom.getRoomNumber() + " status?");
        confirmAlert.setContentText("From: " + selectedRoom.getStatus() + "\nTo: " + newStatus);

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateRoomStatus(selectedRoom.getRoomId(), newStatus);
        }
    }

    /**
     * Updates room status in database
     * Demonstrates: JDBC PreparedStatement update
     */
    private void updateRoomStatus(int roomId, String newStatus) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DatabaseConnection.getConnection();

            String sql = "UPDATE ROOMS SET STATUS = ? WHERE ROOM_ID = ?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, roomId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                notesArea.setText("Room status updated successfully to: " + newStatus);
                loadRooms();  // Reload rooms
            } else {
                notesArea.setText("Failed to update room status");
            }

        } catch (ClassNotFoundException | SQLException e) {
            notesArea.setText("Error updating room status: " + e.getMessage());
        } finally {
            closeResources(null, pstmt, connection);
        }
    }

    /**
     * Handles refresh button click
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadRooms();
    }

    /**
     * Handles back button click
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ReceptionistDashboard.fxml"));
            Parent root = loader.load();

            ReceptionistDashboardController controller = loader.getController();
            controller.setCurrentReceptionist(currentReceptionist);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            notesArea.setText("Error going back: " + e.getMessage());
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
