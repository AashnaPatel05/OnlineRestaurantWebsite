package beans;

import dao.CartDAO;
import dao.OrderDAO;
import dao.PaymentDAO;
import entities.CartItem;
import entities.Order;
import entities.OrderItem;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * CheckoutBean
 * Handles full checkout process
 */
@Named("checkoutBean")
@ViewScoped
public class CheckoutBean implements Serializable {

    /* BASIC FIELDS */
    private String deliveryAddress;
    private String paymentMethod;

    private List<CartItem> cartItems;
    private double totalAmount;

    /* CARD DETAILS */
    private String cardName;
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;

    /* CONSTRUCTOR */
    public CheckoutBean() {
        loadCartData();
    }

    /* Load cart */
    private void loadCartData() {

        CartBean cartBean = getCartBean();

        if (cartBean != null && !cartBean.isCartEmpty()) {
            cartItems = cartBean.getCartItems();
            totalAmount = cartBean.getCartTotal();
        } else {
            cartItems = new ArrayList<>();
            totalAmount = 0;
        }
    }

    /* PLACE ORDER */
    public String placeOrder() {

        if (!validateCheckout()) {
            return null;
        }

        LoginBean loginBean = getLoginBean();

        if (loginBean == null || !loginBean.isLoggedIn()) {
            addMessage("Please login first");
            return "login?faces-redirect=true";
        }

        int userId = loginBean.getCurrentUser().getUserID();

        try {

            /* CREATE ORDER */
            Order order = new Order();
            order.setUserId(userId);

            /* FIXED TOTAL AMOUNT */
            order.setTotalAmount(
                BigDecimal.valueOf(totalAmount).setScale(2, RoundingMode.HALF_UP)
            );

            order.setDeliveryAddress(deliveryAddress);
            order.setPaymentMethod(paymentMethod);

            /* CREATE ORDER ITEMS */
            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem c : cartItems) {
                OrderItem item = new OrderItem();
                item.setFoodId(c.getFoodId());
                item.setQuantity(c.getQuantity());

                /* FIXED PRICE */
                item.setPriceAtTime(
                    c.getPrice().setScale(2, RoundingMode.HALF_UP)
                );

                orderItems.add(item);
            }

            /* SAVE ORDER */
            int orderId = new OrderDAO().createOrder(order, orderItems);

            if (orderId > 0) {

                /* SAVE PAYMENT */
                String txnId = generateTransactionId();
                new PaymentDAO().addPayment(orderId, userId, totalAmount, paymentMethod, txnId);

                /* CLEAR CART */
                CartBean cartBean = getCartBean();
                if (cartBean != null) {
                    cartBean.clearCart();
                }

                /* SUCCESS MESSAGE */
                addMessage("Your order has been successfully placed. Thank you!");

                return "orderConfirmation?faces-redirect=true";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        addMessage("Order failed");
        return null;
    }

    /* VALIDATION */
    private boolean validateCheckout() {

        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            addMessage("Enter delivery address");
            return false;
        }

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            addMessage("Select payment method");
            return false;
        }

        /* CARD VALIDATION */
        if ("credit_card".equals(paymentMethod)) {

            if (cardName == null || cardName.trim().isEmpty()) {
                addMessage("Enter card name");
                return false;
            }

            if (cardNumber == null || cardNumber.length() < 12) {
                addMessage("Invalid card number");
                return false;
            }

            if (cardExpiry == null || cardExpiry.isEmpty()) {
                addMessage("Enter expiry date");
                return false;
            }

            if (cardCvv == null || cardCvv.length() < 3) {
                addMessage("Invalid CVV");
                return false;
            }
        }

        return true;
    }

    /* HELPER METHODS */

    private void addMessage(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(msg));
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    private CartBean getCartBean() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication()
                .evaluateExpressionGet(context, "#{cartBean}", CartBean.class);
    }

    private LoginBean getLoginBean() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication()
                .evaluateExpressionGet(context, "#{loginBean}", LoginBean.class);
    }

    /* GETTERS & SETTERS */

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<CartItem> getCartItems() { return cartItems; }

    public double getTotalAmount() { return totalAmount; }

    public String getFormattedTotal() {
        return "£" + String.format("%.2f", totalAmount);
    }

    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardExpiry() { return cardExpiry; }
    public void setCardExpiry(String cardExpiry) { this.cardExpiry = cardExpiry; }

    public String getCardCvv() { return cardCvv; }
    public void setCardCvv(String cardCvv) { this.cardCvv = cardCvv; }
}