/* ============================================
   SPICE GARDEN RESTAURANT - FORM VALIDATION
   Validates login, registration, checkout forms
   ============================================ */

// Wait for the page to load completely
document.addEventListener('DOMContentLoaded', function() {
    
    // ==================== REGISTRATION FORM VALIDATION ====================
    const registrationForm = document.querySelector('.auth-form');
    if (registrationForm) {
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');
        
        if (confirmPassword) {
            confirmPassword.addEventListener('blur', function() {
                validatePasswordMatch(password, confirmPassword);
            });
        }
        
        if (password) {
            password.addEventListener('blur', function() {
                validatePasswordStrength(password);
            });
        }
    }
    
    // ==================== LOGIN FORM VALIDATION ====================
    const loginForm = document.querySelector('.auth-form');
    if (loginForm && document.getElementById('username') && document.getElementById('password')) {
        // Login form validation will happen on submit via JSF
    }
    
    // ==================== CHECKOUT FORM VALIDATION ====================
    const checkoutForm = document.querySelector('.checkout-container form');
    if (checkoutForm) {
        const paymentMethod = document.querySelector('input[name="paymentMethod"]');
        const cardNumber = document.getElementById('cardNumber');
        const cardExpiry = document.getElementById('cardExpiry');
        const cardCvv = document.getElementById('cardCvv');
        
        if (paymentMethod) {
            const radioButtons = document.querySelectorAll('input[name="paymentMethod"]');
            radioButtons.forEach(function(radio) {
                radio.addEventListener('change', function() {
                    toggleCardDetails(this.value);
                });
            });
        }
    }
    
    // ==================== CONTACT FORM VALIDATION ====================
    const contactForm = document.querySelector('.contact-form');
    if (contactForm) {
        const email = document.getElementById('email');
        if (email) {
            email.addEventListener('blur', function() {
                validateEmail(email);
            });
        }
        
        const phone = document.getElementById('phone');
        if (phone) {
            phone.addEventListener('blur', function() {
                validatePhone(phone);
            });
        }
    }
});

// ==================== VALIDATION FUNCTIONS ====================

/**
 * Validate password match for registration
 */
function validatePasswordMatch(password, confirmPassword) {
    if (password.value !== confirmPassword.value) {
        showError(confirmPassword, 'Passwords do not match');
        return false;
    } else {
        clearError(confirmPassword);
        return true;
    }
}

/**
 * Validate password strength
 */
function validatePasswordStrength(password) {
    const value = password.value;
    
    if (value.length < 6) {
        showError(password, 'Password must be at least 6 characters long');
        return false;
    }
    
    clearError(password);
    return true;
}

/**
 * Validate email format
 */
function validateEmail(email) {
    const emailRegex = /^[A-Za-z0-9+_.-]+@(.+)$/;
    const value = email.value;
    
    if (!emailRegex.test(value)) {
        showError(email, 'Please enter a valid email address (e.g., name@example.com)');
        return false;
    }
    
    clearError(email);
    return true;
}

/**
 * Validate phone number (10-11 digits)
 */
function validatePhone(phone) {
    const phoneRegex = /^[0-9]{10,11}$/;
    const value = phone.value;
    
    if (value && !phoneRegex.test(value)) {
        showError(phone, 'Please enter a valid phone number (10-11 digits)');
        return false;
    }
    
    clearError(phone);
    return true;
}

/**
 * Validate delivery address
 */
function validateAddress(address) {
    const value = address.value;
    
    if (value.length < 5) {
        showError(address, 'Please enter a complete delivery address');
        return false;
    }
    
    clearError(address);
    return true;
}

/**
 * Validate card number (16 digits)
 */
function validateCardNumber(cardNumber) {
    const cardRegex = /^[0-9]{16}$/;
    const value = cardNumber.value.replace(/\s/g, '');
    
    if (!cardRegex.test(value)) {
        showError(cardNumber, 'Please enter a valid 16-digit card number');
        return false;
    }
    
    clearError(cardNumber);
    return true;
}

/**
 * Validate card expiry date (MM/YY)
 */
function validateCardExpiry(expiry) {
    const expiryRegex = /^(0[1-9]|1[0-2])\/([0-9]{2})$/;
    const value = expiry.value;
    
    if (!expiryRegex.test(value)) {
        showError(expiry, 'Please enter expiry date in MM/YY format');
        return false;
    }
    
    // Check if card is not expired
    const today = new Date();
    const currentYear = today.getFullYear() % 100;
    const currentMonth = today.getMonth() + 1;
    const expMonth = parseInt(value.split('/')[0]);
    const expYear = parseInt(value.split('/')[1]);
    
    if (expYear < currentYear || (expYear === currentYear && expMonth < currentMonth)) {
        showError(expiry, 'Your card has expired');
        return false;
    }
    
    clearError(expiry);
    return true;
}

/**
 * Validate CVV (3 digits)
 */
function validateCvv(cvv) {
    const cvvRegex = /^[0-9]{3}$/;
    const value = cvv.value;
    
    if (!cvvRegex.test(value)) {
        showError(cvv, 'Please enter a valid 3-digit CVV');
        return false;
    }
    
    clearError(cvv);
    return true;
}

/**
 * Show error message for a field
 */
function showError(field, message) {
    // Remove existing error
    clearError(field);
    
    // Add error class to field
    field.classList.add('error-field');
    
    // Create error message element
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.style.color = '#e74c3c';
    errorDiv.style.fontSize = '0.8rem';
    errorDiv.style.marginTop = '5px';
    errorDiv.innerHTML = message;
    
    // Insert error message after the field
    field.parentNode.insertBefore(errorDiv, field.nextSibling);
}

