package beans;

import dao.FoodDAO;
import dao.OrderDAO;
import dao.UserDAO;
import entities.Food;
import entities.Order;
import entities.User;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * AdminBean handles admin panel operations like:
 * - managing users
 * - managing food items
 * - managing orders
 */
@Named("adminBean")
@RequestScoped
public class AdminBean {

    private List<User> allUsers;
    private List<Food> allFoodItems;
    private List<Order> allOrders;

    private Food newFood;
    private Food selectedFood;
    private Order selectedOrder;

    private static final String[] ORDER_STATUSES = {
        "pending", "confirmed", "preparing", "delivered", "cancelled"
    };

    private static final String[] CATEGORIES = {
        "Starters", "Main Courses", "Desserts", "Drinks"
    };

    public AdminBean() {
        newFood = new Food();
        loadData();
    }

    // load everything
    public void loadData() {
        loadUsers();
        loadFoodItems();
        loadOrders();
    }

    public void loadUsers() {
        try {
            allUsers = new UserDAO().getAllUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadFoodItems() {
        try {
            allFoodItems = new FoodDAO().getAllFood();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadOrders() {
        try {
            allOrders = new OrderDAO().getAllOrders();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // add new food
    public String addFood() {
        if (!validateFood(newFood)) return null;

        try {
            boolean added = new FoodDAO().addFood(newFood);

            if (added) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Food added successfully"));

                newFood = new Food();
                loadFoodItems();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Failed to add food"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // update food
    public void updateFood() {
        if (selectedFood == null || !validateFood(selectedFood)) return;

        try {
            new FoodDAO().updateFood(selectedFood);
            loadFoodItems();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Food updated"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // delete food
    public void deleteFood(int foodId) {
        try {
            new FoodDAO().deleteFood(foodId);
            loadFoodItems();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Food deleted"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // toggle availability
    public void toggleAvailability(int foodId) {
        try {
            Food food = new FoodDAO().getFoodById(foodId);

            if (food != null) {
                food.setIsAvailable(!food.isIsAvailable());
                new FoodDAO().updateFood(food);
                loadFoodItems();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // update order status
    public void updateOrderStatus() {
        if (selectedOrder == null) return;

        try {
            new OrderDAO().updateOrderStatus(
                    selectedOrder.getOrderId(),
                    selectedOrder.getStatus()
            );

            loadOrders();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Order updated"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // delete user
    public void deleteUser(int userId) {
        try {
            new UserDAO().deleteUser(userId);
            loadUsers();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("User deleted"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // simple validation
    private boolean validateFood(Food food) {

        if (food.getName() == null || food.getName().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Enter food name"));
            return false;
        }

        if (food.getPrice() == null ||
                food.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Invalid price"));
            return false;
        }

        return true;
    }

    // getters & setters

    public List<User> getAllUsers() { return allUsers; }

    public List<Food> getAllFoodItems() { return allFoodItems; }

    public List<Order> getAllOrders() { return allOrders; }

    public Food getNewFood() { return newFood; }

    public void setNewFood(Food newFood) { this.newFood = newFood; }

    public Food getSelectedFood() { return selectedFood; }

    public void setSelectedFood(Food selectedFood) { this.selectedFood = selectedFood; }

    public Order getSelectedOrder() { return selectedOrder; }

    public void setSelectedOrder(Order selectedOrder) { this.selectedOrder = selectedOrder; }

    public String[] getOrderStatuses() { return ORDER_STATUSES; }

    public String[] getCategories() { return CATEGORIES; }
}