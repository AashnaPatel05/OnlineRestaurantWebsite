package beans;

import dao.CartDAO;
import dao.FoodDAO;
import entities.CartItem;
import entities.Food;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * CartBean - Manages Shopping Cart Operations
 *
 * PURPOSE:
 * This bean handles all cart functionality for the restaurant website.
 * Customers can add dishes, update quantities, remove items and checkout.
 *
 * WHY SESSION SCOPED:
 * Cart must persist across multiple pages (menu → cart → checkout).
 * SessionScoped keeps the cart alive for the entire user session.
 *
 * FEATURES:
 * - Add items to cart from menu page
 * - Update item quantities with AJAX
 * - Remove individual items
 * - Clear entire cart
 * - Calculate cart total
 * - Redirect to login if not logged in
 *
 * @author Group 10 - Online Restaurant Project
 * @version 1.0
 * @since 2026
 */
@Named("cartBean")
@SessionScoped
public class CartBean implements Serializable {

    // ===================== FIELDS =====================

    /**
     * LIST OF CART ITEMS: All dishes currently in the customer's cart.
     * Each CartItem contains foodId, quantity, price etc.
     */
    private List<CartItem> cartItems;

    /**
     * ITEM COUNT: Total number of items in cart.
     * Displayed in the header navigation e.g. "Cart (3)"
     */
    private int itemCount;

    /**
     * CART TOTAL: Total price of all items combined.
     * Calculated from database for accuracy.
     */
    private double cartTotal;

    // ===================== CONSTRUCTOR =====================

    /**
     * Default constructor - initialises empty cart.
     * Cart loads from database when user is logged in.
     * Empty lists prevent NullPointerException before login.
     */
    public CartBean() {
        // Initialise with empty values
        // Actual data loads after user logs in
        cartItems = new ArrayList<>();
        itemCount = 0;
        cartTotal = 0.0;
    }

    // ===================== CART OPERATIONS =====================

