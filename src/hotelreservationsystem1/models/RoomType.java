package hotelreservationsystem1.models;

/**
 * RoomType class
 * Demonstrates Interface implementation - implements Comparable
 * Used for sorting room types by price
 */
public class RoomType implements Comparable<RoomType> {
    private int typeId;
    private String typeName;
    private String description;
    private double basePrice;
    private int capacity;
    private String amenities;

    // Constructors
    public RoomType() {
    }

    public RoomType(String typeName, String description, double basePrice, int capacity, String amenities) {
        this.typeName = typeName;
        this.description = description;
        this.basePrice = basePrice;
        this.capacity = capacity;
        this.amenities = amenities;
    }

    /**
     * Implements Comparable interface
     * Natural ordering by base price (ascending)
     */
    @Override
    public int compareTo(RoomType other) {
        return Double.compare(this.basePrice, other.basePrice);
    }

    // Getters and Setters
    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    @Override
    public String toString() {
        return "RoomType{" +
                "typeId=" + typeId +
                ", typeName='" + typeName + '\'' +
                ", basePrice=" + basePrice +
                ", capacity=" + capacity +
                '}';
    }
}
