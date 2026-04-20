package beans;

import dao.UserDAO;
import entities.User;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Handles login, logout and session
 */
@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private String username;
    private String password;
    private User currentUser;
    private boolean loggedIn;

    public LoginBean() {
        loggedIn = false;
    }

    // login method
    public String login() {

        // check empty fields
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Enter username and password"));
            return "login";
        }

        try {
            UserDAO userDAO = new UserDAO();

            // FIXED METHOD NAME (very important)
            User user = userDAO.findUserByUserName(username);

            // check user + password
            if (user != null && user.getPassword().equals(password)) {

                currentUser = user;
                loggedIn = true;

                // store in session
                FacesContext.getCurrentInstance().getExternalContext()
                        .getSessionMap().put("user", currentUser);

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Welcome " + currentUser.getFullName()));

                return "index?faces-redirect=true";

            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Invalid username or password"));
                return "login";
            }

        } catch (SQLException e) {
            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Database error"));
            return "login";
        }
    }

    // logout
    public String logout() {

        FacesContext.getCurrentInstance().getExternalContext()
                .invalidateSession();

        loggedIn = false;
        currentUser = null;

        return "index?faces-redirect=true";
    }

    // check login
    public boolean isLoggedIn() {
        return loggedIn;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // check admin
    public boolean isAdmin() {
        return loggedIn && currentUser != null && currentUser.isAdmin();
    }

    // getters/setters
    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}