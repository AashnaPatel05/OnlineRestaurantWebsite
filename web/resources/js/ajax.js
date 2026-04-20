/* ============================================
   SPICE GARDEN RESTAURANT - AJAX FUNCTIONS
   Handles cart updates without page refresh
   ============================================ */

// Wait for the page to load completely
document.addEventListener('DOMContentLoaded', function() {
    
    // ==================== CART AJAX FUNCTIONS ====================
    
    // Add to Cart buttons
    const addToCartButtons = document.querySelectorAll('.add-to-cart-btn, .btn-order');
    addToCartButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            const foodId = this.getAttribute('data-food-id');
            if (foodId) {
                addToCartAjax(foodId);
            }
        });
    });
    
    // Quantity update inputs on cart page
    const quantityInputs = document.querySelectorAll('.quantity-input');
    quantityInputs.forEach(function(input) {
        input.addEventListener('change', function() {
            const cartId = this.getAttribute('data-cart-id');
            const quantity = this.value;
            if (cartId) {
                updateCartQuantityAjax(cartId, quantity);
            }
        });
    });
    
    // Remove from cart links
    const removeLinks = document.querySelectorAll('.remove-link');
    removeLinks.forEach(function(link) {
        link.addEventListener('click', function(event) {
            event.preventDefault();
            const cartId = this.getAttribute('data-cart-id');
            if (cartId && confirm('Are you sure you want to remove this item?')) {
                removeFromCartAjax(cartId);
            }
        });
    });
    
    // Clear cart button
    const clearCartBtn = document.querySelector('.clear-cart .clear-link');
    if (clearCartBtn) {
        clearCartBtn.addEventListener('click', function(event) {
            event.preventDefault();
            if (confirm('Are you sure you want to clear your entire cart?')) {
                clearCartAjax();
            }
        });
    }
    
    // ==================== MENU CATEGORY FILTER AJAX ====================
    
    const categoryButtons = document.querySelectorAll('.category-btn');
    categoryButtons.forEach(function(button) {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            const category = this.getAttribute('data-category');
            if (category) {
                filterMenuByCategory(category);
            }
        });
    });
    
    // ==================== SEARCH AJAX ====================
    
    const searchInput = document.querySelector('.search-input');
    if (searchInput) {
        let searchTimeout;
        searchInput.addEventListener('keyup', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(function() {
                const keyword = searchInput.value;
                searchMenuAjax(keyword);
            }, 500);
        });
    }
});

// ==================== CART AJAX FUNCTIONS ====================

/**
 * Add item to cart using AJAX
 */
function addToCartAjax(foodId) {
    showLoadingOverlay();
    
    const xhr = new XMLHttpRequest();
    xhr.open('POST', window.location.origin + window.location.pathname + '?addToCart=' + foodId, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            hideLoadingOverlay();
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                if (response.success) {
                    updateCartCount(response.cartCount);
                    showToast('Item added to cart successfully!', 'success');
                } else {
                    showToast('Please login to add items to cart', 'error');
                    setTimeout(function() {
                        window.location.href = 'login.xhtml';
                    }, 2000);
                }
            } else {
                showToast('Error adding item to cart', 'error');
            }
        }
    };
    
    xhr.send('foodId=' + encodeURIComponent(foodId));
}

/**
 * Update cart item quantity using AJAX
 */
function updateCartQuantityAjax(cartId, quantity) {
    if (quantity < 1) {
        removeFromCartAjax(cartId);
        return;
    }
    
    showLoadingOverlay();
    
    const xhr = new XMLHttpRequest();
    xhr.open('POST', window.location.origin + window.location.pathname + '?updateQuantity=' + cartId, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            hideLoadingOverlay();
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                if (response.success) {
                    updateCartCount(response.cartCount);
                    updateCartTotal(response.cartTotal);
                    updateItemSubtotal(cartId, response.itemSubtotal);
                    showToast('Cart updated', 'success');
                } else {
                    showToast('Error updating cart', 'error');
                }
            }
        }
    };
    
    xhr.send('cartId=' + encodeURIComponent(cartId) + '&quantity=' + encodeURIComponent(quantity));
}

