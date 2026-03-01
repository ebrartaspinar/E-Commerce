BEGIN;

-- Welcome notifications for all users
INSERT INTO notifications (id, user_id, type, channel, title, content, status, is_read, created_at, updated_at, version) VALUES
-- Sellers
('a0000001-0000-0000-0000-000000000001'::uuid, '10000000-0000-0000-0000-000000000001'::uuid, 'IN_APP', 'WELCOME', 'Welcome to TrendyolClone!', 'Welcome Defacto Official! Your seller account is ready. Start listing your products and reach millions of customers!', 'SENT', true, NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days', 0),
('a0000001-0000-0000-0000-000000000002'::uuid, '10000000-0000-0000-0000-000000000002'::uuid, 'IN_APP', 'WELCOME', 'Welcome to TrendyolClone!', 'Welcome Koton Store! Your seller account is ready. Start listing your products and reach millions of customers!', 'SENT', true, NOW() - INTERVAL '29 days', NOW() - INTERVAL '29 days', 0),
('a0000001-0000-0000-0000-000000000003'::uuid, '10000000-0000-0000-0000-000000000003'::uuid, 'IN_APP', 'WELCOME', 'Welcome to TrendyolClone!', 'Welcome LC Waikiki! Your seller account is ready. Start listing your products and reach millions of customers!', 'SENT', true, NOW() - INTERVAL '28 days', NOW() - INTERVAL '28 days', 0),
('a0000001-0000-0000-0000-000000000004'::uuid, '10000000-0000-0000-0000-000000000004'::uuid, 'IN_APP', 'WELCOME', 'Welcome to TrendyolClone!', 'Welcome Mavi Jeans! Your seller account is ready. Start listing your products and reach millions of customers!', 'SENT', true, NOW() - INTERVAL '27 days', NOW() - INTERVAL '27 days', 0),
('a0000001-0000-0000-0000-000000000005'::uuid, '10000000-0000-0000-0000-000000000005'::uuid, 'IN_APP', 'WELCOME', 'Welcome to TrendyolClone!', 'Welcome Atasun Optik! Your seller account is ready. Start listing your products and reach millions of customers!', 'SENT', true, NOW() - INTERVAL '26 days', NOW() - INTERVAL '26 days', 0),
-- Buyers
('a0000001-0000-0000-0000-000000000006'::uuid, '20000000-0000-0000-0000-000000000001'::uuid, 'IN_APP', 'WELCOME', 'Welcome to TrendyolClone!', 'Welcome Ahmet! Your adventure in the marketplace begins now. Explore thousands of products and find the best deals!', 'SENT', true, NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days', 0),
('a0000001-0000-0000-0000-000000000007'::uuid, '20000000-0000-0000-0000-000000000002'::uuid, 'IN_APP', 'WELCOME', 'Welcome to TrendyolClone!', 'Welcome Elif! Your adventure in the marketplace begins now. Explore thousands of products and find the best deals!', 'SENT', true, NOW() - INTERVAL '24 days', NOW() - INTERVAL '24 days', 0),
('a0000001-0000-0000-0000-000000000008'::uuid, '20000000-0000-0000-0000-000000000003'::uuid, 'IN_APP', 'WELCOME', 'Welcome to TrendyolClone!', 'Welcome Mehmet! Your adventure in the marketplace begins now. Explore thousands of products and find the best deals!', 'SENT', true, NOW() - INTERVAL '23 days', NOW() - INTERVAL '23 days', 0),
('a0000001-0000-0000-0000-000000000009'::uuid, '20000000-0000-0000-0000-000000000004'::uuid, 'IN_APP', 'WELCOME', 'Welcome to TrendyolClone!', 'Welcome Zeynep! Your adventure in the marketplace begins now. Explore thousands of products and find the best deals!', 'SENT', true, NOW() - INTERVAL '22 days', NOW() - INTERVAL '22 days', 0),
('a0000001-0000-0000-0000-000000000010'::uuid, '20000000-0000-0000-0000-000000000005'::uuid, 'IN_APP', 'WELCOME', 'Welcome to TrendyolClone!', 'Welcome Emre! Your adventure in the marketplace begins now. Explore thousands of products and find the best deals!', 'SENT', true, NOW() - INTERVAL '21 days', NOW() - INTERVAL '21 days', 0);

-- Order confirmation notifications
INSERT INTO notifications (id, user_id, type, channel, title, content, status, is_read, created_at, updated_at, version) VALUES
('a0000002-0000-0000-0000-000000000001'::uuid, '20000000-0000-0000-0000-000000000001'::uuid, 'IN_APP', 'ORDER_CONFIRMATION', 'Order Confirmed!', 'Your order TY-20260215-ABC123 has been confirmed. We are preparing your items for shipment!', 'SENT', true, NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days', 0),
('a0000002-0000-0000-0000-000000000002'::uuid, '20000000-0000-0000-0000-000000000002'::uuid, 'IN_APP', 'ORDER_CONFIRMATION', 'Order Confirmed!', 'Your order TY-20260216-DEF456 has been confirmed. We are preparing your items for shipment!', 'SENT', true, NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days', 0),
('a0000002-0000-0000-0000-000000000003'::uuid, '20000000-0000-0000-0000-000000000003'::uuid, 'IN_APP', 'ORDER_CONFIRMATION', 'Order Confirmed!', 'Your order TY-20260217-GHI789 has been confirmed. We are preparing your items for shipment!', 'SENT', true, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days', 0),
('a0000002-0000-0000-0000-000000000004'::uuid, '20000000-0000-0000-0000-000000000004'::uuid, 'IN_APP', 'ORDER_CONFIRMATION', 'Order Confirmed!', 'Your order TY-20260218-JKL012 has been confirmed. We are preparing your items for shipment!', 'SENT', false, NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days', 0),
('a0000002-0000-0000-0000-000000000005'::uuid, '20000000-0000-0000-0000-000000000005'::uuid, 'IN_APP', 'ORDER_CONFIRMATION', 'Order Confirmed!', 'Your order TY-20260219-MNO345 has been confirmed. We are preparing your items for shipment!', 'SENT', false, NOW() - INTERVAL '11 days', NOW() - INTERVAL '11 days', 0);

-- Payment success notifications
INSERT INTO notifications (id, user_id, type, channel, title, content, status, is_read, created_at, updated_at, version) VALUES
('a0000003-0000-0000-0000-000000000001'::uuid, '20000000-0000-0000-0000-000000000001'::uuid, 'IN_APP', 'PAYMENT_SUCCESS', 'Payment Successful!', 'Your payment of 2,499.00 TL for order TY-20260215-ABC123 has been processed successfully.', 'SENT', true, NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days', 0),
('a0000003-0000-0000-0000-000000000002'::uuid, '20000000-0000-0000-0000-000000000002'::uuid, 'IN_APP', 'PAYMENT_SUCCESS', 'Payment Successful!', 'Your payment of 899.90 TL for order TY-20260216-DEF456 has been processed successfully.', 'SENT', true, NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days', 0),
('a0000003-0000-0000-0000-000000000003'::uuid, '20000000-0000-0000-0000-000000000003'::uuid, 'IN_APP', 'PAYMENT_SUCCESS', 'Payment Successful!', 'Your payment of 4,599.00 TL for order TY-20260217-GHI789 has been processed successfully.', 'SENT', true, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days', 0),
('a0000003-0000-0000-0000-000000000004'::uuid, '20000000-0000-0000-0000-000000000004'::uuid, 'IN_APP', 'PAYMENT_SUCCESS', 'Payment Successful!', 'Your payment of 1,250.00 TL for order TY-20260218-JKL012 has been processed successfully.', 'SENT', false, NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days', 0);

-- Payment failed notifications
INSERT INTO notifications (id, user_id, type, channel, title, content, status, is_read, created_at, updated_at, version) VALUES
('a0000003-0000-0000-0000-000000000005'::uuid, '20000000-0000-0000-0000-000000000006'::uuid, 'IN_APP', 'PAYMENT_FAILED', 'Payment Failed', 'Your payment for order TY-20260220-STU901 could not be processed. Please try again or use a different payment method.', 'SENT', false, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', 0);

-- Order shipped notifications
INSERT INTO notifications (id, user_id, type, channel, title, content, status, is_read, created_at, updated_at, version) VALUES
('a0000004-0000-0000-0000-000000000001'::uuid, '20000000-0000-0000-0000-000000000001'::uuid, 'IN_APP', 'ORDER_SHIPPED', 'Order Shipped!', 'Great news! Your order TY-20260215-ABC123 has been shipped and is on its way to you!', 'SENT', true, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days', 0),
('a0000004-0000-0000-0000-000000000002'::uuid, '20000000-0000-0000-0000-000000000002'::uuid, 'IN_APP', 'ORDER_SHIPPED', 'Order Shipped!', 'Great news! Your order TY-20260216-DEF456 has been shipped and is on its way to you!', 'SENT', false, NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days', 0);

-- Order delivered notifications
INSERT INTO notifications (id, user_id, type, channel, title, content, status, is_read, created_at, updated_at, version) VALUES
('a0000005-0000-0000-0000-000000000001'::uuid, '20000000-0000-0000-0000-000000000001'::uuid, 'IN_APP', 'ORDER_DELIVERED', 'Order Delivered!', 'Your order TY-20260215-ABC123 has been delivered. We hope you enjoy your purchase! Please leave a review.', 'SENT', false, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', 0);

-- Price drop notifications
INSERT INTO notifications (id, user_id, type, channel, title, content, status, is_read, created_at, updated_at, version) VALUES
('a0000006-0000-0000-0000-000000000001'::uuid, '20000000-0000-0000-0000-000000000001'::uuid, 'IN_APP', 'PRICE_DROP', 'Price Drop Alert!', 'Samsung Galaxy S24 Ultra price dropped from 54,999 TL to 49,999 TL! Grab it before it''s gone!', 'SENT', false, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days', 0),
('a0000006-0000-0000-0000-000000000002'::uuid, '20000000-0000-0000-0000-000000000003'::uuid, 'IN_APP', 'PRICE_DROP', 'Price Drop Alert!', 'Nike Air Max 270 price dropped from 3,499 TL to 2,799 TL! Limited time offer!', 'SENT', false, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', 0),
('a0000006-0000-0000-0000-000000000003'::uuid, '20000000-0000-0000-0000-000000000005'::uuid, 'IN_APP', 'PRICE_DROP', 'Price Drop Alert!', 'Apple AirPods Pro 2 price dropped from 9,999 TL to 7,999 TL! Don''t miss out!', 'SENT', false, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days', 0);

-- Stock low notifications (for sellers)
INSERT INTO notifications (id, user_id, type, channel, title, content, status, is_read, created_at, updated_at, version) VALUES
('a0000007-0000-0000-0000-000000000001'::uuid, '10000000-0000-0000-0000-000000000001'::uuid, 'IN_APP', 'STOCK_LOW', 'Low Stock Warning', 'Your product "Defacto Basic T-Shirt" has only 5 items left in stock. Consider restocking soon!', 'SENT', false, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days', 0),
('a0000007-0000-0000-0000-000000000002'::uuid, '10000000-0000-0000-0000-000000000002'::uuid, 'IN_APP', 'STOCK_LOW', 'Low Stock Warning', 'Your product "Koton Slim Fit Jean" has only 3 items left in stock. Consider restocking soon!', 'SENT', false, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', 0);

COMMIT;
