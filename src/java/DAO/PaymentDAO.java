package dao;

import util.DatabaseUtil;
import java.sql.*;

/**
 * This class handles all database operations related to payments
 * It is used to insert update and fetch payment records from the database
 */
public class PaymentDAO {
    
    /**
     * This method adds a new payment record into the payment table
     * It stores details like order id user id amount payment method and transaction id
     * It returns true if the payment is successfully added
     */
    public boolean addPayment(int orderId, int userId, double amount, 
                               String paymentMethod, String transactionId) throws SQLException {
        
        /**
         * SQL query used to insert a new payment record
         */
        String sql = "INSERT INTO payment (order_id, user_id, amount, payment_method, " +
                     "payment_status, transaction_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        /**
         * Create database connection and prepare the SQL statement
         */
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            /**
             * Set values for each placeholder in the SQL query
             */
            stmt.setInt(1, orderId);
            stmt.setInt(2, userId);
            stmt.setDouble(3, amount);
            stmt.setString(4, paymentMethod);
            
            /**
             * Set default payment status as completed
             */
            stmt.setString(5, "completed");
            
            /**
             * Set transaction id which may come from payment gateway
             */
            stmt.setString(6, transactionId);
            
            /**
             * Execute the query and return true if insertion is successful
             */
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * This method retrieves payment details using order id
     * It returns a result set containing payment information
     */
    public ResultSet getPaymentByOrderId(int orderId) throws SQLException {
        
        /**
         * SQL query to fetch payment details for a given order id
         */
        String sql = "SELECT * FROM payment WHERE order_id = ?";
        
        /**
         * Create connection and prepare statement
         */
        Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        /**
         * Set order id value in the query
         */
        stmt.setInt(1, orderId);
        
        /**
         * Execute query and return result set
         */
        return stmt.executeQuery();
    }
    
    /**
     * This method updates the payment status of a specific payment
     * Status can be pending completed or failed
     */
    public boolean updatePaymentStatus(int paymentId, String status) throws SQLException {
        
        /**
         * SQL query to update payment status
         */
        String sql = "UPDATE payment SET payment_status = ? WHERE payment_id = ?";
        
        /**
         * Create connection and prepare statement
         */
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            /**
             * Set new status value
             */
            stmt.setString(1, status);
            
            /**
             * Set payment id to identify the record
             */
            stmt.setInt(2, paymentId);
            
            /**
             * Execute update and return result
             */
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * This method checks whether a payment exists for a given order id
     * It returns true if at least one payment record is found
     */
    public boolean paymentExists(int orderId) throws SQLException {
        
        /**
         * SQL query to count number of payments for an order
         */
        String sql = "SELECT COUNT(*) FROM payment WHERE order_id = ?";
        
        /**
         * Create connection and prepare statement
         */
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            /**
             * Set order id value
             */
            stmt.setInt(1, orderId);
            
            /**
             * Execute query and get result
             */
            ResultSet rs = stmt.executeQuery();
            
            /**
             * Check if result is available and count is greater than zero
             */
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        
        /**
         * Return false if no payment exists
         */
        return false;
    }
}