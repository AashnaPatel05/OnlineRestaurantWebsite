package dao;

import entities.Order;
import entities.OrderItem;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles all database operations related to orders
 * It manages creating orders retrieving them and updating order status
 */
public class OrderDAO {
    
    /**
     * This method creates a new order along with its items
     * It uses transaction to make sure both order and items are saved together
     */
    public int createOrder(Order order, List<OrderItem> items) throws SQLException {
        
        /**
         * Initialize required variables for database operations
         */
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet generatedKeys = null;
        int orderId = -1;
        
        try {
            /**
             * Get database connection and start transaction
             */
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            /**
             * SQL query to insert order details
             */
            String orderSql = "INSERT INTO orders (user_id, total_amount, delivery_address, payment_method, status) " +
                             "VALUES (?, ?, ?, ?, ?)";
            
            orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            
            /**
             * Set order values
             */
            orderStmt.setInt(1, order.getUserId());
            orderStmt.setBigDecimal(2, order.getTotalAmount());
            orderStmt.setString(3, order.getDeliveryAddress());
            orderStmt.setString(4, order.getPaymentMethod());
            
            /**
             * Default order status is pending
             */
            orderStmt.setString(5, Order.STATUS_PENDING);
            
            /**
             * Execute order insert
             */
            orderStmt.executeUpdate();
            
            /**
             * Get generated order id
             */
            generatedKeys = orderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
                order.setOrderId(orderId);
            }
            
            /**
             * SQL query to insert order items
             */
            String itemSql = "INSERT INTO order_items (order_id, food_id, quantity, price_at_time) " +
                            "VALUES (?, ?, ?, ?)";
            
            itemStmt = conn.prepareStatement(itemSql);
            
            /**
             * Loop through each item and add to batch
             */
            for (OrderItem item : items) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, item.getFoodId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setBigDecimal(4, item.getPriceAtTime());
                itemStmt.addBatch();
            }
            
            /**
             * Execute all item inserts together
             */
            itemStmt.executeBatch();
            
            /**
             * Commit transaction if everything is successful
             */
            conn.commit();
            return orderId;
            
        } catch (SQLException e) {
            
            /**
             * Rollback transaction if any error occurs
             */
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            throw e;
            
        } finally {
            
            /**
             * Close all resources properly
             */
            if (orderStmt != null) try { orderStmt.close(); } catch (SQLException e) {}
            if (itemStmt != null) try { itemStmt.close(); } catch (SQLException e) {}
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) {}
            
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseUtil.closeConnection(conn);
            }
        }
    }
    
    /**
     * This method returns all orders for a specific user
     */
    public List<Order> getOrdersByUser(int userId) throws SQLException {
        
        /**
         * Create list to store orders
         */
        List<Order> orders = new ArrayList<>();
        
        /**
         * SQL query to fetch orders by user id
         */
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            /**
             * Convert each record into Order object
             */
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        }
        
        return orders;
    }
    
    /**
     * This method gets a single order using order id
     */
    public Order getOrderById(int orderId) throws SQLException {
        
        /**
         * SQL query to fetch order
         */
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            /**
             * If order exists then also load its items
             */
            if (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                order.setOrderItems(getOrderItems(orderId));
                return order;
            }
        }
        
        return null;
    }
    
    /**
     * This method retrieves all items for a specific order
     */
    public List<OrderItem> getOrderItems(int orderId) throws SQLException {
        
        List<OrderItem> items = new ArrayList<>();
        
        /**
         * SQL query to join order items with food details
         */
        String sql = "SELECT oi.*, f.name as food_name, f.image_url " +
                     "FROM order_items oi JOIN food f ON oi.food_id = f.food_id " +
                     "WHERE oi.order_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            /**
             * Convert result into OrderItem objects
             */
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setFoodId(rs.getInt("food_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPriceAtTime(rs.getBigDecimal("price_at_time"));
                item.setFoodName(rs.getString("food_name"));
                item.setFoodImage(rs.getString("image_url"));
                items.add(item);
            }
        }
        
        return items;
    }
    
    /**
     * This method returns all orders in the system
     * Mostly used by admin
     */
    public List<Order> getAllOrders() throws SQLException {
        
        List<Order> orders = new ArrayList<>();
        
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        }
        
        return orders;
    }
    
    /**
     * This method updates the status of an order
     */
    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        
        /**
         * SQL query to update order status
         */
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * This helper method converts result set into Order object
     */
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        
        Order order = new Order();
        
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setOrderDate(rs.getTimestamp("order_date"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setDeliveryAddress(rs.getString("delivery_address"));
        order.setPaymentMethod(rs.getString("payment_method"));
        
        return order;
    }
}