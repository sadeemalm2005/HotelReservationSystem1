package hotelreservationsystem1.exceptions;

/**
 * Custom exception for reservation conflicts
 * Thrown when a room is already booked for the requested dates
 * Demonstrates Exception Handling concept
 */
public class ReservationConflictException extends Exception {

    public ReservationConflictException(String message) {
        super(message);
    }

    public ReservationConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
