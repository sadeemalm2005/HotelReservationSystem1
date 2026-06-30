package hotelreservationsystem1.exceptions;

/**
 * Custom exception for database-related errors
 * Demonstrates Exception Handling concept
 */
public class DatabaseException extends Exception {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
