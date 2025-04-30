-- Dummy data for Users table
INSERT INTO Users (username, email, password, role) VALUES
('admin', 'admin@giantrex.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'manager'),
('staff', 'staff@giantrex.com', '1562206543da764123c21bd524674f0a8aaf49c8a89744c97352fe677f7e4006', 'staff');

INSERT INTO Categories(name, description) VALUES
('IEM', 'In-Ear Monitors'),
('Mouse', 'Wired & Wireless Gaming Mice'),
('Keyboard', 'Prebuilt Mechanical Keyboards');

-- Dummy data for Products table
INSERT INTO Products (image_path, name, description, price, stock, category_id) VALUES
('/assets/products/AFUL Explorer/1.png', 'AFUL Explorer', '', 550, 50, 1),
('/assets/products/Logitech G Pro/1.png', 'Logitech G Pro', '', 329, 100, 2),
('/assets/products/Kefine Klean/1.png', 'Kefine Klean', '', 160, 50, 1),
('/assets/products/SeeAudio Yume II/1.png', 'SeeAudio Yume II', '', 750, 20, 1),
('/assets/products/ATK X1 Ultra/1.png', 'ATK X1 Ultra', '', 270, 50, 2),
('/assets/products/EndgameGear OP1 8K/1.png', 'EndgameGear OP1 8K', '', 320, 50, 2),
('/assets/products/Varmilo VA87M/1.jpg', 'Varmilo VA87M', '', 200, 50, 3),
('/assets/products/Keychron K2/1.jpg', 'Keychron K2', '', 329, 50, 3),
('/assets/products/Akko 3068/1.jpg', 'Akko 3068', '', 329, 50, 3);

INSERT INTO Promotions (promo_code, discount, valid_from, valid_to) VALUES
('WINTER15', 0.15, '01.12.2025', '01.03.2025'),
('SPRING10', 0.10, '01.03.2025', '01.06.2025'),
('SUMMER25', 0.25, '01.06.2025', '01.09.2025'),
('FALL20', 0.20, '01.09.2025', '01.12.2025');