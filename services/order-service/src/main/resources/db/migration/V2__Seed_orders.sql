BEGIN;

-- ============================================================
-- SEED DATA: Orders (20 sample orders in various states)
-- User IDs reference user-service buyers:
--   d0000001-0000-0000-0000-00000000000X (1-10)
-- Product IDs reference product-service:
--   f0000001-0000-0000-0000-0000000000XX
-- Seller IDs reference user-service sellers:
--   c0000001-0000-0000-0000-00000000000X (1-5)
-- ============================================================

-- ===================== DELIVERED ORDERS =====================

-- Order 1: Ahmet - Samsung Galaxy S24 Ultra (DELIVERED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000001', 'TY-20240401-000001', 'd0000001-0000-0000-0000-000000000001', 'DELIVERED', 'Ahmet Yilmaz', '+905551000001', 'Ataturk Cad. No:42 D:5 Kadikoy', 'Istanbul', 'Kadikoy', '34710', 59999.99, 5000.00, 0.00, 54999.99, 'b1000001-0000-0000-0000-000000000001', NULL, '2024-04-01 14:30:00', '2024-04-05 10:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000001', 'a1000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001', 'Samsung Galaxy S24 Ultra 256GB', 'https://picsum.photos/seed/samsung-s24-ultra/400/400', 59999.99, 1, 59999.99, 'DELIVERED', '2024-04-01 14:30:00', '2024-04-05 10:00:00');

-- Order 2: Fatma - Koton Gomlek + Mavi Jean (DELIVERED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000002', 'TY-20240405-000002', 'd0000001-0000-0000-0000-000000000002', 'DELIVERED', 'Fatma Kaya', '+905551000002', 'Kizilay Mah. Ziya Gokalp Cad. No:28 D:3', 'Ankara', 'Cankaya', '06420', 1099.98, 100.00, 14.99, 1014.97, 'b1000001-0000-0000-0000-000000000002', 'Hediye paketi olsun lutfen', '2024-04-05 11:00:00', '2024-04-09 16:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000002', 'a1000001-0000-0000-0000-000000000002', 'f0000001-0000-0000-0000-000000000014', 'c0000001-0000-0000-0000-000000000002', 'Koton Kadin Uzun Kollu Gomlek', 'https://picsum.photos/seed/koton-kadin-gomlek/400/400', 349.99, 1, 349.99, 'DELIVERED', '2024-04-05 11:00:00', '2024-04-09 16:00:00'),
('a2000001-0000-0000-0000-000000000003', 'a1000001-0000-0000-0000-000000000002', 'f0000001-0000-0000-0000-000000000017', 'c0000001-0000-0000-0000-000000000004', 'Mavi Kadin Yuksek Bel Jean', 'https://picsum.photos/seed/mavi-kadin-jean/400/400', 749.99, 1, 749.99, 'DELIVERED', '2024-04-05 11:00:00', '2024-04-09 16:00:00');

-- Order 3: Mehmet - MacBook Air M3 (DELIVERED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000003', 'TY-20240410-000003', 'd0000001-0000-0000-0000-000000000003', 'DELIVERED', 'Mehmet Demir', '+905551000003', 'Alsancak Mah. Kibris Sehitleri Cad. No:102 D:7', 'Izmir', 'Konak', '35220', 54999.00, 0.00, 0.00, 54999.00, 'b1000001-0000-0000-0000-000000000003', NULL, '2024-04-10 09:00:00', '2024-04-14 14:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000004', 'a1000001-0000-0000-0000-000000000003', 'f0000001-0000-0000-0000-000000000005', 'c0000001-0000-0000-0000-000000000001', 'Apple MacBook Air M3 15 inc 256GB', 'https://picsum.photos/seed/macbook-air-m3/400/400', 54999.00, 1, 54999.00, 'DELIVERED', '2024-04-10 09:00:00', '2024-04-14 14:00:00');

-- Order 4: Ayse - Bellona Kose Koltuk (DELIVERED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000004', 'TY-20240412-000004', 'd0000001-0000-0000-0000-000000000004', 'DELIVERED', 'Ayse Celik', '+905551000004', 'Bagdat Cad. No:312 D:9 Suadiye', 'Istanbul', 'Kadikoy', '34740', 12999.00, 2000.00, 0.00, 10999.00, 'b1000001-0000-0000-0000-000000000004', 'Asansor var, 5. kat', '2024-04-12 16:00:00', '2024-04-18 11:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000005', 'a1000001-0000-0000-0000-000000000004', 'f0000001-0000-0000-0000-000000000031', 'c0000001-0000-0000-0000-000000000005', 'Bellona Panda Kose Koltuk Takimi', 'https://picsum.photos/seed/bellona-kose-koltuk/400/400', 12999.00, 1, 12999.00, 'DELIVERED', '2024-04-12 16:00:00', '2024-04-18 11:00:00');

-- Order 5: Emre - Nike Air Force + Adidas Superstar (DELIVERED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000005', 'TY-20240415-000005', 'd0000001-0000-0000-0000-000000000007', 'DELIVERED', 'Emre Sahin', '+905551000007', 'Besiktas Mah. Barbaros Bulvari No:56 D:8', 'Istanbul', 'Besiktas', '34353', 6998.00, 500.00, 0.00, 6498.00, 'b1000001-0000-0000-0000-000000000005', NULL, '2024-04-15 10:00:00', '2024-04-19 15:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000006', 'a1000001-0000-0000-0000-000000000005', 'f0000001-0000-0000-0000-000000000024', 'c0000001-0000-0000-0000-000000000001', 'Nike Air Force 1 07 Beyaz Spor Ayakkabi', 'https://picsum.photos/seed/nike-air-force-1/400/400', 3499.00, 1, 3499.00, 'DELIVERED', '2024-04-15 10:00:00', '2024-04-19 15:00:00'),
('a2000001-0000-0000-0000-000000000007', 'a1000001-0000-0000-0000-000000000005', 'f0000001-0000-0000-0000-000000000025', 'c0000001-0000-0000-0000-000000000001', 'Adidas Superstar Beyaz Spor Ayakkabi', 'https://picsum.photos/seed/adidas-superstar/400/400', 3499.00, 1, 3499.00, 'DELIVERED', '2024-04-15 10:00:00', '2024-04-19 15:00:00');

-- ===================== SHIPPED ORDERS =====================

-- Order 6: Mustafa - Arzum Okka + Emsan Tava Seti (SHIPPED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000006', 'TY-20240501-000006', 'd0000001-0000-0000-0000-000000000005', 'SHIPPED', 'Mustafa Ozturk', '+905551000005', 'Muratpasa Mah. Ismet Inonu Bulvari No:65 D:2', 'Antalya', 'Muratpasa', '07040', 3198.00, 500.00, 14.99, 2712.99, 'b1000001-0000-0000-0000-000000000006', NULL, '2024-05-01 14:00:00', '2024-05-03 09:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000008', 'a1000001-0000-0000-0000-000000000006', 'f0000001-0000-0000-0000-000000000038', 'c0000001-0000-0000-0000-000000000005', 'Arzum Okka Turk Kahvesi Makinesi', 'https://picsum.photos/seed/arzum-okka/400/400', 2199.00, 1, 2199.00, 'SHIPPED', '2024-05-01 14:00:00', '2024-05-03 09:00:00'),
('a2000001-0000-0000-0000-000000000009', 'a1000001-0000-0000-0000-000000000006', 'f0000001-0000-0000-0000-000000000040', 'c0000001-0000-0000-0000-000000000005', 'Emsan Premium Granit Tava Seti 3lu', 'https://picsum.photos/seed/emsan-tava-seti/400/400', 999.00, 1, 999.00, 'SHIPPED', '2024-05-01 14:00:00', '2024-05-03 09:00:00');

-- Order 7: Zeynep - iPhone 15 Pro Max (SHIPPED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000007', 'TY-20240503-000007', 'd0000001-0000-0000-0000-000000000006', 'SHIPPED', 'Zeynep Arslan', '+905551000006', 'Nisantasi Mah. Valikonagi Cad. No:88 D:12', 'Istanbul', 'Sisli', '34365', 74999.00, 0.00, 0.00, 74999.00, 'b1000001-0000-0000-0000-000000000007', 'Lutfen kapida birakmayin', '2024-05-03 11:00:00', '2024-05-05 08:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000010', 'a1000001-0000-0000-0000-000000000007', 'f0000001-0000-0000-0000-000000000002', 'c0000001-0000-0000-0000-000000000001', 'iPhone 15 Pro Max 256GB', 'https://picsum.photos/seed/iphone-15-pro-max/400/400', 74999.00, 1, 74999.00, 'SHIPPED', '2024-05-03 11:00:00', '2024-05-05 08:00:00');

-- Order 8: Elif - LC Waikiki Elbise + Koton Canta (SHIPPED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000008', 'TY-20240505-000008', 'd0000001-0000-0000-0000-000000000008', 'SHIPPED', 'Elif Dogan', '+905551000008', 'Moda Mah. Caferaga Cad. No:33 D:2', 'Istanbul', 'Kadikoy', '34710', 899.98, 150.00, 14.99, 764.97, 'b1000001-0000-0000-0000-000000000008', NULL, '2024-05-05 16:30:00', '2024-05-07 10:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000011', 'a1000001-0000-0000-0000-000000000008', 'f0000001-0000-0000-0000-000000000016', 'c0000001-0000-0000-0000-000000000003', 'LC Waikiki Kadin Midi Elbise', 'https://picsum.photos/seed/lcw-kadin-elbise/400/400', 449.99, 1, 449.99, 'SHIPPED', '2024-05-05 16:30:00', '2024-05-07 10:00:00'),
('a2000001-0000-0000-0000-000000000012', 'a1000001-0000-0000-0000-000000000008', 'f0000001-0000-0000-0000-000000000028', 'c0000001-0000-0000-0000-000000000002', 'Koton Kadin Zincir Askili Omuz Cantasi', 'https://picsum.photos/seed/koton-omuz-cantasi/400/400', 449.99, 1, 449.99, 'SHIPPED', '2024-05-05 16:30:00', '2024-05-07 10:00:00');

-- Order 9: Selin - Samsung 65 QLED TV (SHIPPED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000009', 'TY-20240507-000009', 'd0000001-0000-0000-0000-000000000010', 'SHIPPED', 'Selin Yildiz', '+905551000010', 'Caddebostan Mah. Bagdat Cad. No:250 D:4', 'Istanbul', 'Kadikoy', '34728', 26999.00, 3000.00, 0.00, 23999.00, 'b1000001-0000-0000-0000-000000000009', 'Asansor yok, 3. kat', '2024-05-07 10:00:00', '2024-05-09 14:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000013', 'a1000001-0000-0000-0000-000000000009', 'f0000001-0000-0000-0000-000000000009', 'c0000001-0000-0000-0000-000000000005', 'Samsung 65 inc QLED 4K TV', 'https://picsum.photos/seed/samsung-65-qled/400/400', 26999.00, 1, 26999.00, 'SHIPPED', '2024-05-07 10:00:00', '2024-05-09 14:00:00');

-- ===================== PROCESSING ORDERS =====================

-- Order 10: Ahmet - Sapiens + Atomic Habits (PROCESSING)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000010', 'TY-20240510-000010', 'd0000001-0000-0000-0000-000000000001', 'PROCESSING', 'Ahmet Yilmaz', '+905551000001', 'Buyukdere Cad. No:185 Levent Plaza Kat:12', 'Istanbul', 'Sisli', '34394', 168.00, 51.00, 9.99, 126.99, 'b1000001-0000-0000-0000-000000000010', NULL, '2024-05-10 08:00:00', '2024-05-10 10:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000014', 'a1000001-0000-0000-0000-000000000010', 'f0000001-0000-0000-0000-000000000049', 'c0000001-0000-0000-0000-000000000003', 'Sapiens: Insan Turunun Kisa Tarihi', 'https://picsum.photos/seed/sapiens-kitap/400/400', 89.00, 1, 89.00, 'CONFIRMED', '2024-05-10 08:00:00', '2024-05-10 10:00:00'),
('a2000001-0000-0000-0000-000000000015', 'a1000001-0000-0000-0000-000000000010', 'f0000001-0000-0000-0000-000000000050', 'c0000001-0000-0000-0000-000000000003', 'Atomic Habits - Atomik Aliskanliklar', 'https://picsum.photos/seed/atomic-habits/400/400', 79.00, 1, 79.00, 'CONFIRMED', '2024-05-10 08:00:00', '2024-05-10 10:00:00');

-- Order 11: Burak - Monster Abra Laptop (PROCESSING)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000011', 'TY-20240511-000011', 'd0000001-0000-0000-0000-000000000009', 'PROCESSING', 'Burak Kilic', '+905551000009', 'Cankaya Mah. Tunali Hilmi Cad. No:110 D:6', 'Ankara', 'Cankaya', '06690', 31999.00, 3000.00, 0.00, 28999.00, 'b1000001-0000-0000-0000-000000000011', 'Hafta ici teslimat lutfen', '2024-05-11 15:00:00', '2024-05-11 17:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000016', 'a1000001-0000-0000-0000-000000000011', 'f0000001-0000-0000-0000-000000000007', 'c0000001-0000-0000-0000-000000000001', 'Monster Abra A5 V21.3 Oyun Bilgisayari', 'https://picsum.photos/seed/monster-abra-a5/400/400', 31999.00, 1, 31999.00, 'CONFIRMED', '2024-05-11 15:00:00', '2024-05-11 17:00:00');

-- ===================== PAYMENT_COMPLETED ORDERS =====================

-- Order 12: Fatma - Nike Esofman + Under Armour Tisort (PAYMENT_COMPLETED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000012', 'TY-20240512-000012', 'd0000001-0000-0000-0000-000000000002', 'PAYMENT_COMPLETED', 'Fatma Kaya', '+905551000002', 'Kizilay Mah. Ziya Gokalp Cad. No:28 D:3', 'Ankara', 'Cankaya', '06420', 2548.00, 650.00, 14.99, 1912.99, 'b1000001-0000-0000-0000-000000000012', NULL, '2024-05-12 09:30:00', '2024-05-12 09:35:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000017', 'a1000001-0000-0000-0000-000000000012', 'f0000001-0000-0000-0000-000000000042', 'c0000001-0000-0000-0000-000000000001', 'Nike Dri-FIT Erkek Esofman Takimi', 'https://picsum.photos/seed/nike-esofman/400/400', 1999.00, 1, 1999.00, 'PENDING', '2024-05-12 09:30:00', '2024-05-12 09:35:00'),
('a2000001-0000-0000-0000-000000000018', 'a1000001-0000-0000-0000-000000000012', 'f0000001-0000-0000-0000-000000000045', 'c0000001-0000-0000-0000-000000000001', 'Under Armour Erkek Spor Tisort', 'https://picsum.photos/seed/underarmour-tisort/400/400', 549.00, 1, 549.00, 'PENDING', '2024-05-12 09:30:00', '2024-05-12 09:35:00');

-- Order 13: Mehmet - Korkmaz Tencere + Nescafe Gold (PAYMENT_COMPLETED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000013', 'TY-20240513-000013', 'd0000001-0000-0000-0000-000000000003', 'PAYMENT_COMPLETED', 'Mehmet Demir', '+905551000003', 'Alsancak Mah. Kibris Sehitleri Cad. No:102 D:7', 'Izmir', 'Konak', '35220', 1818.00, 319.00, 14.99, 1513.99, 'b1000001-0000-0000-0000-000000000013', NULL, '2024-05-13 14:00:00', '2024-05-13 14:05:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000019', 'a1000001-0000-0000-0000-000000000013', 'f0000001-0000-0000-0000-000000000039', 'c0000001-0000-0000-0000-000000000005', 'Korkmaz Proline Dusuk Basincli Tencere 7L', 'https://picsum.photos/seed/korkmaz-tencere/400/400', 1599.00, 1, 1599.00, 'PENDING', '2024-05-13 14:00:00', '2024-05-13 14:05:00'),
('a2000001-0000-0000-0000-000000000020', 'a1000001-0000-0000-0000-000000000013', 'f0000001-0000-0000-0000-000000000052', 'c0000001-0000-0000-0000-000000000003', 'Nescafe Gold 200gr Kavanoz', 'https://picsum.photos/seed/nescafe-gold/400/400', 219.00, 1, 219.00, 'PENDING', '2024-05-13 14:00:00', '2024-05-13 14:05:00');

-- ===================== CREATED / PAYMENT_PENDING ORDERS =====================

-- Order 14: Mustafa - Adidas Ultraboost (CREATED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000014', 'TY-20240515-000014', 'd0000001-0000-0000-0000-000000000005', 'CREATED', 'Mustafa Ozturk', '+905551000005', 'Muratpasa Mah. Ismet Inonu Bulvari No:65 D:2', 'Antalya', 'Muratpasa', '07040', 3999.00, 1000.00, 0.00, 2999.00, NULL, NULL, '2024-05-15 11:00:00', '2024-05-15 11:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000021', 'a1000001-0000-0000-0000-000000000014', 'f0000001-0000-0000-0000-000000000043', 'c0000001-0000-0000-0000-000000000001', 'Adidas Ultraboost Light Kosu Ayakkabisi', 'https://picsum.photos/seed/adidas-ultraboost/400/400', 3999.00, 1, 3999.00, 'PENDING', '2024-05-15 11:00:00', '2024-05-15 11:00:00');

-- Order 15: Zeynep - LEGO Ferrari (PAYMENT_PENDING)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000015', 'TY-20240515-000015', 'd0000001-0000-0000-0000-000000000006', 'PAYMENT_PENDING', 'Zeynep Arslan', '+905551000006', 'Nisantasi Mah. Valikonagi Cad. No:88 D:12', 'Istanbul', 'Sisli', '34365', 6999.00, 1000.00, 14.99, 6013.99, NULL, 'Hediye icin', '2024-05-15 14:00:00', '2024-05-15 14:05:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000022', 'a1000001-0000-0000-0000-000000000015', 'f0000001-0000-0000-0000-000000000051', 'c0000001-0000-0000-0000-000000000003', 'Lego Technic Ferrari Daytona SP3', 'https://picsum.photos/seed/lego-ferrari/400/400', 6999.00, 1, 6999.00, 'PENDING', '2024-05-15 14:00:00', '2024-05-15 14:05:00');

-- Order 16: Selin - MAC Ruj + L'Oreal Serum (CREATED)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000016', 'TY-20240516-000016', 'd0000001-0000-0000-0000-000000000010', 'CREATED', 'Selin Yildiz', '+905551000010', 'Caddebostan Mah. Bagdat Cad. No:250 D:4', 'Istanbul', 'Kadikoy', '34728', 1128.00, 220.00, 14.99, 922.99, NULL, NULL, '2024-05-16 15:00:00', '2024-05-16 15:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000023', 'a1000001-0000-0000-0000-000000000016', 'f0000001-0000-0000-0000-000000000046', 'c0000001-0000-0000-0000-000000000002', 'Gratis Mac Ruby Woo Ruj', 'https://picsum.photos/seed/mac-ruby-woo/400/400', 749.00, 1, 749.00, 'PENDING', '2024-05-16 15:00:00', '2024-05-16 15:00:00'),
('a2000001-0000-0000-0000-000000000024', 'a1000001-0000-0000-0000-000000000016', 'f0000001-0000-0000-0000-000000000047', 'c0000001-0000-0000-0000-000000000002', 'Loreal Paris Revitalift Serum', 'https://picsum.photos/seed/loreal-revitalift/400/400', 379.00, 1, 379.00, 'PENDING', '2024-05-16 15:00:00', '2024-05-16 15:00:00');

-- Order 17: Emre - Defacto Polo + Defacto Kargo Pantolon (PAYMENT_PENDING)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000017', 'TY-20240517-000017', 'd0000001-0000-0000-0000-000000000007', 'PAYMENT_PENDING', 'Emre Sahin', '+905551000007', 'Besiktas Mah. Barbaros Bulvari No:56 D:8', 'Istanbul', 'Besiktas', '34353', 599.98, 250.00, 14.99, 364.97, NULL, NULL, '2024-05-17 12:00:00', '2024-05-17 12:05:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000025', 'a1000001-0000-0000-0000-000000000017', 'f0000001-0000-0000-0000-000000000019', 'c0000001-0000-0000-0000-000000000001', 'Defacto Erkek Slim Fit Polo Yaka Tisort', 'https://picsum.photos/seed/defacto-erkek-polo/400/400', 199.99, 1, 199.99, 'PENDING', '2024-05-17 12:00:00', '2024-05-17 12:05:00'),
('a2000001-0000-0000-0000-000000000026', 'a1000001-0000-0000-0000-000000000017', 'f0000001-0000-0000-0000-000000000023', 'c0000001-0000-0000-0000-000000000001', 'Defacto Erkek Regular Fit Kargo Pantolon', 'https://picsum.photos/seed/defacto-erkek-kargo/400/400', 399.99, 1, 399.99, 'PENDING', '2024-05-17 12:00:00', '2024-05-17 12:05:00');

-- ===================== CANCELLED ORDERS =====================

-- Order 18: Burak - Xiaomi Redmi Note 13 (CANCELLED - user changed mind)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, cancel_reason, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000018', 'TY-20240420-000018', 'd0000001-0000-0000-0000-000000000009', 'CANCELLED', 'Burak Kilic', '+905551000009', 'Cankaya Mah. Tunali Hilmi Cad. No:110 D:6', 'Ankara', 'Cankaya', '06690', 10999.00, 1500.00, 0.00, 9499.00, NULL, NULL, 'Baska bir model almaya karar verdim', '2024-04-20 10:00:00', '2024-04-20 12:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000027', 'a1000001-0000-0000-0000-000000000018', 'f0000001-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000001', 'Xiaomi Redmi Note 13 Pro 256GB', 'https://picsum.photos/seed/xiaomi-redmi-note-13/400/400', 10999.00, 1, 10999.00, 'CANCELLED', '2024-04-20 10:00:00', '2024-04-20 12:00:00');

-- Order 19: Ayse - Ray-Ban Wayfarer (CANCELLED - payment failed)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, cancel_reason, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000019', 'TY-20240425-000019', 'd0000001-0000-0000-0000-000000000004', 'CANCELLED', 'Ayse Celik', '+905551000004', 'Bagdat Cad. No:312 D:9 Suadiye', 'Istanbul', 'Kadikoy', '34740', 3999.00, 0.00, 0.00, 3999.00, 'b1000001-0000-0000-0000-000000000019', NULL, 'Odeme basarisiz oldu, kart limiti yetersiz', '2024-04-25 18:00:00', '2024-04-25 18:10:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000028', 'a1000001-0000-0000-0000-000000000019', 'f0000001-0000-0000-0000-000000000048', 'c0000001-0000-0000-0000-000000000005', 'Atasun Rayban Wayfarer Gunes Gozlugu', 'https://picsum.photos/seed/rayban-wayfarer/400/400', 3999.00, 1, 3999.00, 'CANCELLED', '2024-04-25 18:00:00', '2024-04-25 18:10:00');

-- Order 20: Elif - Ulker Cikolata x3 + English Home Nevresim (CANCELLED - item out of stock)
INSERT INTO orders (id, order_number, user_id, status, full_name, phone, full_address, city, district, postal_code, total_amount, discount_amount, shipping_cost, final_amount, payment_id, notes, cancel_reason, created_at, updated_at) VALUES
('a1000001-0000-0000-0000-000000000020', 'TY-20240428-000020', 'd0000001-0000-0000-0000-000000000008', 'CANCELLED', 'Elif Dogan', '+905551000008', 'Moda Mah. Caferaga Cad. No:33 D:2', 'Istanbul', 'Kadikoy', '34710', 809.69, 200.00, 14.99, 624.68, NULL, NULL, 'Urun stokta kalmadi', '2024-04-28 11:00:00', '2024-04-28 13:00:00');

INSERT INTO order_items (id, order_id, product_id, seller_id, product_name, product_image, unit_price, quantity, total_price, status, created_at, updated_at) VALUES
('a2000001-0000-0000-0000-000000000029', 'a1000001-0000-0000-0000-000000000020', 'f0000001-0000-0000-0000-000000000053', 'c0000001-0000-0000-0000-000000000003', 'Ulker Cikolata Paketi 6li', 'https://picsum.photos/seed/ulker-cikolata/400/400', 69.90, 3, 209.70, 'CANCELLED', '2024-04-28 11:00:00', '2024-04-28 13:00:00'),
('a2000001-0000-0000-0000-000000000030', 'a1000001-0000-0000-0000-000000000020', 'f0000001-0000-0000-0000-000000000036', 'c0000001-0000-0000-0000-000000000003', 'English Home Pamuklu Nevresim Takimi Cift', 'https://picsum.photos/seed/englishhome-nevresim/400/400', 599.99, 1, 599.99, 'CANCELLED', '2024-04-28 11:00:00', '2024-04-28 13:00:00');

COMMIT;
