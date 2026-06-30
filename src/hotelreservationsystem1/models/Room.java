package hotelreservationsystem1.models;

/**
 * Room class
 * Demonstrates Interface implementation - implements Comparable
 * Demonstrates Composition - contains RoomType object
 */
public class Room implements Comparable<Room> {
    private int roomId;
    private String roomNumber;
    private RoomType roomType;  // Composition - Room HAS-A RoomType
    private int floor;
    private String status;

    // Constants for room status
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_OCCUPIED = "OCCUPIED";
    public static final String STATUS_MAINTENANCE = "MAINTENANCE";
    public static final String STATUS_CLEANING = "CLEANING";

    // Constructors
    public Room() {
        this.status = STATUS_AVAILABLE;
    }

    public Room(String roomNumber, RoomType roomType, int floor) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.floor = floor;
        this.status = STATUS_AVAILABLE;
    }

    /**
     * Implements Comparable interface
     * Natural ordering by room number (ascending)
     */
    @Override
    public int compareTo(Room other) {
        return this.roomNumber.compareTo(other.roomNumber);
    }

    // Check if room is available
    public boolean isAvailable() {
        return STATUS_AVAILABLE.equals(this.status);
    }

    // Get price from room type (demonstrates composition)
    public double getPrice() {
        return roomType != null ? roomType.getBasePrice() : 0.0;
    }

    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get room type name for TableView display
     * Returns the type name if available
     */
    public String getRoomTypeName() {
        if (roomType != null) {
            return roomType.getTypeName();
        }
        return "N/A";
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", roomNumber='" + roomNumber + '\'' +
                ", type='" + (roomType != null ? roomType.getTypeName() : "N/A") + '\'' +
                ", floor=" + floor +
                ", status='" + status + '\'' +
                ", price=" + getPrice() +
                '}';
    }
}
