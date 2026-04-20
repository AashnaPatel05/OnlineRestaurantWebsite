-- ============================================
-- DATABASE: restaurant_db
-- CREATED: Online Restaurant Website
-- ============================================

-- DROP TABLES IF EXISTS (Run this first if tables exist)
DROP TABLE payment;
DROP TABLE order_items;
DROP TABLE orders;
DROP TABLE cart;
DROP TABLE food;
DROP TABLE users;

-- ============================================
-- TABLE 1: USERS (Customer Accounts)
-- ============================================
CREATE TABLE users (
    user_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(500),
    role VARCHAR(20) DEFAULT 'customer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
);

-- ============================================
-- TABLE 2: FOOD (Menu Items)
-- ============================================
CREATE TABLE food (
    food_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(255),
    is_available SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (food_id)
);

-- ============================================
-- TABLE 3: CART (Shopping Cart)
-- ============================================
CREATE TABLE cart (
    cart_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    user_id INTEGER NOT NULL,
    food_id INTEGER NOT NULL,
    quantity INTEGER DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (cart_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES food(food_id) ON DELETE CASCADE
);

-- ============================================
-- TABLE 4: ORDERS (Order Headers)
-- ============================================
CREATE TABLE orders (
    order_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    user_id INTEGER NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'pending',
    delivery_address VARCHAR(500) NOT NULL,
    payment_method VARCHAR(50),
    PRIMARY KEY (order_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================
-- TABLE 5: ORDER_ITEMS (Order Details)
-- ============================================
CREATE TABLE order_items (
    order_item_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    order_id INTEGER NOT NULL,
    food_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    price_at_time DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (order_item_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES food(food_id) ON DELETE CASCADE
);

-- ============================================
-- TABLE 6: PAYMENT (Payment Records)
-- ============================================
CREATE TABLE payment (
    payment_id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    order_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) DEFAULT 'pending',
    transaction_id VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (payment_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================
-- SAMPLE DATA: Insert Menu Items
-- ============================================

-- Starters
INSERT INTO food (name, category, description, price, image_url) VALUES
('Garlic Bread', 'Starters', 'Toasted bread with garlic butter and herbs', 4.99, 'garlic-bread.jpg'),
('Chicken Wings', 'Starters', 'Spicy chicken wings served with ranch dip', 6.99, 'chicken-wings.jpg'),
('Spring Rolls', 'Starters', 'Crispy vegetable spring rolls', 5.49, 'spring-rolls.jpg'),
('Bruschetta', 'Starters', 'Toasted bread with tomatoes and basil', 5.99, 'bruschetta.jpg');

-- Main Courses
INSERT INTO food (name, category, description, price, image_url) VALUES
('Margherita Pizza', 'Main Courses', 'Fresh tomato, mozzarella, and basil', 12.99, 'margherita.jpg'),
('Chicken Burger', 'Main Courses', 'Grilled chicken breast with lettuce and mayo', 11.99, 'chicken-burger.jpg'),
('Grilled Salmon', 'Main Courses', 'Fresh salmon with lemon butter sauce', 18.99, 'salmon.jpg'),
('Pasta Carbonara', 'Main Courses', 'Creamy pasta with bacon and parmesan', 13.99, 'carbonara.jpg'),
('Beef Steak', 'Main Courses', 'Grilled sirloin steak with vegetables', 22.99, 'steak.jpg'),
('Vegetable Curry', 'Main Courses', 'Mixed vegetables in coconut curry sauce', 11.99, 'curry.jpg');

-- Desserts
INSERT INTO food (name, category, description, price, image_url) VALUES
('Chocolate Lava Cake', 'Desserts', 'Warm chocolate cake with molten center', 5.99, 'lava-cake.jpg'),
('Vanilla Ice Cream', 'Desserts', 'Creamy vanilla ice cream with chocolate sauce', 3.99, 'ice-cream.jpg'),
('Cheesecake', 'Desserts', 'New York style cheesecake', 4.99, 'cheesecake.jpg'),
('Tiramisu', 'Desserts', 'Italian coffee-flavored dessert', 5.49, 'tiramisu.jpg');

-- Drinks
INSERT INTO food (name, category, description, price, image_url) VALUES
('Fresh Lemonade', 'Drinks', 'Homemade lemonade with mint', 3.50, 'lemonade.jpg'),
('Cappuccino', 'Drinks', 'Italian coffee with steamed milk foam', 3.99, 'cappuccino.jpg'),
('Soft Drink', 'Drinks', 'Coke, Sprite, or Fanta', 2.50, 'soft-drink.jpg'),
('Fresh Orange Juice', 'Drinks', 'Freshly squeezed orange juice', 4.50, 'orange-juice.jpg'),
('Mojito', 'Drinks', 'Non-alcoholic mint and lime mojito', 4.99, 'mojito.jpg');

-- Insert sample user (password: password123 - will hash later)
INSERT INTO users (username, email, password, full_name, phone, address) VALUES
('john_doe', 'john@example.com', 'password123', 'John Doe', '07712345678', '123 Main Street, London, UK');

-- Verify data
SELECT * FROM food;
SELECT * FROM users;