package dao;

import entities.Food;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FoodDAO - Data Access Object for Food items
 *
 * PURPOSE:
 * This class handles ALL database operations related to food/menu items.
 * It follows the DAO pattern - separating database logic from business logic.
 *
 * CRUD OPERATIONS:
 * - CREATE : addFood()
 * - READ   : getAllFood(), getFoodById(), searchFood()
 * - UPDATE : updateFood()
 * - DELETE : deleteFood()
 *
 * DATABASE TABLE: food
 * Columns: food_id, name, category, description, price, image_url, is_available
 *
 * @author Group 10 - Online Restaurant Project
 * @version 1.0
 * @since 2026
 */
public class FoodDAO {

    // ===================== CREATE =====================

    /**
     * Adds a new food item to the database.
     * Used by admin to add new dishes to the menu.
     *
     * @param food - Food object with all dish details
     * @return true if successfully added, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean addFood(Food food) throws SQLException {

        String sql = "INSERT INTO food (name, category, description, price, image_url, is_available) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, food.getName());
            stmt.setString(2, food.getCategory());
            stmt.setString(3, food.getDescription());
            stmt.setBigDecimal(4, food.getPrice());
            stmt.setString(5, food.getImageUrl());
            stmt.setBoolean(6, food.isIsAvailable());

            return stmt.executeUpdate() > 0;
        }
    }

    // ===================== READ =====================

    /**
     * Retrieves ALL food items from the database.
     * Used by MenuBean to display the complete menu to customers.
     * Only returns available items (is_available = true).
     *
     * @return List of all Food objects from database
     * @throws SQLException if database error occurs
     */
    public List<Food> getAllFood() throws SQLException {

        List<Food> list = new ArrayList<>();

        // Only show available items to customers
        String sql = "SELECT * FROM food WHERE is_available = 1 ORDER BY category, name";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(extractFood(rs));
            }
        }

        return list;
    }

    /**
     * Retrieves ALL food items including unavailable ones.
     * Used by admin panel to manage the complete menu.
     *
     * @return List of ALL Food objects from database
     * @throws SQLException if database error occurs
     */
    public List<Food> getAllFoodAdmin() throws SQLException {

        List<Food> list = new ArrayList<>();

        String sql = "SELECT * FROM food ORDER BY category, name";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(extractFood(rs));
            }
        }

        return list;
    }

    /**
     * Retrieves a single food item by its ID.
     * Used when adding items to cart or viewing dish details.
     *
     * @param id - food_id to search for
     * @return Food object if found, null if not found
     * @throws SQLException if database error occurs
     */
    public Food getFoodById(int id) throws SQLException {

        String sql = "SELECT * FROM food WHERE food_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractFood(rs);
                }
            }
        }

        return null;
    }

    /**
     * Searches food items by name keyword.
     * Used for AJAX search functionality on menu page.
     * Case-insensitive search using LOWER() function.
     *
     * EXAMPLE: searchFood("paneer") returns Paneer Tikka, Paneer Butter Masala etc.
     *
     * @param keyword - search term entered by customer
     * @return List of matching Food objects
     * @throws SQLException if database error occurs
     */
    public List<Food> searchFood(String keyword) throws SQLException {

        List<Food> list = new ArrayList<>();

        // Search in name AND description, case-insensitive
        String sql = "SELECT * FROM food WHERE "
                   + "(LOWER(name) LIKE ? OR LOWER(description) LIKE ? "
                   + "OR LOWER(category) LIKE ?) "
                   + "AND is_available = true "
                   + "ORDER BY name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchTerm = "%" + keyword.toLowerCase() + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFood(rs));
                }
            }
        }

        return list;
    }

    /**
     * Retrieves all food items in a specific category.
     * Used to display category-filtered menu sections.
     *
     * @param category - category name (e.g., "Starters", "Desserts")
     * @return List of Food objects in that category
     * @throws SQLException if database error occurs
     */
    public List<Food> getFoodByCategory(String category) throws SQLException {

        List<Food> list = new ArrayList<>();

        String sql = "SELECT * FROM food WHERE LOWER(category) = LOWER(?) "
                   + "AND is_available = 1 ORDER BY name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFood(rs));
                }
            }
        }

        return list;
    }

    // ===================== UPDATE =====================

    /**
     * Updates an existing food item in the database.
     * Used by admin to edit dish details, price, or availability.
     *
     * @param food - Food object with updated details (must have valid foodId)
     * @return true if successfully updated, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean updateFood(Food food) throws SQLException {

        String sql = "UPDATE food SET name=?, category=?, description=?, "
                   + "price=?, image_url=?, is_available=? "
                   + "WHERE food_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, food.getName());
            stmt.setString(2, food.getCategory());
            stmt.setString(3, food.getDescription());
            stmt.setBigDecimal(4, food.getPrice());
            stmt.setString(5, food.getImageUrl());
            stmt.setBoolean(6, food.isIsAvailable());
            stmt.setInt(7, food.getFoodId());

            return stmt.executeUpdate() > 0;
        }
    }

    // ===================== DELETE =====================

    /**
     * Deletes a food item from the database permanently.
     * Used by admin to remove dishes from the menu.
     * Note: Consider using updateFood() to set is_available=false instead.
     *
     * @param id - food_id of item to delete
     * @return true if successfully deleted, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean deleteFood(int id) throws SQLException {

        String sql = "DELETE FROM food WHERE food_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;
        }
    }

    // ===================== HELPER METHODS =====================

    /**
     * Converts a database ResultSet row into a Food object.
     * Called internally by all READ methods above.
     * Centralizes the mapping logic so it only needs to be written once.
     *
     * DATABASE COLUMN → JAVA FIELD MAPPING:
     * food_id      → foodId
     * name         → name
     * category     → category
     * description  → description
     * price        → price
     * image_url    → imageUrl
     * is_available → isAvailable
     *
     * @param rs - ResultSet positioned at current row
     * @return populated Food object
     * @throws SQLException if column name doesn't exist
     */
    private Food extractFood(ResultSet rs) throws SQLException {

        Food food = new Food();

        food.setFoodId(rs.getInt("food_id"));
        food.setName(rs.getString("name"));
        food.setCategory(rs.getString("category"));
        food.setDescription(rs.getString("description"));
        food.setPrice(rs.getBigDecimal("price"));
        food.setImageUrl(rs.getString("image_url"));
        food.setIsAvailable(rs.getBoolean("is_available"));

        return food;
    }
}