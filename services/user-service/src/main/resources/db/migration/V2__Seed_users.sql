BEGIN;

-- ============================================================
-- SEED DATA: Users (5 Sellers + 10 Buyers)
-- Password for all users: Password123!
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- ============================================================

-- ===================== SELLERS =====================
INSERT INTO users (id, email, password, first_name, last_name, phone, role, status, created_at, updated_at) VALUES
('c0000001-0000-0000-0000-000000000001', 'seller@defacto.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Defacto', 'Official', '+905321000001', 'SELLER', 'ACTIVE', '2024-01-15 09:00:00', '2024-01-15 09:00:00'),
('c0000001-0000-0000-0000-000000000002', 'seller@koton.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Koton', 'Store', '+905321000002', 'SELLER', 'ACTIVE', '2024-01-16 10:00:00', '2024-01-16 10:00:00'),
('c0000001-0000-0000-0000-000000000003', 'seller@lcwaikiki.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'LC', 'Waikiki', '+905321000003', 'SELLER', 'ACTIVE', '2024-01-17 11:00:00', '2024-01-17 11:00:00'),
('c0000001-0000-0000-0000-000000000004', 'seller@mavi.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Mavi', 'Jeans', '+905321000004', 'SELLER', 'ACTIVE', '2024-02-01 08:30:00', '2024-02-01 08:30:00'),
('c0000001-0000-0000-0000-000000000005', 'seller@atasun.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Atasun', 'Optik', '+905321000005', 'SELLER', 'ACTIVE', '2024-02-05 14:00:00', '2024-02-05 14:00:00');

-- ===================== BUYERS =====================
INSERT INTO users (id, email, password, first_name, last_name, phone, role, status, created_at, updated_at) VALUES
('d0000001-0000-0000-0000-000000000001', 'ahmet.yilmaz@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ahmet', 'Yilmaz', '+905551000001', 'BUYER', 'ACTIVE', '2024-02-10 12:00:00', '2024-02-10 12:00:00'),
('d0000001-0000-0000-0000-000000000002', 'fatma.kaya@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Fatma', 'Kaya', '+905551000002', 'BUYER', 'ACTIVE', '2024-02-12 09:30:00', '2024-02-12 09:30:00'),
('d0000001-0000-0000-0000-000000000003', 'mehmet.demir@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Mehmet', 'Demir', '+905551000003', 'BUYER', 'ACTIVE', '2024-02-15 16:00:00', '2024-02-15 16:00:00'),
('d0000001-0000-0000-0000-000000000004', 'ayse.celik@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ayse', 'Celik', '+905551000004', 'BUYER', 'ACTIVE', '2024-02-18 10:15:00', '2024-02-18 10:15:00'),
('d0000001-0000-0000-0000-000000000005', 'mustafa.ozturk@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Mustafa', 'Ozturk', '+905551000005', 'BUYER', 'ACTIVE', '2024-03-01 11:00:00', '2024-03-01 11:00:00'),
('d0000001-0000-0000-0000-000000000006', 'zeynep.arslan@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Zeynep', 'Arslan', '+905551000006', 'BUYER', 'ACTIVE', '2024-03-05 13:45:00', '2024-03-05 13:45:00'),
('d0000001-0000-0000-0000-000000000007', 'emre.sahin@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Emre', 'Sahin', '+905551000007', 'BUYER', 'ACTIVE', '2024-03-10 08:00:00', '2024-03-10 08:00:00'),
('d0000001-0000-0000-0000-000000000008', 'elif.dogan@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Elif', 'Dogan', '+905551000008', 'BUYER', 'ACTIVE', '2024-03-12 15:30:00', '2024-03-12 15:30:00'),
('d0000001-0000-0000-0000-000000000009', 'burak.kilic@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Burak', 'Kilic', '+905551000009', 'BUYER', 'ACTIVE', '2024-03-15 17:00:00', '2024-03-15 17:00:00'),
('d0000001-0000-0000-0000-000000000010', 'selin.yildiz@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Selin', 'Yildiz', '+905551000010', 'BUYER', 'ACTIVE', '2024-03-20 09:00:00', '2024-03-20 09:00:00');

-- ===================== ADDRESSES =====================

-- Ahmet Yilmaz - 3 addresses
INSERT INTO addresses (id, user_id, title, full_address, city, district, postal_code, is_default, created_at, updated_at) VALUES
('e0000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000001', 'Ev Adresim', 'Ataturk Cad. No:42 D:5 Kadikoy', 'Istanbul', 'Kadikoy', '34710', true, '2024-02-10 12:10:00', '2024-02-10 12:10:00'),
('e0000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000001', 'Is Adresim', 'Buyukdere Cad. No:185 Levent Plaza Kat:12', 'Istanbul', 'Sisli', '34394', false, '2024-02-10 12:15:00', '2024-02-10 12:15:00'),
('e0000001-0000-0000-0000-000000000003', 'd0000001-0000-0000-0000-000000000001', 'Yazlik', 'Sahil Yolu Cad. No:15 Cesme', 'Izmir', 'Cesme', '35930', false, '2024-02-10 12:20:00', '2024-02-10 12:20:00');

-- Fatma Kaya - 2 addresses
INSERT INTO addresses (id, user_id, title, full_address, city, district, postal_code, is_default, created_at, updated_at) VALUES
('e0000001-0000-0000-0000-000000000004', 'd0000001-0000-0000-0000-000000000002', 'Ev', 'Kizilay Mah. Ziya Gokalp Cad. No:28 D:3', 'Ankara', 'Cankaya', '06420', true, '2024-02-12 09:40:00', '2024-02-12 09:40:00'),
('e0000001-0000-0000-0000-000000000005', 'd0000001-0000-0000-0000-000000000002', 'Annem', 'Cumhuriyet Mah. Istasyon Cad. No:55', 'Ankara', 'Altindag', '06030', false, '2024-02-12 09:45:00', '2024-02-12 09:45:00');

-- Mehmet Demir - 2 addresses
INSERT INTO addresses (id, user_id, title, full_address, city, district, postal_code, is_default, created_at, updated_at) VALUES
('e0000001-0000-0000-0000-000000000006', 'd0000001-0000-0000-0000-000000000003', 'Ev Adresim', 'Alsancak Mah. Kibris Sehitleri Cad. No:102 D:7', 'Izmir', 'Konak', '35220', true, '2024-02-15 16:10:00', '2024-02-15 16:10:00'),
('e0000001-0000-0000-0000-000000000007', 'd0000001-0000-0000-0000-000000000003', 'Ofis', 'Bayrakli Mah. Ankara Cad. No:81 B Blok Kat:5', 'Izmir', 'Bayrakli', '35530', false, '2024-02-15 16:15:00', '2024-02-15 16:15:00');

-- Ayse Celik - 3 addresses
INSERT INTO addresses (id, user_id, title, full_address, city, district, postal_code, is_default, created_at, updated_at) VALUES
('e0000001-0000-0000-0000-000000000008', 'd0000001-0000-0000-0000-000000000004', 'Evim', 'Bagdat Cad. No:312 D:9 Suadiye', 'Istanbul', 'Kadikoy', '34740', true, '2024-02-18 10:20:00', '2024-02-18 10:20:00'),
('e0000001-0000-0000-0000-000000000009', 'd0000001-0000-0000-0000-000000000004', 'Is Yeri', 'Maslak Mah. Eski Buyukdere Cad. No:7 Veko Giz Plaza', 'Istanbul', 'Sariyer', '34398', false, '2024-02-18 10:25:00', '2024-02-18 10:25:00'),
('e0000001-0000-0000-0000-000000000010', 'd0000001-0000-0000-0000-000000000004', 'Aile Evi', 'Ataturk Bulvari No:200 D:4', 'Bursa', 'Osmangazi', '16040', false, '2024-02-18 10:30:00', '2024-02-18 10:30:00');

-- Mustafa Ozturk - 2 addresses
INSERT INTO addresses (id, user_id, title, full_address, city, district, postal_code, is_default, created_at, updated_at) VALUES
('e0000001-0000-0000-0000-000000000011', 'd0000001-0000-0000-0000-000000000005', 'Ev', 'Muratpasa Mah. Ismet Inonu Bulvari No:65 D:2', 'Antalya', 'Muratpasa', '07040', true, '2024-03-01 11:10:00', '2024-03-01 11:10:00'),
('e0000001-0000-0000-0000-000000000012', 'd0000001-0000-0000-0000-000000000005', 'Dukkan', 'Konyaalti Cad. No:22', 'Antalya', 'Konyaalti', '07070', false, '2024-03-01 11:15:00', '2024-03-01 11:15:00');

-- Zeynep Arslan - 2 addresses
INSERT INTO addresses (id, user_id, title, full_address, city, district, postal_code, is_default, created_at, updated_at) VALUES
('e0000001-0000-0000-0000-000000000013', 'd0000001-0000-0000-0000-000000000006', 'Ev Adresim', 'Nisantasi Mah. Valikonagi Cad. No:88 D:12', 'Istanbul', 'Sisli', '34365', true, '2024-03-05 13:50:00', '2024-03-05 13:50:00'),
('e0000001-0000-0000-0000-000000000014', 'd0000001-0000-0000-0000-000000000006', 'Yurt', 'Beytepe Mah. Universite Cad. No:10 KYK', 'Ankara', 'Cankaya', '06800', false, '2024-03-05 13:55:00', '2024-03-05 13:55:00');

-- Emre Sahin - 2 addresses
INSERT INTO addresses (id, user_id, title, full_address, city, district, postal_code, is_default, created_at, updated_at) VALUES
('e0000001-0000-0000-0000-000000000015', 'd0000001-0000-0000-0000-000000000007', 'Ev', 'Besiktas Mah. Barbaros Bulvari No:56 D:8', 'Istanbul', 'Besiktas', '34353', true, '2024-03-10 08:10:00', '2024-03-10 08:10:00'),
('e0000001-0000-0000-0000-000000000016', 'd0000001-0000-0000-0000-000000000007', 'Anne Evi', 'Osmangazi Mah. Fevzi Cakmak Cad. No:120', 'Eskisehir', 'Odunpazari', '26010', false, '2024-03-10 08:15:00', '2024-03-10 08:15:00');

-- Elif Dogan - 3 addresses
INSERT INTO addresses (id, user_id, title, full_address, city, district, postal_code, is_default, created_at, updated_at) VALUES
('e0000001-0000-0000-0000-000000000017', 'd0000001-0000-0000-0000-000000000008', 'Ev', 'Moda Mah. Caferaga Cad. No:33 D:2', 'Istanbul', 'Kadikoy', '34710', true, '2024-03-12 15:35:00', '2024-03-12 15:35:00'),
('e0000001-0000-0000-0000-000000000018', 'd0000001-0000-0000-0000-000000000008', 'Is', 'Esentepe Mah. Harman Sok. No:4 Levent 199', 'Istanbul', 'Sisli', '34394', false, '2024-03-12 15:40:00', '2024-03-12 15:40:00'),
('e0000001-0000-0000-0000-000000000019', 'd0000001-0000-0000-0000-000000000008', 'Baba Evi', 'Selcuklu Mah. Yeni Istanbul Cad. No:48', 'Konya', 'Selcuklu', '42060', false, '2024-03-12 15:45:00', '2024-03-12 15:45:00');

-- Burak Kilic - 2 addresses
INSERT INTO addresses (id, user_id, title, full_address, city, district, postal_code, is_default, created_at, updated_at) VALUES
('e0000001-0000-0000-0000-000000000020', 'd0000001-0000-0000-0000-000000000009', 'Ev', 'Cankaya Mah. Tunali Hilmi Cad. No:110 D:6', 'Ankara', 'Cankaya', '06690', true, '2024-03-15 17:10:00', '2024-03-15 17:10:00'),
('e0000001-0000-0000-0000-000000000021', 'd0000001-0000-0000-0000-000000000009', 'Ofis', 'Sogutozu Mah. 2180. Cad. No:6 Cyberpark', 'Ankara', 'Cankaya', '06530', false, '2024-03-15 17:15:00', '2024-03-15 17:15:00');

-- Selin Yildiz - 2 addresses
INSERT INTO addresses (id, user_id, title, full_address, city, district, postal_code, is_default, created_at, updated_at) VALUES
('e0000001-0000-0000-0000-000000000022', 'd0000001-0000-0000-0000-000000000010', 'Ev Adresim', 'Caddebostan Mah. Bagdat Cad. No:250 D:4', 'Istanbul', 'Kadikoy', '34728', true, '2024-03-20 09:10:00', '2024-03-20 09:10:00'),
('e0000001-0000-0000-0000-000000000023', 'd0000001-0000-0000-0000-000000000010', 'Yazlik', 'Kalkan Mah. Yali Boyu Cad. No:18', 'Antalya', 'Kas', '07960', false, '2024-03-20 09:15:00', '2024-03-20 09:15:00');

COMMIT;