/**
 * Clear error message for a field
 */
function clearError(field) {
    field.classList.remove('error-field');
    
    // Remove error message if exists
    const nextSibling = field.nextSibling;
    if (nextSibling && nextSibling.className === 'error-message') {
        nextSibling.remove();
    }
}

/**
 * Toggle card details section based on payment method
 */
function toggleCardDetails(paymentMethod) {
    const cardDetails = document.querySelector('.card-details');
    if (cardDetails) {
        if (paymentMethod === 'credit_card') {
            cardDetails.style.display = 'block';
        } else {
            cardDetails.style.display = 'none';
        }
    }
}

/**
 * Validate entire registration form before submit
 */
function validateRegistrationForm() {
    const username = document.getElementById('username');
    const email = document.getElementById('email');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const fullName = document.getElementById('fullName');
    const phone = document.getElementById('phone');
    const address = document.getElementById('address');
    
    let isValid = true;
    
    if (username && username.value.length < 3) {
        showError(username, 'Username must be at least 3 characters');
        isValid = false;
    }
    
    if (email && !validateEmail(email)) {
        isValid = false;
    }
    
    if (password && password.value.length < 6) {
        showError(password, 'Password must be at least 6 characters');
        isValid = false;
    }
    
    if (confirmPassword && password && password.value !== confirmPassword.value) {
        showError(confirmPassword, 'Passwords do not match');
        isValid = false;
    }
    
    if (fullName && fullName.value.length < 2) {
        showError(fullName, 'Please enter your full name');
        isValid = false;
    }
    
    if (phone && phone.value && !validatePhone(phone)) {
        isValid = false;
    }
    
    if (address && address.value.length < 5) {
        showError(address, 'Please enter your delivery address');
        isValid = false;
    }
    
    return isValid;
}

/**
 * Validate checkout form before submit
 */
function validateCheckoutForm() {
    const deliveryAddress = document.getElementById('deliveryAddress');
    const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked');
    const cardNumber = document.getElementById('cardNumber');
    const cardExpiry = document.getElementById('cardExpiry');
    const cardCvv = document.getElementById('cardCvv');
    
    let isValid = true;
    
    if (deliveryAddress && deliveryAddress.value.length < 5) {
        showError(deliveryAddress, 'Please enter your delivery address');
        isValid = false;
    }
    
    if (!paymentMethod) {
        const paymentOptions = document.querySelector('.payment-options');
        showError(paymentOptions, 'Please select a payment method');
        isValid = false;
    }
    
    if (paymentMethod && paymentMethod.value === 'credit_card') {
        if (cardNumber && !validateCardNumber(cardNumber)) {
            isValid = false;
        }
        if (cardExpiry && !validateCardExpiry(cardExpiry)) {
            isValid = false;
        }
        if (cardCvv && !validateCvv(cardCvv)) {
            isValid = false;
        }
    }
    
    return isValid;
}

/**
 * Format card number with spaces (1234 5678 9012 3456)
 */
function formatCardNumber(input) {
    let value = input.value.replace(/\s/g, '');
    if (value.length > 16) {
        value = value.slice(0, 16);
    }
    let formatted = '';
    for (let i = 0; i < value.length; i++) {
        if (i > 0 && i % 4 === 0) {
            formatted += ' ';
        }
        formatted += value[i];
    }
    input.value = formatted;
}

/**
 * Format expiry date (MM/YY)
 */
function formatExpiryDate(input) {
    let value = input.value.replace(/\//g, '');
    if (value.length > 4) {
        value = value.slice(0, 4);
    }
    if (value.length >= 3) {
        value = value.slice(0, 2) + '/' + value.slice(2);
    }
    input.value = value;
}

/**
 * Show success message
 */
function showSuccessMessage(message) {
    const successDiv = document.createElement('div');
    successDiv.className = 'success-message';
    successDiv.style.backgroundColor = '#d4edda';
    successDiv.style.color = '#155724';
    successDiv.style.padding = '10px';
    successDiv.style.borderRadius = '5px';
    successDiv.style.marginBottom = '10px';
    successDiv.innerHTML = message;
    
    const container = document.querySelector('.container');
    if (container) {
        container.insertBefore(successDiv, container.firstChild);
        
        // Auto hide after 5 seconds
        setTimeout(function() {
            successDiv.remove();
        }, 5000);
    }
}

/**
 * Show loading spinner on button
 */
function showLoading(button) {
    const originalText = button.innerHTML;
    button.innerHTML = 'Loading...';
    button.disabled = true;
    
    return function() {
        button.innerHTML = originalText;
        button.disabled = false;
    };
}

/**
 * Validate username (no special characters, min 3 chars)
 */
function validateUsername(username) {
    const usernameRegex = /^[a-zA-Z0-9_]+$/;
    const value = username.value;
    
    if (value.length < 3) {
        showError(username, 'Username must be at least 3 characters');
        return false;
    }
    
    if (!usernameRegex.test(value)) {
        showError(username, 'Username can only contain letters, numbers, and underscores');
        return false;
    }
    
    clearError(username);
    return true;
}

/**
 * Validate full name (letters and spaces only)
 */
function validateFullName(fullName) {
    const nameRegex = /^[a-zA-Z\s]+$/;
    const value = fullName.value;
    
    if (value.length < 2) {
        showError(fullName, 'Please enter your full name');
        return false;
    }
    
    if (!nameRegex.test(value)) {
        showError(fullName, 'Name can only contain letters and spaces');
        return false;
    }
    
    clearError(fullName);
    return true;
}