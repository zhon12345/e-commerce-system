CREATE TABLE Users (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('customer', 'staff', 'manager')),
    is_archived BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Categories (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE PaymentInfo (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT,
    card_number VARCHAR(255) ,
    card_holder_name VARCHAR(255) ,
    expiration_date DATE ,
    cvv VARCHAR(255) ,
    payment_method VARCHAR(50) NOT NULL CHECK (payment_method IN ('cash', 'card','e-wallet')),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE CustomerAddresses (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT,
    receiver_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    home_address VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE Products (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    stock INT NOT NULL CHECK (stock >= 0),
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES Categories(id)
);

CREATE TABLE Orders (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_price DECIMAL(10,2) NOT NULL CHECK (total_price >= 0),
    payment_info_id INT NOT NULL,
    delivery_cost DECIMAL(10,2) DEFAULT 0.00,
    address_id INT,
    FOREIGN KEY (user_id) REFERENCES Users(id) ,
    FOREIGN KEY (address_id) REFERENCES CustomerAddresses(id) ,
    FOREIGN KEY (payment_info_id) REFERENCES PaymentInfo(id)
);

CREATE TABLE OrderDetails (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    order_id INT,
    product_id INT,
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(id) ,
    FOREIGN KEY (product_id) REFERENCES Products(id)
);

CREATE TABLE Reviews (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT,
    product_id INT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    review VARCHAR(255),
    is_archived BOOLEAN DEFAULT FALSE,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ,
    FOREIGN KEY (product_id) REFERENCES Products(id)
);

CREATE TABLE Reply (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    review_id INT,
    user_id INT,
    reply_text VARCHAR(255) NOT NULL,
    is_archived BOOLEAN DEFAULT FALSE,
    reply_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (review_id) REFERENCES Reviews(id) ,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

CREATE TABLE Cart (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT,
    product_id INT,
    quantity INT NOT NULL CHECK (quantity > 0),
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ,
    FOREIGN KEY (product_id) REFERENCES Products(id) ,
    UNIQUE (user_id, product_id)
);

CREATE TABLE Promotions (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    promo_code VARCHAR(50) UNIQUE NOT NULL,
    discount DECIMAL(5,2) NOT NULL CHECK (discount > 0),
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE Reports (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    report_type VARCHAR(255) NOT NULL,
    generated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details VARCHAR(4000),
    generated_by_id INT,
    FOREIGN KEY (generated_by_id) REFERENCES Users(id)
);
