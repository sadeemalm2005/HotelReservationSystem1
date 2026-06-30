package hotelreservationsystem1.exceptions;

/**
 * Custom exception for authentication failures
 * Thrown when login credentials are invalid
 * Demonstrates Exception Handling concept
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
