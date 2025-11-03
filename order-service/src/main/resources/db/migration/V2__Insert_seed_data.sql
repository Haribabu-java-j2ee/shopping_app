-- Seed data for testing orders

-- Sample orders
INSERT INTO orders (order_number, customer_id, status, subtotal, tax_amount, shipping_cost, total_amount, 
                    payment_method, street, city, state, zip_code, country, confirmed_at)
VALUES
('ORD-1234567890-ABCD1234', 2, 'DELIVERED', 100.00, 10.00, 5.00, 115.00, 'CREDIT_CARD',
 '123 Main St', 'Mumbai', 'Maharashtra', '400001', 'India', CURRENT_TIMESTAMP),
('ORD-1234567891-EFGH5678', 2, 'CONFIRMED', 50.00, 5.00, 0.00, 55.00, 'UPI',
 '456 Park Ave', 'Delhi', 'Delhi', '110001', 'India', CURRENT_TIMESTAMP),
('ORD-1234567892-IJKL9012', 3, 'PROCESSING', 200.00, 20.00, 5.00, 225.00, 'DEBIT_CARD',
 '789 Lake View', 'Bangalore', 'Karnataka', '560001', 'India', CURRENT_TIMESTAMP);

-- Sample order items
INSERT INTO order_items (order_id, product_id, product_name, sku, quantity, unit_price, total_price)
VALUES
(1, 'PROD-001', 'Wireless Mouse', 'WM-2024-BLK', 2, 25.00, 50.00),
(1, 'PROD-002', 'USB Cable', 'USB-C-3FT', 1, 15.00, 15.00),
(1, 'PROD-003', 'Laptop Stand', 'LS-ADJ-001', 1, 35.00, 35.00),
(2, 'PROD-004', 'Keyboard', 'KB-MECH-RGB', 1, 50.00, 50.00),
(3, 'PROD-005', 'Monitor 24"', 'MON-24-FHD', 1, 200.00, 200.00);





