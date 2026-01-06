-- Seed data for testing orders and customer statistics APIs
-- This data includes various customer spending patterns for testing:
-- - HIGH_SPEND customers (single transaction > 50,000)
-- - MID_SPEND customers (single transaction between 20,000 - 50,000)  
-- - LOW_SPEND customers (all transactions < 20,000)
-- - Various volume categories based on total spending

-- Sample orders with diverse spending patterns for Customer Stats API testing

-- Customer 1: HIGH_SPEND user (has a transaction > 50,000)
-- Also HIGH_VOLUME (total > 50,000)
INSERT INTO orders (order_number, customer_id, status, subtotal, tax_amount, shipping_cost, total_amount, 
                    payment_method, street, city, state, zip_code, country, confirmed_at)
VALUES
('ORD-1234567890-HIGH0001', 1, 'DELIVERED', 55000.00, 5500.00, 0.00, 60500.00, 'CREDIT_CARD',
 '100 Luxury Lane', 'Mumbai', 'Maharashtra', '400001', 'India', CURRENT_TIMESTAMP),
('ORD-1234567891-HIGH0002', 1, 'CONFIRMED', 15000.00, 1500.00, 0.00, 16500.00, 'UPI',
 '100 Luxury Lane', 'Mumbai', 'Maharashtra', '400001', 'India', CURRENT_TIMESTAMP),
('ORD-1234567892-HIGH0003', 1, 'DELIVERED', 8000.00, 800.00, 0.00, 8800.00, 'CREDIT_CARD',
 '100 Luxury Lane', 'Mumbai', 'Maharashtra', '400001', 'India', CURRENT_TIMESTAMP);

-- Customer 2: MID_SPEND user (highest transaction between 20,000 - 50,000)
-- Also MID_VOLUME (total between 20,000 - 50,000)
INSERT INTO orders (order_number, customer_id, status, subtotal, tax_amount, shipping_cost, total_amount, 
                    payment_method, street, city, state, zip_code, country, confirmed_at)
VALUES
('ORD-1234567893-MID00001', 2, 'DELIVERED', 25000.00, 2500.00, 0.00, 27500.00, 'DEBIT_CARD',
 '200 Middle Road', 'Delhi', 'Delhi', '110001', 'India', CURRENT_TIMESTAMP),
('ORD-1234567894-MID00002', 2, 'CONFIRMED', 5000.00, 500.00, 0.00, 5500.00, 'UPI',
 '200 Middle Road', 'Delhi', 'Delhi', '110001', 'India', CURRENT_TIMESTAMP);

-- Customer 3: LOW_SPEND user (all transactions < 20,000)
-- Also LOW_VOLUME (total < 20,000)
INSERT INTO orders (order_number, customer_id, status, subtotal, tax_amount, shipping_cost, total_amount, 
                    payment_method, street, city, state, zip_code, country, confirmed_at)
VALUES
('ORD-1234567895-LOW00001', 3, 'DELIVERED', 5000.00, 500.00, 100.00, 5600.00, 'UPI',
 '300 Budget Street', 'Bangalore', 'Karnataka', '560001', 'India', CURRENT_TIMESTAMP),
('ORD-1234567896-LOW00002', 3, 'PROCESSING', 3000.00, 300.00, 100.00, 3400.00, 'DEBIT_CARD',
 '300 Budget Street', 'Bangalore', 'Karnataka', '560001', 'India', CURRENT_TIMESTAMP),
('ORD-1234567897-LOW00003', 3, 'CONFIRMED', 2000.00, 200.00, 100.00, 2300.00, 'UPI',
 '300 Budget Street', 'Bangalore', 'Karnataka', '560001', 'India', CURRENT_TIMESTAMP);

-- Customer 4: LOW_SPEND (single transaction based) but MID_VOLUME (total based)
-- Many small transactions that add up to mid-range total
INSERT INTO orders (order_number, customer_id, status, subtotal, tax_amount, shipping_cost, total_amount, 
                    payment_method, street, city, state, zip_code, country, confirmed_at)
VALUES
('ORD-1234567898-MIX00001', 4, 'DELIVERED', 8000.00, 800.00, 0.00, 8800.00, 'CREDIT_CARD',
 '400 Regular Ave', 'Chennai', 'Tamil Nadu', '600001', 'India', CURRENT_TIMESTAMP),
