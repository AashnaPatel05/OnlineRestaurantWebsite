package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class manages database connection for the application
 * It provides methods to connect close and test database connections
 * Centralizing this logic avoids repeating connection code in multiple classes
 */
public class DatabaseUtil {
    
    /**
     * Database connection details
     * URL contains host port and database name
     */
    private static final String DATABASE_URL = "jdbc:derby://localhost:1527/restaurant_db";
    
    /**
     * Database username and password
     * In real applications these should not be hard coded
     */
    private static final String USERNAME = "app";
    private static final String PASSWORD = "app";
    
    /**
     * JDBC driver class for Apache Derby
     */
    private static final String DRIVER_CLASS = "org.apache.derby.jdbc.ClientDriver";
    
    /**
     * Static block loads database driver when class is first used
     */
    static {
        try {
            
            /**
             * Load JDBC driver so application can connect to database
             */
            Class.forName(DRIVER_CLASS);
            
            System.out.println("Database driver loaded successfully");
            
        } catch (ClassNotFoundException e) {
            
            /**
             * Error occurs if driver is missing
             */
            System.out.println("Failed to load database driver");
            e.printStackTrace();
        }
    }
    
    /**
     * This method creates and returns a database connection
     */
    public static Connection getConnection() throws SQLException {
        try {
            
            /**
             * Create connection using URL username and password
             */
            Connection conn = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            
            return conn;
            
        } catch (SQLException e) {
            
            /**
             * Print error details if connection fails
             */
            System.out.println("Database connection failed");
            System.out.println("Error message: " + e.getMessage());
            
            throw e;
        }
    }
    
    /**
     * This method closes database connection safely
     */
    public static void closeConnection(Connection connection) {
        
        /**
         * Check if connection is not null before closing
         */
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                
                /**
                 * Print error if closing fails
                 */
                System.out.println("Failed to close connection");
                System.out.println("Error message: " + e.getMessage());
            }
        }
    }
    
    /**
     * This method tests if database connection is working
     */
    public static boolean testConnection() {
        
        try (Connection conn = getConnection()) {
            
            /**
             * Check if connection is valid
             */
            return conn != null && !conn.isClosed();
            
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * This method returns database URL for debugging
     */
    public static String getDatabaseUrl() {
        return DATABASE_URL;
    }
    
    /**
     * This method returns database username for debugging
     */
    public static String getDatabaseUsername() {
        return USERNAME;
    }
}