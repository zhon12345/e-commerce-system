INSERT INTO Customers (username, email, password) VALUES
('john_doe', 'john.doe@email.com', 'pass123'),
('jane_smith', 'jane.smith@email.com', 'pass456'),
('alice_jones', 'alice.j@email.com', 'pass789'),
('bob_brown', 'bob.b@email.com', 'pass101'),
('charlie_d', 'charlie.d@email.com', 'pass112'),
('diana_evans', 'diana.e@email.com', 'pass131'),
('ethan_fox', 'ethan.f@email.com', 'pass415'),
('fiona_green', 'fiona.g@email.com', 'pass161'),
('george_hill', 'george.h@email.com', 'pass718'),
('hannah_irey', 'hannah.i@email.com', 'pass192');

-- INSERT INTO PaymentInfo (customer_id, card_number, card_holder_name, expiration_date, cvv) VALUES
-- (1, '**** **** **** 1111', 'John Doe', '2027-12-31', '111'),
-- (2, '**** **** **** 2222', 'Jane Smith', '2026-11-30', '222'),
-- (3, '**** **** **** 3333', 'Alice Jones', '2028-01-31', '333'),
-- (4, '**** **** **** 4444', 'Bob Brown', '2027-05-31', '444'),
-- (5, '**** **** **** 5555', 'Charlie Davis', '2026-08-31', '555'),
-- (6, '**** **** **** 6666', 'Diana Evans', '2028-03-31', '666'),
-- (7, '**** **** **** 7777', 'Ethan Fox', '2027-09-30', '777'),
-- (8, '**** **** **** 8888', 'Fiona Green', '2026-04-30', '888'),
-- (9, '**** **** **** 9999', 'George Hill', '2028-06-30', '999'),
-- (10, '**** **** **** 0000', 'Hannah Irey', '2027-10-31', '100');

INSERT INTO CustomerAddresses (customer_id, receiver_name, phone_number, home_address) VALUES
(1, 'John Doe', '111-222-3333', '1 Main St, Anytown'),
(2, 'Jane Smith', '222-333-4444', '2 Oak Ave, Otherville'),
(3, 'Alice Jones', '333-444-5555', '3 Pine Ln, Sometown'),
(4, 'Bob Brown', '444-555-6666', '4 Maple Dr, Anycity'),
(1, 'John Doe Alt', '111-222-3334', '5 Birch Rd, Anytown'), -- Second address for customer 1
(5, 'Charlie Davis', '555-666-7777', '6 Cedar Ct, New Town'),
(6, 'Diana Evans', '666-777-8888', '7 Willow Way, Old City'),
(7, 'Ethan Fox', '777-888-9999', '8 Elm Pl, Bigville'),
(8, 'Fiona Green', '888-999-0000', '9 Aspen St, Smalltown'),
(9, 'George Hill', '999-000-1111', '10 Spruce Ave, Villagetown');

INSERT INTO Products (name, description, price, stock, category) VALUES
('Laptop Pro', 'High-performance laptop', 1499.99, 30, 'Electronics'),
('Wireless Mouse', 'Ergonomic wireless mouse', 29.99, 150, 'Accessories'),
('Mechanical Keyboard', 'RGB Backlit Keyboard', 89.50, 75, 'Accessories'),
('USB-C Hub', '7-in-1 USB-C Adapter', 39.95, 200, 'Accessories'),
('Webcam HD', '1080p HD Webcam', 45.00, 90, 'Electronics'),
('Monitor 27"', '27-inch QHD Monitor', 299.99, 40, 'Electronics'),
('Gaming Chair', 'Ergonomic Gaming Chair', 199.99, 25, 'Furniture'),
('Smartphone X', 'Latest Gen Smartphone', 999.00, 60, 'Electronics'),
('Tablet Lite', '10-inch Android Tablet', 179.50, 110, 'Electronics'),
('Smart Watch', 'Fitness Tracker Watch', 129.99, 85, 'Wearables');

-- INSERT INTO Orders (customer_id, total_price, status) VALUES
-- (1, 1529.98, 2), -- Laptop + Mouse, Shipped
-- (2, 89.50, 1),   -- Keyboard, Processing
-- (3, 344.99, 3),   -- Monitor + Webcam, Delivered
-- (4, 39.95, 2),    -- USB-C Hub, Shipped
-- (5, 1178.50, 1),  -- Smartphone + Tablet, Processing
-- (1, 45.00, 3),    -- Webcam, Delivered
-- (6, 129.99, 1),  -- Smart Watch, Processing
-- (7, 199.99, 2),  -- Gaming Chair, Shipped
-- (8, 1178.50, 1),  -- Smartphone + Tablet (duplicate for another user), Processing
-- (10, 1499.99, 1); -- Laptop Pro, Processing

