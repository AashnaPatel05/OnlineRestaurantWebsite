package entities;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * CART ITEM ENTITY CLASS - Shopping Cart Management
 * 
 * PURPOSE:
 * This class represents a single item in a customer's shopping cart.
 * It combines cart table data with food details for easy display.
 * 
 * WHY THIS CLASS EXISTS:
 * When a customer adds food to cart, we need to track:
 * - Which user added it (userId)
 * - Which dish they want (foodId)
 * - How many they want (quantity)
 * - What it costs (price - for calculating total)
 * 
 * DATABASE RELATIONSHIP:
 * This class is a "JOIN" between cart table and food table.
 * Instead of making two database queries, we get all data in one query.
 * This improves performance and reduces database load.
 * 
 * @author Group 10 - Online Restaurant Project
 * @version 1.0
 * @since 2024
 */
public class CartItem implements Serializable {
    
    // FIELDS FROM CART TABLE
    
    /*
     * CART ITEM ID: Unique identifier for this cart entry.
     * Primary key of the cart table.
     * Auto-generated when item is added to cart.
     */
    private int cartId;
    
    /*
     * CUSTOMER ID: Which user added this to their cart?
     * Foreign key referencing users(user_id).
     * Used to retrieve cart items for logged-in user.
     */
    private int userId;
    
    /*
     * DISH ID: Which menu item did customer add?
     * Foreign key referencing food(food_id).
     * Used to get price and name of the dish.
     */
    private int foodId;
    
    /*
     * QUANTITY: How many of this dish does customer want?
     * Minimum: 1 (cannot add zero or negative)
     * Can be increased/decreased on cart page.
     */
    private int quantity;
    
    // FIELDS FROM FOOD TABLE (DENORMALIZED) 
    // These fields come from the food table but are stored here for display
    // This avoids an extra database query when showing the cart
    
    /*
     * DISH NAME: Name of the food item (from food table)
     * Example: "Samosa", "Gujarati Thali", "Masala Dosa"
     * Used to display what the customer ordered.
     */
    private String foodName;
    
    /*
     * UNIT PRICE: Cost of one portion (from food table)
     * Stored as BigDecimal for accurate multiplication with quantity.
     * Example: £4.99 for Samosa
     */
    private BigDecimal price;
    
    /*
     * DISH PHOTO: Path to food image (from food table)
     * Used to show picture of dish in cart.
     * Enhances user experience with visual confirmation.
     */
    private String imageUrl;
    
    // CONSTRUCTORS
    
    /**
     * DEFAULT CONSTRUCTOR
     * Required for JavaBeans specification.
     * Sets default quantity to 1 (customer wants one portion).
     */
    public CartItem() {
        // When customer adds item without specifying quantity
        // Default to 1 portion of that dish
        this.quantity = 1;
    }
    
    /**
     * PARAMETERIZED CONSTRUCTOR
     * Creates cart item with core data (without food details).
     * Food details will be filled by DAO using JOIN query.
     * 
     * @param cartId   - Unique cart item ID
     * @param userId   - Customer who added this item
     * @param foodId   - Which dish was added
     * @param quantity - How many portions
     */
    public CartItem(int cartId, int userId, int foodId, int quantity) {
        this.cartId = cartId;
        this.userId = userId;
        this.foodId = foodId;
        this.quantity = quantity;
        // foodName, price, imageUrl will be set separately
        // They come from the food table via JOIN query
    }
    
    // GETTERS AND SETTERS
    
    public int getCartId() {
        return cartId;
    }
    
    public void setCartId(int cartId) {
        this.cartId = cartId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getFoodId() {
        return foodId;
    }
    
    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getFoodName() {
        return foodName;
    }
    
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    // BUSINESS LOGIC METHODS
    
    /**
     * CALCULATE SUBTOTAL: price × quantity
     * Example: If customer wants 3 Samosas at £3.99 each = £11.97
     * 
     * WHY USE BigDecimal: Avoids floating-point precision errors.
     * Never use double for money - 0.1 + 0.2 != 0.3 in binary!
     * 
     * @return total cost for this line item
     */
    public BigDecimal getSubtotal() {
        if (price != null && quantity > 0) {
            // Multiply unit price by quantity
            // Example: £3.99 × 3 = £11.97
            return price.multiply(new BigDecimal(quantity));
        }
        // Safety check: if price is null or quantity is 0, return £0.00
        return BigDecimal.ZERO;
    }
    
    /**
     * FORMAT SUBTOTAL: Returns subtotal with £ symbol for display.
     * Used in cart.xhtml to show item totals.
     * 
     * EXAMPLE: getSubtotal() returns 11.97, this returns "£11.97"
     * 
     * @return formatted subtotal string (e.g., "£11.97")
     */
    public String getFormattedSubtotal() {
        return "£" + getSubtotal().toString();
    }
    
    /**
     * FORMAT PRICE: Returns unit price with £ symbol.
     * Used in cart.xhtml to show price per portion.
     * 
     * @return formatted price string (e.g., "£3.99")
     */
    public String getFormattedPrice() {
        return "£" + (price != null ? price.toString() : "0.00");
    }
    
    /**
     * DEBUGGING: String representation of cart item.
     * Used for logging when troubleshooting cart issues.
     * 
     * @return formatted string with cart details
     */
    @Override
    public String toString() {
        return "CartItem{" +
                "cartId=" + cartId +
                ", foodName='" + foodName + '\'' +
                ", quantity=" + quantity +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}