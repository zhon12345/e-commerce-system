-- Dummy data for Users table
INSERT INTO Users (username, email, password, role) VALUES
('customerREX', 'customer1@example.com', 'password123', 'customer'),
('customer2', 'customer2@example.com', 'securepass', 'customer'),
('staffREX', 'staff1@example.com', 'staffpass', 'staff'),
('staff2', 'staff2@example.com', 'anotherstaff', 'staff'),
('GiantREX', 'GREX@JEEYANG.com', 'Banish to summon itself', 'manager');

-- Dummy data for CustomerAddresses table
INSERT INTO CustomerAddresses (user_id, receiver_name, phone_number, home_address) VALUES
(1, 'REX LJY', '012-3456789', '123 Jurrastic world, Taman Paleozoic'),
(2, 'Jane Smith', '017-9876543', '456 Lorong DEF, Bandar GHI');

-- Dummy data for Products table
INSERT INTO Products (name, description, price, stock, category_id) VALUES
('IEM', 'AFUL Performer 5', 1200.50, 50, 1),
('Mouse', 'Wireless ergonomic mouse', 25.99, 100, 2),
('Keyboard', 'Mechanical gaming keyboard', 79.99, 75, 2),
('Monitor', '27-inch 4K monitor', 350.00, 30, 1),
('Webcam', 'HD webcam with microphone', 49.95, 60, 2);

-- Dummy data for Promotions table
INSERT INTO Promotions (promo_code, discount, valid_from, valid_to) VALUES
('SUMMER20', 0.20, '2025-06-01', '2025-08-31'),
('FREESHIP', 0.00, '2025-05-01', '2025-05-31'),
('SPRING15', 0.15, '2025-03-01', '2025-05-31'),
('NEWUSER', 0.10, '2025-04-19', '2025-05-19'),
('BUNDLE5', 0.05, '2025-04-25', '2025-07-31');
