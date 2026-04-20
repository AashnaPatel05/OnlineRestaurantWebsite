package dao;

import entities.CartItem;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles all database operations related to the shopping cart
 * It is used to add update retrieve and delete cart items for users
 */
public class CartDAO {
    
    /**
     * This method adds an item to the cart
     * If the item already exists then it updates the quantity
     */
    public void addToCart(int userId, int foodId, int quantity) throws SQLException {
        
        /**
         * SQL query to check if item already exists in cart
         */
        String checkSql = "SELECT * FROM cart WHERE user_id = ? AND food_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            /**
             * Set user id and food id
             */
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, foodId);
            
            ResultSet rs = checkStmt.executeQuery();
            
            /**
             * If item exists then update quantity
             */
            if (rs.next()) {
                int cartId = rs.getInt("cart_id");
                int currentQuantity = rs.getInt("quantity");
                
                /**
                 * Add new quantity to existing quantity
                 */
                int newQuantity = currentQuantity + quantity;
                
                updateCartQuantity(cartId, newQuantity);
            } else {
                
                /**
                 * If item does not exist then insert new record
                 */
                String insertSql = "INSERT INTO cart (user_id, food_id, quantity) VALUES (?, ?, ?)";
                
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, foodId);
                    insertStmt.setInt(3, quantity);
                    
                    insertStmt.executeUpdate();
                }
            }
        }
    }
    
    /**
     * This method retrieves all cart items for a specific user
     */
    public List<CartItem> getCartItems(int userId) throws SQLException {
        
        List<CartItem> cartItems = new ArrayList<>();
        
        /**
         * SQL query to join cart with food table to get item details
         */
        String sql = "SELECT c.cart_id, c.user_id, c.food_id, c.quantity, " +
                     "f.name as food_name, f.price, f.image_url " +
                     "FROM cart c JOIN food f ON c.food_id = f.food_id " +
                     "WHERE c.user_id = ? ORDER BY c.added_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            /**
             * Convert each record into CartItem object
             */
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setCartId(rs.getInt("cart_id"));
                item.setUserId(rs.getInt("user_id"));
                item.setFoodId(rs.getInt("food_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setFoodName(rs.getString("food_name"));
                item.setPrice(rs.getBigDecimal("price"));
                item.setImageUrl(rs.getString("image_url"));
                
                cartItems.add(item);
            }
        }
        
        return cartItems;
    }
    
    /**
     * This method returns total number of items in the cart
     */
    public int getCartItemCount(int userId) throws SQLException {
        
        /**
         * SQL query to calculate total quantity
         */
        String sql = "SELECT SUM(quantity) as total FROM cart WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            /**
             * Return total count if exists
             */
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        
        return 0;
    }
    
    /**
     * This method calculates total price of items in the cart
     */
    public double getCartTotal(int userId) throws SQLException {
        
        /**
         * SQL query to calculate total cost using quantity and price
         */
        String sql = "SELECT SUM(c.quantity * f.price) as total " +
                     "FROM cart c JOIN food f ON c.food_id = f.food_id " +
                     "WHERE c.user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            /**
             * Return total amount
             */
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        
        return 0.0;
    }
    
    /**
     * This method updates quantity of a cart item
     * If quantity becomes zero or less then item is removed
     */
    public void updateCartQuantity(int cartId, int quantity) throws SQLException {
        
        /**
         * If quantity is zero or negative then delete item
         */
        if (quantity <= 0) {
            deleteCartItem(cartId);
        } else {
            
            /**
             * SQL query to update quantity
             */
            String sql = "UPDATE cart SET quantity = ? WHERE cart_id = ?";
            
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, quantity);
                stmt.setInt(2, cartId);
                
                stmt.executeUpdate();
            }
        }
    }
    
    /**
     * This method deletes a specific item from the cart
     */
    public void deleteCartItem(int cartId) throws SQLException {
        
        /**
         * SQL query to delete cart item
         */
        String sql = "DELETE FROM cart WHERE cart_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * This method clears all items from a user's cart
     */
    public void clearCart(int userId) throws SQLException {
        
        /**
         * SQL query to delete all cart items for a user
         */
        String sql = "DELETE FROM cart WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}