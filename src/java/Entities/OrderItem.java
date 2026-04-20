package entities;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ORDER ITEM ENTITY CLASS - Individual Items Within an Order
 * 
 * PURPOSE:
 * This class represents a single line item in a customer's order.
 * Each order can have multiple order items (one for each dish).
 * 
 * WHY THIS CLASS EXISTS:
 * When a customer places an order, we need to store which dishes they ordered
 * and at what price. This is important because menu prices might change later,
 * but the order receipt should show the price at time of purchase.
 * 
 * DATABASE RELATIONSHIP:
 * - One Order can have MANY OrderItems (One-to-Many)
 * - One Food item can appear in MANY OrderItems (One-to-Many)
 * 
 * PRICE SNAPSHOT PATTERN:
 * This class stores price_at_time which is a copy of the food price
 * at the moment of ordering. This is a design pattern called "snapshot"
 * - it preserves historical data even if menu prices change later.
 * 
 * @author Group 10 - Online Restaurant Project
 * @version 1.0
 * @since 2024
 * 
 */
public class OrderItem implements Serializable {
    
    // FIELDS FROM ORDER_ITEMS TABLE
    
    /*
     * ORDER ITEM ID: Unique identifier for this line item.
     * Primary key of order_items table.
     * Auto-generated when order is placed.
     */
    private int orderItemId;
    
    /*
     * ORDER ID: Which order does this item belong to?
     * Foreign key referencing orders(order_id).
     * Used to group items under their parent order.
     */
    private int orderId;
    
    /*
     * DISH ID: Which menu item was ordered?
     * Foreign key referencing food(food_id).
     * Used to get dish name and image for display.
     */
    private int foodId;
    
    /*
     * QUANTITY: How many portions of this dish?
     * Customer might order multiple of same dish.
     * Minimum: 1
     */
    private int quantity;
    
    /*
     * PRICE SNAPSHOT: What was the price when ordered?
     * IMPORTANT: This is a COPY of the food price at order time.
     * If menu price changes later, this stays the same.
     * This preserves the correct amount the customer paid.
     */
    private BigDecimal priceAtTime;
    
    // TRANSIENT FIELDS (Not stored in database)
    // These come from food table for display purposes only
    
    /*
     * DISH NAME: Name of the food item (from food table)
     * Used on order confirmation and history pages.
     * Example: "Gujarati Thali", "Masala Dosa"
     */
    private String foodName;
    
    /*
     * DISH PHOTO: Path to food image (from food table)
     * Shows picture of what customer ordered.
     * Enhances order history visual appeal.
     */
    private String foodImage;
    
    // CONSTRUCTORS
    
    /**
     * DEFAULT CONSTRUCTOR
     * Required for JavaBeans specification.
     * Sets default quantity to 1.
     */
    public OrderItem() {
        this.quantity = 1;
    }
    
    /**
     * PARAMETERIZED CONSTRUCTOR
     * Creates order item with core data.
     * Called when converting cart items to order items during checkout.
     * 
     * @param orderId     - Parent order ID
     * @param foodId      - Which dish was ordered
     * @param quantity    - How many portions
     * @param priceAtTime - Price at time of order (from food table)
     */
    public OrderItem(int orderId, int foodId, int quantity, BigDecimal priceAtTime) {
        this.orderId = orderId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
    }
    
    // GETTERS AND SETTERS
    
    public int getOrderItemId() {
        return orderItemId;
    }
    
    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }
    
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
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
    
    public BigDecimal getPriceAtTime() {
        return priceAtTime;
    }
    
    public void setPriceAtTime(BigDecimal priceAtTime) {
        this.priceAtTime = priceAtTime;
    }
    
    public String getFoodName() {
        return foodName;
    }
    
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
    
    public String getFoodImage() {
        return foodImage;
    }
    
    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }
    
    // BUSINESS LOGIC METHODS
    
    /**
     * CALCULATE SUBTOTAL: price × quantity for this line item.
     * Uses priceAtTime (snapshot) not current food price.
     * This ensures customer receipt matches what they actually paid.
     * 
     * Example: 2 × Gulab Jamun at £3.99 each = £7.98
     * 
     * @return total cost for this order line
     */
    public BigDecimal getSubtotal() {
        if (priceAtTime != null && quantity > 0) {
            return priceAtTime.multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * FORMAT PRICE: Returns unit price with £ symbol.
     * Used on order confirmation page.
     * 
     * @return formatted price (e.g., "£3.99")
     */
    public String getFormattedPrice() {
        return "£" + (priceAtTime != null ? priceAtTime.toString() : "0.00");
    }
    
    /**
     * FORMAT SUBTOTAL: Returns line total with £ symbol.
     * Used on order confirmation and history pages.
     * 
     * @return formatted subtotal (e.g., "£7.98")
     */
    public String getFormattedSubtotal() {
        return "£" + getSubtotal().toString();
    }
    
    /**
     * DEBUGGING: String representation of order item.
     * Used for logging and troubleshooting.
     * 
     * @return formatted string with order item details
     */
    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", foodId=" + foodId +
                ", quantity=" + quantity +
                ", priceAtTime=" + priceAtTime +
                '}';
    }
}