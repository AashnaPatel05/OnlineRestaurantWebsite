package dao;

import entities.User;
import util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles all database operations related to users
 * It is used for creating reading updating and deleting user records
 */
public class UserDAO {
    
    /**
     * This method is used to register a new user in the database
     * It takes user object and stores all its details into users table
     */
    public boolean registerUser(User user) throws SQLException {
        
        /**
         * SQL query to insert a new user record
         */
        String sql = "INSERT INTO users (userName, email, password, full_name, phone, address, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        /**
         * Create connection and prepare statement with generated keys
         */
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            /**
             * Set all values from user object into query
             */
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmail());
            
            /**
             * Password is stored directly here but in real applications it should be hashed
             */
            stmt.setString(3, user.getPassword());
            
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getAddress());
            
            /**
             * If role is not provided then default role is customer
             */
            stmt.setString(7, user.getRole() != null ? user.getRole() : "customer");
            
            /**
             * Execute insert query
             */
            int rowsAffected = stmt.executeUpdate();
            
            /**
             * If user is inserted successfully then get generated user id
             */
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserID(generatedKeys.getInt(1));
                }
                return true;
            }
            
            /**
             * Return false if insertion fails
             */
            return false;
        }
    }
    
    /**
     * This method finds a user using userName
     * Returns user object if found otherwise null
     */
    public User findUserByUserName(String userName) throws SQLException {
        
        /**
         * SQL query to search user by userName
         */
        String sql = "SELECT * FROM users WHERE userName = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            /**
             * Set username value
             */
            stmt.setString(1, userName);
            
            /**
             * Execute query
             */
            ResultSet rs = stmt.executeQuery();
            
            /**
             * If user found then convert result into user object
             */
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        }
        
        /**
         * Return null if no user found
         */
        return null;
    }
    
    /**
     * This method finds a user using email
     */
    public User findUserByEmail(String email) throws SQLException {
        
        /**
         * SQL query to search user by email
         */
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        }
        
        return null;
    }
    
    /**
     * This method gets user details using user id
     */
    public User getUserByuserID(int userID) throws SQLException {
        
        /**
         * SQL query to get user by id
         */
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        }
        
        return null;
    }
    
    /**
     * This method returns all users from database
     * Mostly used by admin
     */
    public List<User> getAllUsers() throws SQLException {
        
        /**
         * Create list to store users
         */
        List<User> users = new ArrayList<>();
        
        /**
         * SQL query to fetch all users
         */
        String sql = "SELECT * FROM users ORDER BY user_id DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            /**
             * Loop through all records and convert them into user objects
             */
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        
        return users;
    }
    
    /**
     * This method updates user profile information
     */
    public boolean updateUser(User user) throws SQLException {
        
        /**
         * SQL query to update user details
         */
        String sql = "UPDATE users SET full_Name = ?, phone = ?, address = ? WHERE user_ID = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getAddress());
            stmt.setInt(4, user.getUserID());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * This method updates user password
     */
    public boolean updatePassword(int userID, String newPassword) throws SQLException {
        
        /**
         * SQL query to update password
         */
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword);
            stmt.setInt(2, userID);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * This method deletes a user from database using user id
     */
    public boolean deleteUser(int userID) throws SQLException {
        
        /**
         * SQL query to delete user
         */
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userID);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * This helper method converts database result into user object
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        
        /**
         * Create new user object
         */
        User user = new User();
        
        /**
         * Set all values from result set into user object
         */
        user.setUserID(rs.getInt("user_ID"));
        user.setUserName(rs.getString("userName"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        
        return user;
    }
    
    /**
     * This method checks if username already exists in database
     */
    public boolean userNameExists(String userName) throws SQLException {
        return findUserByUserName(userName) != null;
    }
    
    /**
     * This method checks if email already exists in database
     */
    public boolean emailExists(String email) throws SQLException {
        return findUserByEmail(email) != null;
    }
}