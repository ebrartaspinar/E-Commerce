BEGIN;

-- ============================================================
-- SEED DATA: Payments for orders
-- References order-service order numbers: TY-YYYYMMDD-XXXXXX
-- References user-service buyer IDs: d0000001-0000-0000-0000-00000000000X
-- Payment IDs match order.payment_id references: b1000001-0000-0000-0000-00000000000X
-- ============================================================

-- ===================== COMPLETED PAYMENTS (Delivered Orders) =====================

-- Payment for Order 1: Ahmet - Samsung Galaxy S24 Ultra (DELIVERED)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000001', 'TY-20240401-000001', 'd0000001-0000-0000-0000-000000000001', 54999.99, 'TRY', 'CREDIT_CARD', 'COMPLETED', 'TXN-20240401-A1B2C3D4', NULL, '2024-04-01 14:32:00', '2024-04-01 14:30:00', '2024-04-01 14:32:00');

-- Payment for Order 2: Fatma - Koton Gomlek + Mavi Jean (DELIVERED)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000002', 'TY-20240405-000002', 'd0000001-0000-0000-0000-000000000002', 1014.97, 'TRY', 'CREDIT_CARD', 'COMPLETED', 'TXN-20240405-E5F6G7H8', NULL, '2024-04-05 11:02:00', '2024-04-05 11:00:00', '2024-04-05 11:02:00');

-- Payment for Order 3: Mehmet - MacBook Air M3 (DELIVERED)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000003', 'TY-20240410-000003', 'd0000001-0000-0000-0000-000000000003', 54999.00, 'TRY', 'BANK_TRANSFER', 'COMPLETED', 'TXN-20240410-I9J0K1L2', NULL, '2024-04-10 09:15:00', '2024-04-10 09:00:00', '2024-04-10 09:15:00');

-- Payment for Order 4: Ayse - Bellona Kose Koltuk (DELIVERED)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000004', 'TY-20240412-000004', 'd0000001-0000-0000-0000-000000000004', 10999.00, 'TRY', 'CREDIT_CARD', 'COMPLETED', 'TXN-20240412-M3N4O5P6', NULL, '2024-04-12 16:03:00', '2024-04-12 16:00:00', '2024-04-12 16:03:00');

-- Payment for Order 5: Emre - Nike + Adidas Shoes (DELIVERED)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000005', 'TY-20240415-000005', 'd0000001-0000-0000-0000-000000000007', 6498.00, 'TRY', 'DEBIT_CARD', 'COMPLETED', 'TXN-20240415-Q7R8S9T0', NULL, '2024-04-15 10:02:00', '2024-04-15 10:00:00', '2024-04-15 10:02:00');

-- ===================== COMPLETED PAYMENTS (Shipped Orders) =====================

-- Payment for Order 6: Mustafa - Arzum Okka + Emsan Tava (SHIPPED)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000006', 'TY-20240501-000006', 'd0000001-0000-0000-0000-000000000005', 2712.99, 'TRY', 'CREDIT_CARD', 'COMPLETED', 'TXN-20240501-U1V2W3X4', NULL, '2024-05-01 14:02:00', '2024-05-01 14:00:00', '2024-05-01 14:02:00');

-- Payment for Order 7: Zeynep - iPhone 15 Pro Max (SHIPPED)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000007', 'TY-20240503-000007', 'd0000001-0000-0000-0000-000000000006', 74999.00, 'TRY', 'CREDIT_CARD', 'COMPLETED', 'TXN-20240503-Y5Z6A7B8', NULL, '2024-05-03 11:03:00', '2024-05-03 11:00:00', '2024-05-03 11:03:00');

-- Payment for Order 8: Elif - LC Waikiki Elbise + Koton Canta (SHIPPED)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000008', 'TY-20240505-000008', 'd0000001-0000-0000-0000-000000000008', 764.97, 'TRY', 'DEBIT_CARD', 'COMPLETED', 'TXN-20240505-C9D0E1F2', NULL, '2024-05-05 16:32:00', '2024-05-05 16:30:00', '2024-05-05 16:32:00');