    /**
     * Loads cart data from database for current user.
     * Called after every cart operation to keep data fresh.
     * Safely handles case when user is not logged in.
     */
    public void loadCart() {

        LoginBean loginBean = getLoginBean();

        if (loginBean != null && loginBean.isLoggedIn()) {

            int userId = loginBean.getCurrentUser().getUserID();

            try {
                CartDAO cartDAO = new CartDAO();

                cartItems = cartDAO.getCartItems(userId);
                itemCount = cartDAO.getCartItemCount(userId);
                cartTotal = cartDAO.getCartTotal(userId);

                // Null safety checks
                if (cartItems == null) {
                    cartItems = new ArrayList<>();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                cartItems = new ArrayList<>();
                itemCount = 0;
                cartTotal = 0.0;
            }

        } else {
            // User not logged in - keep cart empty
            cartItems = new ArrayList<>();
            itemCount = 0;
            cartTotal = 0.0;
        }
    }

    /**
     * Adds a single item to the cart with quantity of 1.
     * Convenience method called from menu page Add to Cart button.
     *
     * EXAMPLE USAGE IN JSF:
     * action="#{cartBean.addToCart(item.foodId)}"
     *
     * @param foodId - ID of the food item to add
     */
    public void addToCart(int foodId) {
        addToCart(foodId, 1);
    }

    /**
     * Adds an item to cart with specified quantity.
     * Redirects to login page if user is not logged in.
     * Refreshes cart data after adding.
     *
     * @param foodId   - ID of the food item to add
     * @param quantity - how many to add (usually 1)
     */
    public void addToCart(int foodId, int quantity) {

        LoginBean loginBean = getLoginBean();

        // Redirect to login if not authenticated
        if (loginBean == null || !loginBean.isLoggedIn()) {
            try {
                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .redirect("login.xhtml");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        int userId = loginBean.getCurrentUser().getUserID();

        try {
            CartDAO cartDAO = new CartDAO();
            cartDAO.addToCart(userId, foodId, quantity);

            // Reload cart to show updated data
            loadCart();

            // Success message for user
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Item added to cart!", null));

        } catch (SQLException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not add item to cart.", null));
        }
    }

    /**
     * Updates the quantity of an existing cart item.
     * Called via AJAX when customer changes quantity on cart page.
     * If quantity is 0 or less, item is removed from cart.
     *
     * EXAMPLE USAGE IN JSF:
     * listener="#{cartBean.updateQuantity(item.cartId, item.quantity)}"
     *
     * @param cartId   - ID of cart record to update
     * @param quantity - new quantity value
     */
    public void updateQuantity(int cartId, int quantity) {

        try {
            if (quantity <= 0) {
                // Remove item if quantity is 0 or negative
                new CartDAO().deleteCartItem(cartId);
            } else {
                new CartDAO().updateCartQuantity(cartId, quantity);
            }
            // Reload to show updated totals
            loadCart();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a single item from the cart.
     * Called when customer clicks Remove button on cart page.
     * Uses AJAX to update cart without full page reload.
     *
     * EXAMPLE USAGE IN JSF:
     * action="#{cartBean.removeFromCart(item.cartId)}"
     *
     * @param cartId - ID of cart record to delete
     */
    public void removeFromCart(int cartId) {

        try {
            new CartDAO().deleteCartItem(cartId);
            // Reload to show updated cart
            loadCart();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears all items from the cart.
     * Called when customer clicks Clear Cart button.
     * Only works if user is logged in.
     */
    public void clearCart() {

        LoginBean loginBean = getLoginBean();

        if (loginBean != null && loginBean.isLoggedIn()) {

            int userId = loginBean.getCurrentUser().getUserID();

            try {
                new CartDAO().clearCart(userId);
                // Reload to show empty cart
                loadCart();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves food details by ID.
     * Used when cart page needs to display food information.
     *
     * @param foodId - ID of food item to retrieve
     * @return Food object or null if not found
     */
    public Food getFoodById(int foodId) {

        try {
            return new FoodDAO().getFoodById(foodId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===================== HELPER METHODS =====================

    /**
     * Gets the LoginBean from JSF context.
     * Required to access current user information.
     * Uses EL expression evaluation to get the session-scoped bean.
     *
     * WHY THIS WAY:
     * Cannot inject LoginBean directly because both are SessionScoped.
     * This approach safely retrieves it from JSF context at runtime.
     *
     * @return LoginBean instance or null if not available
     */
    private LoginBean getLoginBean() {

        FacesContext context = FacesContext.getCurrentInstance();

        if (context == null) return null;

        return context.getApplication()
                .evaluateExpressionGet(context, "#{loginBean}", LoginBean.class);
    }

    /**
     * Checks if the cart is empty.
     * Used in cart.xhtml to show empty cart message.
     *
     * EXAMPLE USAGE IN JSF:
     * rendered="#{cartBean.cartEmpty}"
     *
     * @return true if cart has no items, false if items exist
     */
    public boolean isCartEmpty() {
        return cartItems == null || cartItems.isEmpty();
    }

    // ===================== GETTERS =====================

    /**
     * Gets all cart items for display in cart.xhtml table.
     * @return List of CartItem objects
     */
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    /**
     * Gets total number of items in cart.
     * Displayed in header navigation as Cart (N).
     * @return item count integer
     */
    public int getItemCount() {
        return itemCount;
    }

    /**
     * Gets raw cart total as double.
     * @return total price as double
     */
    public double getCartTotal() {
        return cartTotal;
    }

    /**
     * Gets formatted cart total with currency symbol.
     * Used in cart summary and checkout page.
     *
     * EXAMPLE USAGE IN JSF:
     * value="#{cartBean.formattedCartTotal}"
     *
     * @return formatted string e.g. "£24.97"
     */
    public String getFormattedCartTotal() {
        return String.format("£%.2f", cartTotal);
    }
}