/**
 * Remove item from cart using AJAX
 */
function removeFromCartAjax(cartId) {
    showLoadingOverlay();
    
    const xhr = new XMLHttpRequest();
    xhr.open('POST', window.location.origin + window.location.pathname + '?removeFromCart=' + cartId, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            hideLoadingOverlay();
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                if (response.success) {
                    updateCartCount(response.cartCount);
                    removeCartItemRow(cartId);
                    updateCartTotal(response.cartTotal);
                    showToast('Item removed from cart', 'success');
                    
                    if (response.cartCount === 0) {
                        showEmptyCart();
                    }
                } else {
                    showToast('Error removing item', 'error');
                }
            }
        }
    };
    
    xhr.send('cartId=' + encodeURIComponent(cartId));
}

/**
 * Clear entire cart using AJAX
 */
function clearCartAjax() {
    showLoadingOverlay();
    
    const xhr = new XMLHttpRequest();
    xhr.open('POST', window.location.origin + window.location.pathname + '?clearCart=true', true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            hideLoadingOverlay();
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                if (response.success) {
                    updateCartCount(0);
                    showEmptyCart();
                    showToast('Cart cleared successfully', 'success');
                } else {
                    showToast('Error clearing cart', 'error');
                }
            }
        }
    };
    
    xhr.send();
}

// ==================== MENU AJAX FUNCTIONS ====================

/**
 * Filter menu by category using AJAX
 */
function filterMenuByCategory(category) {
    showLoadingOverlay();
    
    const xhr = new XMLHttpRequest();
    xhr.open('GET', window.location.origin + window.location.pathname + '?category=' + encodeURIComponent(category), true);
    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            hideLoadingOverlay();
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                if (response.success) {
                    updateMenuItems(response.html);
                    updateActiveCategoryButton(category);
                }
            }
        }
    };
    
    xhr.send();
}

/**
 * Search menu items using AJAX
 */
function searchMenuAjax(keyword) {
    if (keyword.length < 2) {
        return;
    }
    
    showLoadingOverlay();
    
    const xhr = new XMLHttpRequest();
    xhr.open('GET', window.location.origin + window.location.pathname + '?search=' + encodeURIComponent(keyword), true);
    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            hideLoadingOverlay();
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                if (response.success) {
                    updateMenuItems(response.html);
                }
            }
        }
    };
    
    xhr.send();
}

// ==================== UI UPDATE FUNCTIONS ====================

/**
 * Update cart count badge in header
 */
function updateCartCount(count) {
    const cartCountElement = document.getElementById('cartCount');
    if (cartCountElement) {
        cartCountElement.textContent = count;
        
        if (count > 0) {
            cartCountElement.style.display = 'inline-block';
        } else {
            cartCountElement.style.display = 'none';
        }
    }
}

/**
 * Update cart total amount
 */
function updateCartTotal(total) {
    const cartTotalElement = document.querySelector('.cart-total-value, .summary-total');
    if (cartTotalElement) {
        cartTotalElement.textContent = '£' + parseFloat(total).toFixed(2);
    }
}

/**
 * Update item subtotal for a specific cart item
 */
function updateItemSubtotal(cartId, subtotal) {
    const row = document.querySelector('tr[data-cart-id="' + cartId + '"]');
    if (row) {
        const subtotalCell = row.querySelector('.cart-subtotal');
        if (subtotalCell) {
            subtotalCell.textContent = '£' + parseFloat(subtotal).toFixed(2);
        }
    }
}

/**
 * Remove cart item row from table
 */
function removeCartItemRow(cartId) {
    const row = document.querySelector('tr[data-cart-id="' + cartId + '"]');
    if (row) {
        row.remove();
    }
}

/**
 * Update menu items display after filter/search
 */
