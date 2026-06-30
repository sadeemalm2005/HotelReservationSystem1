package hotelreservationsystem1.models;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Reservation class
 * Demonstrates Composition - contains Guest and Room objects
 * Demonstrates Collections - uses ArrayList for services
 */
public class Reservation implements Comparable<Reservation> {
    private int reservationId;
    private Guest guest;  // Composition - Reservation HAS-A Guest
    private Room room;    // Composition - Reservation HAS-A Room
    private Date checkInDate;
    private Date checkOutDate;
    private double totalPrice;
    private String status;
    private Date bookingDate;
    private List<Service> services;  // Collection - List of services

    // Constants for reservation status
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CHECKED_IN = "CHECKED_IN";
    public static final String STATUS_CHECKED_OUT = "CHECKED_OUT";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // Constructors
    public Reservation() {
        this.status = STATUS_PENDING;
        this.bookingDate = new Date();
        this.services = new ArrayList<>();  // Initialize ArrayList
    }

    public Reservation(Guest guest, Room room, Date checkInDate, Date checkOutDate) {
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = STATUS_PENDING;
        this.bookingDate = new Date();
        this.services = new ArrayList<>();
        this.totalPrice = calculateTotalPrice();
    }

    /**
     * Implements Comparable interface
     * Natural ordering by check-in date (ascending)
     */
    @Override
    public int compareTo(Reservation other) {
        return this.checkInDate.compareTo(other.checkInDate);
    }

    /**
     * Calculate number of nights between two dates
     */
    public long getNumberOfNights() {
        if (checkInDate == null || checkOutDate == null) {
            return 0;
        }
        // Calculate difference in milliseconds, then convert to days
        long diffInMillis = checkOutDate.getTime() - checkInDate.getTime();
        return diffInMillis / (1000 * 60 * 60 * 24);
    }

    /**
     * Calculate total price based on number of nights and room price
     * Plus any additional services
     */
    public double calculateTotalPrice() {
        if (checkInDate == null || checkOutDate == null || room == null) {
            return 0.0;
        }

        long numberOfNights = getNumberOfNights();
        double roomTotal = numberOfNights * room.getPrice();

        // Add service prices
        double servicesTotal = 0.0;
        for (Service service : services) {
            servicesTotal += service.getPrice();
        }

        return roomTotal + servicesTotal;
    }

    /**
     * Add a service to this reservation
     * Demonstrates ArrayList usage
     */
    public void addService(Service service) {
        if (service != null) {
            services.add(service);
            this.totalPrice = calculateTotalPrice();
        }
    }

    /**
     * Remove a service from this reservation
     */
    public void removeService(Service service) {
        if (services.remove(service)) {
            this.totalPrice = calculateTotalPrice();
        }
    }

    /**
     * Check if reservation is active
     */
    public boolean isActive() {
        return STATUS_CHECKED_IN.equals(status) || STATUS_CONFIRMED.equals(status);
    }

    /**
     * Check if reservation can be cancelled
     */
    public boolean canBeCancelled() {
        return STATUS_PENDING.equals(status) || STATUS_CONFIRMED.equals(status);
    }

    // Getters and Setters
    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
        this.totalPrice = calculateTotalPrice();
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
        this.totalPrice = calculateTotalPrice();
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
        this.totalPrice = calculateTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
        this.totalPrice = calculateTotalPrice();
    }

    /**
     * Get guest name for TableView display
     * Returns the full name of the guest if available
     */
    public String getGuestName() {
        if (guest != null) {
            return guest.getFullName();
        }
        return "N/A";
    }

    /**
     * Get room number for TableView display
     * Returns the room number if available
     */
    public String getRoomNumber() {
        if (room != null) {
            return room.getRoomNumber();
        }
        return "N/A";
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", guest=" + (guest != null ? guest.getFullName() : "N/A") +
                ", room=" + (room != null ? room.getRoomNumber() : "N/A") +
                ", checkIn=" + checkInDate +
                ", checkOut=" + checkOutDate +
                ", nights=" + getNumberOfNights() +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", services=" + services.size() +
                '}';
    }
}
