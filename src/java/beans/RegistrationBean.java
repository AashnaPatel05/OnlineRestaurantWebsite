package beans;

import dao.UserDAO;
import entities.User;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * Handles user registration process
 */
@Named("registrationBean")
@RequestScoped
public class RegistrationBean {

    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String phone;
    private String address;

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[0-9]{10,11}$");

    // register new user
    public String register() {

        if (!validateInput()) {
            return "register";
        }

        try {
            UserDAO userDAO = new UserDAO();

            // check username
            if (userDAO.userNameExists(username)) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Username already exists.", null));
                return "register";
            }

            // check email
            if (userDAO.emailExists(email)) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Email already registered.", null));
                return "register";
            }

            // create user
            User newUser = new User();
            newUser.setUserName(username);   // IMPORTANT FIX
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setFullName(fullName);
            newUser.setPhone(phone);
            newUser.setAddress(address);
            newUser.setRole("customer");

            boolean registered = userDAO.registerUser(newUser);

            if (registered) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Registration successful!", null));

                return "login?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Registration failed.", null));

                return "register";
            }

        } catch (SQLException e) {
            e.printStackTrace();

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Database error.", null));

            return "register";
        }
    }

    // validate form input
    private boolean validateInput() {

        if (username == null || username.trim().length() < 3) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Username must be at least 3 characters.", null));
            return false;
        }

        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid email address.", null));
            return false;
        }

        if (password == null || password.length() < 6) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Password must be at least 6 characters.", null));
            return false;
        }

        if (!password.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Passwords do not match.", null));
            return false;
        }

        if (fullName == null || fullName.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Full name is required.", null));
            return false;
        }

        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Invalid phone number.", null));
            return false;
        }

        return true;
    }

    // getters & setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}