function updateMenuItems(html) {
    const menuContainer = document.querySelector('.menu-container');
    if (menuContainer) {
        menuContainer.innerHTML = html;
        reattachCartButtons();
    }
}

/**
 * Update active category button styling
 */
function updateActiveCategoryButton(activeCategory) {
    const buttons = document.querySelectorAll('.category-btn');
    buttons.forEach(function(button) {
        button.classList.remove('active');
        if (button.getAttribute('data-category') === activeCategory) {
            button.classList.add('active');
        }
    });
}

/**
 * Show empty cart message
 */
function showEmptyCart() {
    const cartContainer = document.querySelector('.cart-container');
    if (cartContainer) {
        cartContainer.innerHTML = `
            <div class="empty-cart">
                <p>Your cart is empty</p>
                <p>Add some delicious items from our menu</p>
                <a href="menu.xhtml" class="btn btn-primary">Browse Menu</a>
            </div>
        `;
    }
}

/**
 * Reattach cart button event listeners after AJAX update
 */
function reattachCartButtons() {
    const addToCartButtons = document.querySelectorAll('.add-to-cart-btn, .btn-order');
    addToCartButtons.forEach(function(button) {
        button.removeEventListener('click', addToCartHandler);
        button.addEventListener('click', addToCartHandler);
    });
}

/**
 * Handler for add to cart buttons
 */
function addToCartHandler(event) {
    event.preventDefault();
    const foodId = this.getAttribute('data-food-id');
    if (foodId) {
        addToCartAjax(foodId);
    }
}

// ==================== HELPER FUNCTIONS ====================

/**
 * Show loading overlay
 */
function showLoadingOverlay() {
    let overlay = document.getElementById('loadingOverlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'loadingOverlay';
        overlay.style.position = 'fixed';
        overlay.style.top = '0';
        overlay.style.left = '0';
        overlay.style.width = '100%';
        overlay.style.height = '100%';
        overlay.style.backgroundColor = 'rgba(0,0,0,0.5)';
        overlay.style.zIndex = '9999';
        overlay.style.display = 'flex';
        overlay.style.alignItems = 'center';
        overlay.style.justifyContent = 'center';
        overlay.innerHTML = '<div style="background:white; padding:20px; border-radius:10px;">Loading...</div>';
        document.body.appendChild(overlay);
    }
    overlay.style.display = 'flex';
}

/**
 * Hide loading overlay
 */
function hideLoadingOverlay() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) {
        overlay.style.display = 'none';
    }
}

/**
 * Show toast notification
 */
function showToast(message, type) {
    let toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toastContainer';
        toastContainer.style.position = 'fixed';
        toastContainer.style.bottom = '20px';
        toastContainer.style.right = '20px';
        toastContainer.style.zIndex = '10000';
        document.body.appendChild(toastContainer);
    }
    
    const toast = document.createElement('div');
    toast.style.backgroundColor = type === 'success' ? '#28a745' : '#e74c3c';
    toast.style.color = 'white';
    toast.style.padding = '12px 20px';
    toast.style.borderRadius = '5px';
    toast.style.marginTop = '10px';
    toast.style.boxShadow = '0 2px 10px rgba(0,0,0,0.2)';
    toast.style.animation = 'fadeIn 0.3s ease';
    toast.textContent = message;
    
    toastContainer.appendChild(toast);
    
    setTimeout(function() {
        toast.style.animation = 'fadeOut 0.3s ease';
        setTimeout(function() {
            toast.remove();
        }, 300);
    }, 3000);
}

/**
 * Add CSS animations for toast
 */
const style = document.createElement('style');
style.textContent = `
    @keyframes fadeIn {
        from { opacity: 0; transform: translateX(100px); }
        to { opacity: 1; transform: translateX(0); }
    }
    @keyframes fadeOut {
        from { opacity: 1; transform: translateX(0); }
        to { opacity: 0; transform: translateX(100px); }
    }
`;
document.head.appendChild(style);