('ORD-1234567899-MIX00002', 4, 'DELIVERED', 7000.00, 700.00, 0.00, 7700.00, 'CREDIT_CARD',
 '400 Regular Ave', 'Chennai', 'Tamil Nadu', '600001', 'India', CURRENT_TIMESTAMP),
('ORD-1234567900-MIX00003', 4, 'CONFIRMED', 6000.00, 600.00, 0.00, 6600.00, 'UPI',
 '400 Regular Ave', 'Chennai', 'Tamil Nadu', '600001', 'India', CURRENT_TIMESTAMP),
('ORD-1234567901-MIX00004', 4, 'DELIVERED', 5000.00, 500.00, 0.00, 5500.00, 'DEBIT_CARD',
 '400 Regular Ave', 'Chennai', 'Tamil Nadu', '600001', 'India', CURRENT_TIMESTAMP);

-- Customer 5: MID_SPEND (single transaction based) but HIGH_VOLUME (total based)
-- Has mid-range single transactions but many of them
INSERT INTO orders (order_number, customer_id, status, subtotal, tax_amount, shipping_cost, total_amount, 
                    payment_method, street, city, state, zip_code, country, confirmed_at)
VALUES
('ORD-1234567902-MIXH0001', 5, 'DELIVERED', 22000.00, 2200.00, 0.00, 24200.00, 'CREDIT_CARD',
 '500 Premium Blvd', 'Hyderabad', 'Telangana', '500001', 'India', CURRENT_TIMESTAMP),
('ORD-1234567903-MIXH0002', 5, 'DELIVERED', 20000.00, 2000.00, 0.00, 22000.00, 'CREDIT_CARD',
 '500 Premium Blvd', 'Hyderabad', 'Telangana', '500001', 'India', CURRENT_TIMESTAMP),
('ORD-1234567904-MIXH0003', 5, 'CONFIRMED', 18000.00, 1800.00, 0.00, 19800.00, 'UPI',
 '500 Premium Blvd', 'Hyderabad', 'Telangana', '500001', 'India', CURRENT_TIMESTAMP);

-- Sample order items for all orders
INSERT INTO order_items (order_id, product_id, product_name, sku, quantity, unit_price, total_price)
VALUES
-- Customer 1 order items (HIGH_SPEND)
(1, 'PROD-LUXURY-001', 'Diamond Necklace', 'DJ-NECK-001', 1, 55000.00, 55000.00),
(2, 'PROD-LUXURY-002', 'Gold Watch', 'GW-ROLEX-001', 1, 15000.00, 15000.00),
(3, 'PROD-LUXURY-003', 'Designer Bag', 'DB-LV-001', 1, 8000.00, 8000.00),

-- Customer 2 order items (MID_SPEND)
(4, 'PROD-MID-001', 'Smartphone', 'SP-IPHONE-15', 1, 25000.00, 25000.00),
(5, 'PROD-MID-002', 'Wireless Earbuds', 'WE-AIRPODS-001', 2, 2500.00, 5000.00),

-- Customer 3 order items (LOW_SPEND)
(6, 'PROD-LOW-001', 'Wireless Mouse', 'WM-2024-BLK', 2, 2500.00, 5000.00),
(7, 'PROD-LOW-002', 'USB Cable', 'USB-C-3FT', 3, 1000.00, 3000.00),
(8, 'PROD-LOW-003', 'Phone Case', 'PC-SILICON-001', 2, 1000.00, 2000.00),

-- Customer 4 order items (LOW_SPEND but MID_VOLUME)
(9, 'PROD-REG-001', 'Bluetooth Speaker', 'BS-JBL-001', 2, 4000.00, 8000.00),
(10, 'PROD-REG-002', 'Fitness Band', 'FB-XIAOMI-001', 2, 3500.00, 7000.00),
(11, 'PROD-REG-003', 'Power Bank', 'PB-ANKER-001', 3, 2000.00, 6000.00),
(12, 'PROD-REG-004', 'Webcam', 'WC-LOGI-001', 1, 5000.00, 5000.00),

-- Customer 5 order items (MID_SPEND but HIGH_VOLUME)
(13, 'PROD-PREM-001', 'Laptop', 'LP-DELL-XPS', 1, 22000.00, 22000.00),
(14, 'PROD-PREM-002', 'Monitor 27"', 'MON-27-4K', 1, 20000.00, 20000.00),
(15, 'PROD-PREM-003', 'Mechanical Keyboard', 'KB-MECH-RGB', 1, 18000.00, 18000.00);








