package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection class handles JDBC connection to Oracle database
 * Following JDBC concepts from Chapter 5 slides
 */
public class DatabaseConnection {

    // Database connection parameters
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1522:XE";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "1234";
    private static final String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";

    /**
     * Establishes and returns a connection to the database
     * Following the JDBC pattern from slides: Load driver -> Create connection
     *
     * @return Connection object to Oracle database
     * @throws SQLException if connection fails
     * @throws ClassNotFoundException if Oracle driver not found
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Step 1: Load the JDBC driver (as shown in Chapter 5 slides)
        Class.forName(DRIVER_CLASS);

        // Step 2: Establish a connection
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        return connection;
    }

    /**
     * Closes the database connection
     *
     * @param connection the connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
