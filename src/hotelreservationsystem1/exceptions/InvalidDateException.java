package hotelreservationsystem1.exceptions;

/**
 * Custom exception for invalid date inputs
 * Thrown when check-out date is before check-in date, or dates are in the past
 * Demonstrates Exception Handling concept
 */
public class InvalidDateException extends Exception {

    public InvalidDateException(String message) {
        super(message);
    }

    public InvalidDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
