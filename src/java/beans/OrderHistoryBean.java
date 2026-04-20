package beans;

import dao.OrderDAO;
import entities.Order;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;

import java.sql.SQLException;
import java.util.List;

/**
 * Handles user order history and order details
 */
@Named("orderHistoryBean")
@RequestScoped
public class OrderHistoryBean {

    private List<Order> orders;
    private Order selectedOrder;

    public OrderHistoryBean() {
        loadOrders();
    }

    // load orders for logged-in user
    public void loadOrders() {

        LoginBean loginBean = getLoginBean();

        if (loginBean != null && loginBean.isLoggedIn()) {
            int userId = loginBean.getCurrentUser().getUserID(); // fixed naming

            try {
                OrderDAO orderDAO = new OrderDAO();
                orders = orderDAO.getOrdersByUser(userId);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // get details of one order
    public Order getOrderDetails(int orderId) {
        try {
            return new OrderDAO().getOrderById(orderId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // set selected order
    public void viewOrderDetails(int orderId) {
        selectedOrder = getOrderDetails(orderId);
    }

    // check if user has orders
    public boolean hasOrders() {
        return orders != null && !orders.isEmpty();
    }

    // get LoginBean from session (Jakarta way)
    private LoginBean getLoginBean() {
        FacesContext context = FacesContext.getCurrentInstance();

        return context.getApplication()
                .evaluateExpressionGet(context, "#{loginBean}", LoginBean.class);
    }

    // getters & setters

    public List<Order> getOrders() {
        return orders;
    }

    public Order getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(Order selectedOrder) {
        this.selectedOrder = selectedOrder;
    }
}