-- INSERT INTO OrderDetails (order_id, product_id, quantity, price) VALUES
-- (1, 1, 1, 1499.99), -- Order 1, Laptop Pro
-- (1, 2, 1, 29.99),   -- Order 1, Wireless Mouse
-- (2, 3, 1, 89.50),   -- Order 2, Keyboard
-- (3, 6, 1, 299.99),  -- Order 3, Monitor
-- (3, 5, 1, 45.00),   -- Order 3, Webcam
-- (4, 4, 1, 39.95),   -- Order 4, USB-C Hub
-- (5, 8, 1, 999.00),  -- Order 5, Smartphone
-- (5, 9, 1, 179.50),  -- Order 5, Tablet
-- (6, 5, 1, 45.00),   -- Order 6, Webcam
-- (7, 10, 1, 129.99), -- Order 7, Smart Watch
-- (8, 7, 1, 199.99),  -- Order 8, Gaming Chair
-- (9, 8, 1, 999.00),  -- Order 9, Smartphone
-- (9, 9, 1, 179.50),  -- Order 9, Tablet
-- (10, 1, 1, 1499.99);-- Order 10, Laptop Pro

INSERT INTO Reviews (customer_id, product_id, rating, review) VALUES
(1, 1, 5, 'Amazing laptop, worth the price!'),
(2, 3, 4, 'Good keyboard, keys feel nice.'),
(3, 6, 5, 'Crystal clear monitor.'),
(3, 5, 4, 'Webcam works well for meetings.'),
(4, 4, 3, 'Hub gets a bit warm, but works.'),
(5, 8, 5, 'Best smartphone I have owned.'),
(5, 9, 4, 'Tablet is great for reading.'),
(1, 5, 4, 'Good picture quality on the webcam.'),
(7, 7, 5, 'Very comfortable gaming chair!'),
(10, 1, 5, 'Powerful machine for work.');

-- INSERT INTO Cart (customer_id, product_id, quantity) VALUES
-- (1, 4, 1),  -- John, USB-C Hub
-- (2, 2, 1),  -- Jane, Wireless Mouse
-- (2, 6, 1),  -- Jane, Monitor
-- (3, 10, 1), -- Alice, Smart Watch
-- (4, 7, 1),  -- Bob, Gaming Chair
-- (5, 3, 1),  -- Charlie, Keyboard
-- (6, 1, 1),  -- Diana, Laptop Pro
-- (7, 8, 1),  -- Ethan, Smartphone X
-- (8, 9, 2),  -- Fiona, Tablet Lite (Qty 2)
-- (9, 5, 1);  -- George, Webcam HD

INSERT INTO Promotions (promo_code, discount, valid_from, valid_to) VALUES
('SUMMER25', 25.00, '2025-06-01', '2025-08-31'),
('NEW10', 10.00, '2025-01-01', '2025-12-31'),
('SPRING15', 15.00, '2025-03-01', '2025-05-31'),
('WKNDDEAL', 20.00, '2025-04-04', '2025-04-06'), -- Assuming today is within this range
('TECH5', 5.00, '2025-04-01', '2025-04-30'),
('FLASH10', 10.00, '2025-04-03', '2025-04-03'), -- Today only
('LOYALTY5', 5.00, '2025-01-01', '2025-06-30'),
('BIGSALE30', 30.00, '2025-11-20', '2025-11-30'),
('ACCESSORY10', 10.00, '2025-04-01', '2025-04-15'),
('FREESHIP', 0.00, '2025-01-01', '2025-12-31'); -- Represents free shipping

INSERT INTO Staff (username, email, password, is_manager) VALUES
('admin_mgr', 'admin@shop.com', 'adminpass', TRUE),
('sarah_c', 'sarah.c@shop.com', 'staffpass1', FALSE),
('mike_l', 'mike.l@shop.com', 'staffpass2',FALSE),
('lisa_r', 'lisa.r@shop.com', 'staffpass3', FALSE),
('david_s', 'david.s@shop.com', 'staffpass4', TRUE), -- Shift Manager
('emily_t', 'emily.t@shop.com', 'staffpass5', FALSE),
('kevin_w', 'kevin.w@shop.com', 'staffpass6', FALSE),
('olivia_p', 'olivia.p@shop.com', 'staffpass7', FALSE),
('jason_m', 'jason.m@shop.com', 'staffpass8', FALSE),
('natalie_b', 'natalie.b@shop.com', 'staffpass9', FALSE);

-- INSERT INTO Reports (report_type, details) VALUES
-- ('Monthly Sales - Feb 2025', 'Total sales figures and analysis for February 2025.'),
-- ('Inventory Stock - Mar 2025', 'End-of-month stock levels for March 2025.'),
-- ('Customer Activity - Q1 2025', 'Summary of new customers and order frequency.'),
-- ('Promotion Usage - SPRING15', 'Analysis of SPRING15 promo code usage.'),
-- ('Website Traffic - Mar 2025', 'Monthly website visitor statistics.'),
-- ('Return Rates - Feb 2025', 'Analysis of product returns in February.'),
-- ('Staff Performance - Q1 2025', 'Quarterly review summary for staff.'),
-- ('Top Selling Products - Mar 2025', 'List of best-selling items in March.'),
-- ('Low Stock Alert', 'Products with stock level below 30 units as of 2025-04-03.'),
-- ('Daily Sales Summary - 2025-04-02', 'Sales report for April 2nd, 2025.');