-- Payment for Order 9: Selin - Samsung 65 QLED TV (SHIPPED)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000009', 'TY-20240507-000009', 'd0000001-0000-0000-0000-000000000010', 23999.00, 'TRY', 'BANK_TRANSFER', 'COMPLETED', 'TXN-20240507-G3H4I5J6', NULL, '2024-05-07 10:20:00', '2024-05-07 10:00:00', '2024-05-07 10:20:00');

-- ===================== COMPLETED PAYMENTS (Processing Orders) =====================

-- Payment for Order 10: Ahmet - Sapiens + Atomic Habits (PROCESSING)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000010', 'TY-20240510-000010', 'd0000001-0000-0000-0000-000000000001', 126.99, 'TRY', 'CREDIT_CARD', 'COMPLETED', 'TXN-20240510-K7L8M9N0', NULL, '2024-05-10 08:02:00', '2024-05-10 08:00:00', '2024-05-10 08:02:00');

-- Payment for Order 11: Burak - Monster Abra Laptop (PROCESSING)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000011', 'TY-20240511-000011', 'd0000001-0000-0000-0000-000000000009', 28999.00, 'TRY', 'CREDIT_CARD', 'COMPLETED', 'TXN-20240511-O1P2Q3R4', NULL, '2024-05-11 15:02:00', '2024-05-11 15:00:00', '2024-05-11 15:02:00');

-- ===================== COMPLETED PAYMENTS (Payment Completed Orders) =====================

-- Payment for Order 12: Fatma - Nike Esofman + Under Armour (PAYMENT_COMPLETED)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000012', 'TY-20240512-000012', 'd0000001-0000-0000-0000-000000000002', 1912.99, 'TRY', 'DEBIT_CARD', 'COMPLETED', 'TXN-20240512-S5T6U7V8', NULL, '2024-05-12 09:32:00', '2024-05-12 09:30:00', '2024-05-12 09:32:00');

-- Payment for Order 13: Mehmet - Korkmaz + Nescafe (PAYMENT_COMPLETED)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000013', 'TY-20240513-000013', 'd0000001-0000-0000-0000-000000000003', 1513.99, 'TRY', 'CREDIT_CARD', 'COMPLETED', 'TXN-20240513-W9X0Y1Z2', NULL, '2024-05-13 14:03:00', '2024-05-13 14:00:00', '2024-05-13 14:03:00');

-- ===================== PENDING PAYMENTS (Created/Payment Pending Orders) =====================

-- Payment for Order 15: Zeynep - LEGO Ferrari (PAYMENT_PENDING on order)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000015', 'TY-20240515-000015', 'd0000001-0000-0000-0000-000000000006', 6013.99, 'TRY', 'CREDIT_CARD', 'PENDING', NULL, NULL, NULL, '2024-05-15 14:00:00', '2024-05-15 14:00:00');

-- Payment for Order 17: Emre - Defacto Polo + Kargo (PAYMENT_PENDING on order)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000017', 'TY-20240517-000017', 'd0000001-0000-0000-0000-000000000007', 364.97, 'TRY', 'CREDIT_CARD', 'PROCESSING', NULL, NULL, NULL, '2024-05-17 12:00:00', '2024-05-17 12:05:00');

-- ===================== FAILED PAYMENTS =====================

-- Payment for Order 19: Ayse - Ray-Ban Wayfarer (CANCELLED due to payment failure)
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000019', 'TY-20240425-000019', 'd0000001-0000-0000-0000-000000000004', 3999.00, 'TRY', 'CREDIT_CARD', 'FAILED', 'TXN-20240425-FAIL01AB', 'Kart limiti yetersiz. Banka red kodu: 51 - Yetersiz bakiye/limit', NULL, '2024-04-25 18:00:00', '2024-04-25 18:05:00');

-- Additional failed payment: Burak tried to pay but card was declined
INSERT INTO payments (id, order_number, user_id, amount, currency, method, status, transaction_id, failure_reason, completed_at, created_at, updated_at) VALUES
('b1000001-0000-0000-0000-000000000020', 'TY-20240420-000018', 'd0000001-0000-0000-0000-000000000009', 9499.00, 'TRY', 'CREDIT_CARD', 'FAILED', 'TXN-20240420-FAIL02CD', '3D Secure dogrulama basarisiz. Islem zaman asimina ugradi.', NULL, '2024-04-20 10:00:00', '2024-04-20 10:03:00');

COMMIT;
