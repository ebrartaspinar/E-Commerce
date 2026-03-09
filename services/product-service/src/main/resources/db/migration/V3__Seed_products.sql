BEGIN;

-- ============================================================
-- SEED DATA: Products (50+ products across categories)
-- Seller IDs reference user-service sellers:
--   c0000001-...-001 = Defacto Official
--   c0000001-...-002 = Koton Store
--   c0000001-...-003 = LC Waikiki
--   c0000001-...-004 = Mavi Jeans
--   c0000001-...-005 = Atasun Optik
-- Category IDs reference V2__Seed_categories.sql
-- ============================================================

-- ===================== ELEKTRONIK > CEP TELEFONU =====================
INSERT INTO products (id, seller_id, name, description, price, discounted_price, stock_quantity, sku, status, category_id, brand, average_rating, review_count, created_at, updated_at) VALUES
('f0000001-0000-0000-0000-000000000001', 'c0000001-0000-0000-0000-000000000001', 'Samsung Galaxy S24 Ultra 256GB', 'Samsung Galaxy S24 Ultra 256GB Titan Siyah Cep Telefonu. Snapdragon 8 Gen 3 islemci, 200MP kamera, S Pen destegi.', 64999.99, 59999.99, 45, 'ELEC-001', 'ACTIVE', 'b0000001-0000-0000-0000-000000000001', 'Samsung', 4.7, 128, '2024-03-01 10:00:00', '2024-03-01 10:00:00'),
('f0000001-0000-0000-0000-000000000002', 'c0000001-0000-0000-0000-000000000001', 'iPhone 15 Pro Max 256GB', 'Apple iPhone 15 Pro Max 256GB Dogal Titanyum. A17 Pro cip, 48MP kamera sistemi, USB-C.', 74999.00, NULL, 30, 'ELEC-002', 'ACTIVE', 'b0000001-0000-0000-0000-000000000001', 'Apple', 4.8, 256, '2024-03-02 11:00:00', '2024-03-02 11:00:00'),
('f0000001-0000-0000-0000-000000000003', 'c0000001-0000-0000-0000-000000000001', 'Xiaomi Redmi Note 13 Pro 256GB', 'Xiaomi Redmi Note 13 Pro 256GB Siyah. 200MP kamera, AMOLED ekran, 5000mAh batarya.', 12499.00, 10999.00, 120, 'ELEC-003', 'ACTIVE', 'b0000001-0000-0000-0000-000000000001', 'Xiaomi', 4.3, 89, '2024-03-03 09:00:00', '2024-03-03 09:00:00'),
('f0000001-0000-0000-0000-000000000004', 'c0000001-0000-0000-0000-000000000001', 'Samsung Galaxy A55 128GB', 'Samsung Galaxy A55 5G 128GB Mavi. Super AMOLED ekran, 5000mAh pil, IP67 su gecirmez.', 14999.00, 13499.00, 85, 'ELEC-004', 'ACTIVE', 'b0000001-0000-0000-0000-000000000001', 'Samsung', 4.2, 67, '2024-03-05 10:00:00', '2024-03-05 10:00:00'),

-- ===================== ELEKTRONIK > BILGISAYAR =====================
('f0000001-0000-0000-0000-000000000005', 'c0000001-0000-0000-0000-000000000001', 'Apple MacBook Air M3 15 inc 256GB', 'Apple MacBook Air M3 cip, 15 inc, 8GB RAM, 256GB SSD. Yildiz Isigi rengi.', 54999.00, NULL, 20, 'ELEC-005', 'ACTIVE', 'b0000001-0000-0000-0000-000000000002', 'Apple', 4.9, 45, '2024-03-06 11:30:00', '2024-03-06 11:30:00'),
('f0000001-0000-0000-0000-000000000006', 'c0000001-0000-0000-0000-000000000001', 'Lenovo IdeaPad Slim 3 15.6 inc', 'Lenovo IdeaPad Slim 3 AMD Ryzen 5 7520U, 8GB RAM, 512GB SSD, 15.6 inc FHD.', 16999.00, 14999.00, 55, 'ELEC-006', 'ACTIVE', 'b0000001-0000-0000-0000-000000000002', 'Lenovo', 4.1, 38, '2024-03-07 08:00:00', '2024-03-07 08:00:00'),
('f0000001-0000-0000-0000-000000000007', 'c0000001-0000-0000-0000-000000000001', 'Monster Abra A5 V21.3 Oyun Bilgisayari', 'Monster Abra A5 Intel Core i7-13700H, RTX 4060 8GB, 16GB RAM, 512GB SSD, 15.6 inc 144Hz.', 34999.00, 31999.00, 25, 'ELEC-007', 'ACTIVE', 'b0000001-0000-0000-0000-000000000002', 'Monster', 4.4, 72, '2024-03-08 14:00:00', '2024-03-08 14:00:00'),

-- ===================== ELEKTRONIK > TELEVIZYON =====================
('f0000001-0000-0000-0000-000000000008', 'c0000001-0000-0000-0000-000000000005', 'Vestel 55 inc 4K Smart TV', 'Vestel 55UA9630 55 inc 139 Ekran Uydu Alicili 4K Ultra HD Smart TV.', 14999.00, 12999.00, 40, 'ELEC-008', 'ACTIVE', 'b0000001-0000-0000-0000-000000000003', 'Vestel', 4.0, 55, '2024-03-09 10:00:00', '2024-03-09 10:00:00'),
('f0000001-0000-0000-0000-000000000009', 'c0000001-0000-0000-0000-000000000005', 'Samsung 65 inc QLED 4K TV', 'Samsung QE65Q60CAUXTR 65 inc 163 Ekran QLED 4K UHD Smart TV. Quantum HDR, Tizen OS.', 29999.00, 26999.00, 15, 'ELEC-009', 'ACTIVE', 'b0000001-0000-0000-0000-000000000003', 'Samsung', 4.6, 33, '2024-03-10 09:00:00', '2024-03-10 09:00:00'),
('f0000001-0000-0000-0000-000000000010', 'c0000001-0000-0000-0000-000000000005', 'Arcelik 50 inc 4K Smart TV', 'Arcelik A50 D 695 A 50 inc 4K Ultra HD Android TV. Dahili uydu alici, Dolby Vision.', 11999.00, NULL, 60, 'ELEC-010', 'ACTIVE', 'b0000001-0000-0000-0000-000000000003', 'Arcelik', 3.9, 41, '2024-03-11 11:00:00', '2024-03-11 11:00:00'),

-- ===================== ELEKTRONIK > KULAKLIK =====================
('f0000001-0000-0000-0000-000000000011', 'c0000001-0000-0000-0000-000000000001', 'Apple AirPods Pro 2. Nesil USB-C', 'Apple AirPods Pro 2. Nesil USB-C sarj kutulu. Aktif Gurultu Engelleme, Adaptif Ses.', 8999.00, 7999.00, 100, 'ELEC-011', 'ACTIVE', 'b0000001-0000-0000-0000-000000000004', 'Apple', 4.7, 190, '2024-03-12 10:00:00', '2024-03-12 10:00:00'),
('f0000001-0000-0000-0000-000000000012', 'c0000001-0000-0000-0000-000000000001', 'JBL Tune 520BT Kablosuz Kulaklik', 'JBL Tune 520BT Bluetooth kulak ustu kulaklik. 57 saat pil omru, coklu baglanti.', 1299.00, 999.00, 200, 'ELEC-012', 'ACTIVE', 'b0000001-0000-0000-0000-000000000004', 'JBL', 4.2, 145, '2024-03-13 08:30:00', '2024-03-13 08:30:00'),
('f0000001-0000-0000-0000-000000000013', 'c0000001-0000-0000-0000-000000000001', 'Sony WH-1000XM5 Kablosuz Kulaklik', 'Sony WH-1000XM5 Bluetooth kulak ustu kulaklik. Otomatik Gurultu Engelleme Optimizer.', 11499.00, NULL, 35, 'ELEC-013', 'INACTIVE', 'b0000001-0000-0000-0000-000000000004', 'Sony', 4.8, 88, '2024-03-14 12:00:00', '2024-03-14 12:00:00'),

-- ===================== MODA > KADIN GIYIM =====================
('f0000001-0000-0000-0000-000000000014', 'c0000001-0000-0000-0000-000000000002', 'Koton Kadin Uzun Kollu Gomlek', 'Koton Kadin Klasik Yaka Uzun Kollu Pamuklu Gomlek. Slim fit, beyaz renk.', 449.99, 349.99, 300, 'FASH-001', 'ACTIVE', 'b0000002-0000-0000-0000-000000000001', 'Koton', 4.1, 56, '2024-03-15 09:00:00', '2024-03-15 09:00:00'),
('f0000001-0000-0000-0000-000000000015', 'c0000001-0000-0000-0000-000000000001', 'Defacto Kadin Oversize Tisort', 'Defacto Kadin Regular Fit Yuvarlak Yaka Oversize Kisa Kollu Tisort. Siyah.', 179.99, 129.99, 500, 'FASH-002', 'ACTIVE', 'b0000002-0000-0000-0000-000000000001', 'Defacto', 4.0, 92, '2024-03-15 09:30:00', '2024-03-15 09:30:00'),
('f0000001-0000-0000-0000-000000000016', 'c0000001-0000-0000-0000-000000000003', 'LC Waikiki Kadin Midi Elbise', 'LC Waikiki Kadin V Yaka Uzun Kollu Cicek Desenli Midi Elbise. Viskon kumas.', 599.99, 449.99, 180, 'FASH-003', 'ACTIVE', 'b0000002-0000-0000-0000-000000000001', 'LC Waikiki', 4.3, 78, '2024-03-16 10:00:00', '2024-03-16 10:00:00'),
('f0000001-0000-0000-0000-000000000017', 'c0000001-0000-0000-0000-000000000004', 'Mavi Kadin Yuksek Bel Jean', 'Mavi Cindy Kadin Yuksek Bel Skinny Jean. Koyu mavi yikama, streC kumas.', 899.99, 749.99, 250, 'FASH-004', 'ACTIVE', 'b0000002-0000-0000-0000-000000000001', 'Mavi', 4.5, 134, '2024-03-17 11:00:00', '2024-03-17 11:00:00'),
('f0000001-0000-0000-0000-000000000018', 'c0000001-0000-0000-0000-000000000002', 'Koton Kadin Triko Hirka', 'Koton Kadin Dugmeli Triko Hirka. Oversized kesim, krem rengi.', 699.99, NULL, 150, 'FASH-005', 'ACTIVE', 'b0000002-0000-0000-0000-000000000001', 'Koton', 4.2, 43, '2024-03-18 09:00:00', '2024-03-18 09:00:00'),

-- ===================== MODA > ERKEK GIYIM =====================
('f0000001-0000-0000-0000-000000000019', 'c0000001-0000-0000-0000-000000000001', 'Defacto Erkek Slim Fit Polo Yaka Tisort', 'Defacto Erkek Slim Fit Polo Yaka Kisa Kollu Pike Tisort. Lacivert.', 249.99, 199.99, 400, 'FASH-006', 'ACTIVE', 'b0000002-0000-0000-0000-000000000002', 'Defacto', 4.0, 67, '2024-03-19 10:00:00', '2024-03-19 10:00:00'),
('f0000001-0000-0000-0000-000000000020', 'c0000001-0000-0000-0000-000000000004', 'Mavi Erkek Jake Slim Fit Jean', 'Mavi Jake Erkek Slim Fit Straight Leg Jean. Koyu indigo, premium denim.', 999.99, 849.99, 200, 'FASH-007', 'ACTIVE', 'b0000002-0000-0000-0000-000000000002', 'Mavi', 4.6, 187, '2024-03-20 11:00:00', '2024-03-20 11:00:00'),
('f0000001-0000-0000-0000-000000000021', 'c0000001-0000-0000-0000-000000000003', 'LC Waikiki Erkek Kapusonlu Sweatshirt', 'LC Waikiki Erkek Kapusonlu Uzun Kollu Basic Sweatshirt. Pamuklu, gri melanj.', 399.99, 299.99, 350, 'FASH-008', 'ACTIVE', 'b0000002-0000-0000-0000-000000000002', 'LC Waikiki', 4.1, 55, '2024-03-21 08:00:00', '2024-03-21 08:00:00'),
('f0000001-0000-0000-0000-000000000022', 'c0000001-0000-0000-0000-000000000002', 'Koton Erkek Keten Gomlek', 'Koton Erkek Regular Fit Keten Gomlek. Uzun kollu, dugmeli, bej renk.', 549.99, NULL, 180, 'FASH-009', 'ACTIVE', 'b0000002-0000-0000-0000-000000000002', 'Koton', 4.3, 29, '2024-03-22 09:00:00', '2024-03-22 09:00:00'),
('f0000001-0000-0000-0000-000000000023', 'c0000001-0000-0000-0000-000000000001', 'Defacto Erkek Regular Fit Kargo Pantolon', 'Defacto Erkek Regular Fit Kargo Cepli Pantolon. Haki renk, %100 pamuk.', 499.99, 399.99, 220, 'FASH-010', 'ACTIVE', 'b0000002-0000-0000-0000-000000000002', 'Defacto', 3.8, 44, '2024-03-23 10:00:00', '2024-03-23 10:00:00'),

-- ===================== MODA > AYAKKABI =====================
('f0000001-0000-0000-0000-000000000024', 'c0000001-0000-0000-0000-000000000001', 'Nike Air Force 1 07 Beyaz Spor Ayakkabi', 'Nike Air Force 1 07 Erkek Beyaz Spor Ayakkabi. Deri ust yuzey, Air tabani.', 3999.00, 3499.00, 75, 'FASH-011', 'ACTIVE', 'b0000002-0000-0000-0000-000000000003', 'Nike', 4.7, 210, '2024-03-24 10:00:00', '2024-03-24 10:00:00'),
('f0000001-0000-0000-0000-000000000025', 'c0000001-0000-0000-0000-000000000001', 'Adidas Superstar Beyaz Spor Ayakkabi', 'Adidas Originals Superstar Beyaz Siyah Spor Ayakkabi. Klasik kabuk burun.', 3499.00, NULL, 60, 'FASH-012', 'ACTIVE', 'b0000002-0000-0000-0000-000000000003', 'Adidas', 4.6, 165, '2024-03-25 11:00:00', '2024-03-25 11:00:00'),
('f0000001-0000-0000-0000-000000000026', 'c0000001-0000-0000-0000-000000000002', 'New Balance 574 Kadin Spor Ayakkabi', 'New Balance 574 Kadin Gunluk Spor Ayakkabi. Gri-Beyaz, suet detay.', 2999.00, 2499.00, 90, 'FASH-013', 'ACTIVE', 'b0000002-0000-0000-0000-000000000003', 'New Balance', 4.4, 78, '2024-03-26 09:00:00', '2024-03-26 09:00:00'),
('f0000001-0000-0000-0000-000000000027', 'c0000001-0000-0000-0000-000000000003', 'LC Waikiki Erkek Deri Klasik Ayakkabi', 'LC Waikiki Erkek Hakiki Deri Klasik Ayakkabi. Siyah, bagcikli.', 1299.00, 999.00, 110, 'FASH-014', 'ACTIVE', 'b0000002-0000-0000-0000-000000000003', 'LC Waikiki', 3.9, 35, '2024-03-27 10:00:00', '2024-03-27 10:00:00'),

-- ===================== MODA > CANTA =====================
('f0000001-0000-0000-0000-000000000028', 'c0000001-0000-0000-0000-000000000002', 'Koton Kadin Zincir Askili Omuz Cantasi', 'Koton Kadin Zincir Askili Kapitone Dikisli Omuz Cantasi. Siyah, suni deri.', 599.99, 449.99, 130, 'FASH-015', 'ACTIVE', 'b0000002-0000-0000-0000-000000000004', 'Koton', 4.0, 48, '2024-03-28 09:00:00', '2024-03-28 09:00:00'),
('f0000001-0000-0000-0000-000000000029', 'c0000001-0000-0000-0000-000000000001', 'Defacto Erkek Sirt Cantasi', 'Defacto Erkek Laptop Bolmeli Sirt Cantasi. Su gecirmez kumas, USB sarj cikisi.', 349.99, 279.99, 200, 'FASH-016', 'ACTIVE', 'b0000002-0000-0000-0000-000000000004', 'Defacto', 4.1, 63, '2024-03-29 10:00:00', '2024-03-29 10:00:00'),
('f0000001-0000-0000-0000-000000000030', 'c0000001-0000-0000-0000-000000000004', 'Mavi Kadin Tote Canta', 'Mavi Kadin Buyuk Boy Tote Canta. Kanvas kumas, lacivert, ic cepli.', 449.99, NULL, 95, 'FASH-017', 'ACTIVE', 'b0000002-0000-0000-0000-000000000004', 'Mavi', 4.3, 37, '2024-03-30 11:00:00', '2024-03-30 11:00:00'),

-- ===================== EV & YASAM > MOBILYA =====================
('f0000001-0000-0000-0000-000000000031', 'c0000001-0000-0000-0000-000000000005', 'Bellona Panda Kose Koltuk Takimi', 'Bellona Panda Kose Koltuk Takimi. Sandikli, yatakli, gri kumas.', 14999.00, 12999.00, 10, 'HOME-001', 'ACTIVE', 'b0000003-0000-0000-0000-000000000001', 'Bellona', 4.2, 28, '2024-04-01 10:00:00', '2024-04-01 10:00:00'),
('f0000001-0000-0000-0000-000000000032', 'c0000001-0000-0000-0000-000000000005', 'Dogtas Yatak Odasi Takimi', 'Dogtas Modern Yatak Odasi Takimi. Gardrop, sifonyer, komodin, karyola. Beyaz ceviz.', 24999.00, NULL, 12, 'HOME-002', 'ACTIVE', 'b0000003-0000-0000-0000-000000000001', 'Dogtas', 4.5, 19, '2024-04-02 09:00:00', '2024-04-02 09:00:00'),
('f0000001-0000-0000-0000-000000000033', 'c0000001-0000-0000-0000-000000000005', 'Ikea Kallax Kitaplik Rafli Dolap', 'Kallax Serisi 4x4 Kitaplik Rafli Dolap. Beyaz, 147x147cm, modular tasarim.', 3499.00, 2999.00, 35, 'HOME-003', 'ACTIVE', 'b0000003-0000-0000-0000-000000000001', 'IKEA', 4.4, 52, '2024-04-03 11:00:00', '2024-04-03 11:00:00'),
('f0000001-0000-0000-0000-000000000034', 'c0000001-0000-0000-0000-000000000005', 'Cilek Genc Odasi Calisma Masasi', 'Cilek Dynamic Genc Odasi Calisma Masasi. Cekmeceli, beyaz-mavi, 120x60cm.', 4999.00, 4299.00, 25, 'HOME-004', 'ACTIVE', 'b0000003-0000-0000-0000-000000000001', 'Cilek', 4.3, 31, '2024-04-04 10:00:00', '2024-04-04 10:00:00'),

-- ===================== EV & YASAM > DEKORASYON =====================
('f0000001-0000-0000-0000-000000000035', 'c0000001-0000-0000-0000-000000000003', 'Madame Coco Dekoratif Yastik Kilifi 2li', 'Madame Coco Keten Gorunumlu Dekoratif Yastik Kilifi Seti. 45x45cm, bej.', 199.99, 149.99, 300, 'HOME-005', 'ACTIVE', 'b0000003-0000-0000-0000-000000000002', 'Madame Coco', 4.1, 85, '2024-04-05 09:00:00', '2024-04-05 09:00:00'),
('f0000001-0000-0000-0000-000000000036', 'c0000001-0000-0000-0000-000000000003', 'English Home Pamuklu Nevresim Takimi Cift', 'English Home Floral Pamuklu Cift Kisilik Nevresim Takimi. 200x220cm, beyaz-pembe.', 799.99, 599.99, 150, 'HOME-006', 'ACTIVE', 'b0000003-0000-0000-0000-000000000002', 'English Home', 4.4, 63, '2024-04-06 10:00:00', '2024-04-06 10:00:00'),
('f0000001-0000-0000-0000-000000000037', 'c0000001-0000-0000-0000-000000000003', 'Taç Hali 160x230 Modern Desen', 'Tac Hali Premium 160x230cm Modern Geometrik Desen Hali. Gri-bej tonlari.', 2499.00, 1999.00, 40, 'HOME-007', 'ACTIVE', 'b0000003-0000-0000-0000-000000000002', 'Tac', 4.0, 27, '2024-04-07 11:00:00', '2024-04-07 11:00:00'),

-- ===================== EV & YASAM > MUTFAK =====================
('f0000001-0000-0000-0000-000000000038', 'c0000001-0000-0000-0000-000000000005', 'Arzum Okka Turk Kahvesi Makinesi', 'Arzum Okka Minio OK004 Turk Kahvesi Makinesi. Otomatik, bakir renk.', 2499.00, 2199.00, 80, 'HOME-008', 'ACTIVE', 'b0000003-0000-0000-0000-000000000003', 'Arzum', 4.5, 210, '2024-04-08 09:00:00', '2024-04-08 09:00:00'),
('f0000001-0000-0000-0000-000000000039', 'c0000001-0000-0000-0000-000000000005', 'Korkmaz Proline Dusuk Basincli Tencere 7L', 'Korkmaz A155-03 Proline Dusuk Basincli Tencere 7 Litre. Paslanmaz celik.', 1899.00, 1599.00, 65, 'HOME-009', 'ACTIVE', 'b0000003-0000-0000-0000-000000000003', 'Korkmaz', 4.6, 97, '2024-04-09 10:00:00', '2024-04-09 10:00:00'),
('f0000001-0000-0000-0000-000000000040', 'c0000001-0000-0000-0000-000000000005', 'Emsan Premium Granit Tava Seti 3lu', 'Emsan Premium Granit 3lu Tava Seti. 20-26-30cm, PFOA-Free, induksiyona uygun.', 1299.00, 999.00, 120, 'HOME-010', 'ACTIVE', 'b0000003-0000-0000-0000-000000000003', 'Emsan', 4.3, 156, '2024-04-10 11:00:00', '2024-04-10 11:00:00'),
('f0000001-0000-0000-0000-000000000041', 'c0000001-0000-0000-0000-000000000005', 'Beko Arcelik Filtre Kahve Makinesi', 'Beko FK 4510 Filtre Kahve Makinesi. 10 Fincan, otomatik kapanma, siyah.', 899.00, NULL, 90, 'HOME-011', 'ACTIVE', 'b0000003-0000-0000-0000-000000000003', 'Beko', 3.8, 42, '2024-04-11 08:00:00', '2024-04-11 08:00:00'),

-- ===================== SPOR & OUTDOOR (using root category) =====================
('f0000001-0000-0000-0000-000000000042', 'c0000001-0000-0000-0000-000000000001', 'Nike Dri-FIT Erkek Esofman Takimi', 'Nike Dri-FIT Academy Erkek Esofman Takimi. Siyah, nefes alan kumas.', 2499.00, 1999.00, 110, 'SPOR-001', 'ACTIVE', 'a0000001-0000-0000-0000-000000000004', 'Nike', 4.4, 88, '2024-04-12 09:00:00', '2024-04-12 09:00:00'),
('f0000001-0000-0000-0000-000000000043', 'c0000001-0000-0000-0000-000000000001', 'Adidas Ultraboost Light Kosu Ayakkabisi', 'Adidas Ultraboost Light Erkek Kosu Ayakkabisi. BOOST tabani, Primeknit ust.', 4999.00, 3999.00, 50, 'SPOR-002', 'ACTIVE', 'a0000001-0000-0000-0000-000000000004', 'Adidas', 4.7, 132, '2024-04-13 10:00:00', '2024-04-13 10:00:00'),
('f0000001-0000-0000-0000-000000000044', 'c0000001-0000-0000-0000-000000000003', 'Decathlon Kamp Cadiri 4 Kisilik', 'Quechua Arpenaz 4.1 Fresh&Black 4 Kisilik Kamp Cadiri. UV korumalisi.', 3999.00, NULL, 30, 'SPOR-003', 'ACTIVE', 'a0000001-0000-0000-0000-000000000004', 'Decathlon', 4.2, 45, '2024-04-14 11:00:00', '2024-04-14 11:00:00'),
('f0000001-0000-0000-0000-000000000045', 'c0000001-0000-0000-0000-000000000001', 'Under Armour Erkek Spor Tisort', 'Under Armour Tech 2.0 Erkek Kisa Kollu Spor Tisort. Nem yonetimi, siyah.', 699.00, 549.00, 250, 'SPOR-004', 'ACTIVE', 'a0000001-0000-0000-0000-000000000004', 'Under Armour', 4.3, 76, '2024-04-15 09:00:00', '2024-04-15 09:00:00'),

-- ===================== KOZMETIK (using root category) =====================
('f0000001-0000-0000-0000-000000000046', 'c0000001-0000-0000-0000-000000000002', 'Gratis Mac Ruby Woo Ruj', 'MAC Matte Lipstick Ruby Woo. Ikonik kirmizi, mat bitisli.', 899.00, 749.00, 200, 'COSM-001', 'ACTIVE', 'a0000001-0000-0000-0000-000000000006', 'MAC', 4.6, 230, '2024-04-16 10:00:00', '2024-04-16 10:00:00'),
('f0000001-0000-0000-0000-000000000047', 'c0000001-0000-0000-0000-000000000002', 'Loreal Paris Revitalift Serum', 'L''Oreal Paris Revitalift Filler %1.5 Saf Hyaluronik Asit Serum 30ml.', 449.00, 379.00, 180, 'COSM-002', 'ACTIVE', 'a0000001-0000-0000-0000-000000000006', 'L''Oreal Paris', 4.4, 178, '2024-04-17 11:00:00', '2024-04-17 11:00:00'),
('f0000001-0000-0000-0000-000000000048', 'c0000001-0000-0000-0000-000000000005', 'Atasun Rayban Wayfarer Gunes Gozlugu', 'Ray-Ban Original Wayfarer Classic RB2140. Siyah cerceve, yesil cam.', 3999.00, NULL, 40, 'COSM-003', 'ACTIVE', 'a0000001-0000-0000-0000-000000000006', 'Ray-Ban', 4.8, 95, '2024-04-18 09:00:00', '2024-04-18 09:00:00'),

-- ===================== KITAP & HOBI (using root category) =====================
('f0000001-0000-0000-0000-000000000049', 'c0000001-0000-0000-0000-000000000003', 'Sapiens: Insan Turunun Kisa Tarihi', 'Yuval Noah Harari - Sapiens: Insan Turunun Kisa Tarihi. Turkce cevirisi.', 120.00, 89.00, 400, 'BOOK-001', 'ACTIVE', 'a0000001-0000-0000-0000-000000000005', 'Kolektif Kitap', 4.8, 342, '2024-04-19 10:00:00', '2024-04-19 10:00:00'),
('f0000001-0000-0000-0000-000000000050', 'c0000001-0000-0000-0000-000000000003', 'Atomic Habits - Atomik Aliskanliklar', 'James Clear - Atomik Aliskanliklar. Kucuk degisiklikler, buyuk sonuclar.', 99.00, 79.00, 350, 'BOOK-002', 'ACTIVE', 'a0000001-0000-0000-0000-000000000005', 'Pegasus Yayinlari', 4.7, 289, '2024-04-20 11:00:00', '2024-04-20 11:00:00'),
('f0000001-0000-0000-0000-000000000051', 'c0000001-0000-0000-0000-000000000003', 'Lego Technic Ferrari Daytona SP3', 'Lego Technic 42143 Ferrari Daytona SP3. 3778 parca, +18 yas.', 7999.00, 6999.00, 15, 'BOOK-003', 'ACTIVE', 'a0000001-0000-0000-0000-000000000005', 'LEGO', 4.9, 22, '2024-04-21 09:00:00', '2024-04-21 09:00:00'),

-- ===================== SUPERMARKET (using root category) =====================
('f0000001-0000-0000-0000-000000000052', 'c0000001-0000-0000-0000-000000000003', 'Nescafe Gold 200gr Kavanoz', 'Nescafe Gold 200gr Cam Kavanoz. Ozel kavrulmus cekirdekler.', 249.00, 219.00, 500, 'GROC-001', 'ACTIVE', 'a0000001-0000-0000-0000-000000000010', 'Nescafe', 4.3, 167, '2024-04-22 08:00:00', '2024-04-22 08:00:00'),
('f0000001-0000-0000-0000-000000000053', 'c0000001-0000-0000-0000-000000000003', 'Ulker Cikolata Paketi 6li', 'Ulker Cikolata Karisik Paket 6li. Sutlu, bitter, findikli cesitler.', 89.90, 69.90, 400, 'GROC-002', 'ACTIVE', 'a0000001-0000-0000-0000-000000000010', 'Ulker', 4.5, 98, '2024-04-23 09:00:00', '2024-04-23 09:00:00'),

-- ===================== INACTIVE / EDGE CASES =====================
('f0000001-0000-0000-0000-000000000054', 'c0000001-0000-0000-0000-000000000004', 'Mavi Erkek Vintage Deri Ceket', 'Mavi Erkek Vintage Yikamali Suni Deri Ceket. Fermuarli, siyah.', 2499.00, NULL, 0, 'FASH-018', 'OUT_OF_STOCK', 'b0000002-0000-0000-0000-000000000002', 'Mavi', 4.4, 56, '2024-02-01 10:00:00', '2024-05-01 10:00:00'),
('f0000001-0000-0000-0000-000000000055', 'c0000001-0000-0000-0000-000000000005', 'Arzum Cay Makinesi Eski Model', 'Arzum Cayci Klasik AR3003 Cay Makinesi. Paslanmaz celik, eski model.', 1299.00, 899.00, 5, 'HOME-012', 'INACTIVE', 'b0000003-0000-0000-0000-000000000003', 'Arzum', 3.2, 15, '2024-01-10 10:00:00', '2024-04-01 10:00:00');


-- ===================== PRODUCT IMAGES =====================
-- Main images for all products
INSERT INTO product_images (id, product_id, url, alt_text, sort_order, is_main, created_at, updated_at) VALUES
-- Electronics - Phones
('10000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'https://picsum.photos/seed/samsung-s24-ultra/400/400', 'Samsung Galaxy S24 Ultra', 0, true, '2024-03-01 10:00:00', '2024-03-01 10:00:00'),
('10000001-0000-0000-0000-000000000002', 'f0000001-0000-0000-0000-000000000001', 'https://picsum.photos/seed/samsung-s24-ultra-back/400/400', 'Samsung Galaxy S24 Ultra Arka', 1, false, '2024-03-01 10:00:00', '2024-03-01 10:00:00'),
('10000001-0000-0000-0000-000000000003', 'f0000001-0000-0000-0000-000000000002', 'https://picsum.photos/seed/iphone-15-pro-max/400/400', 'iPhone 15 Pro Max', 0, true, '2024-03-02 11:00:00', '2024-03-02 11:00:00'),
('10000001-0000-0000-0000-000000000004', 'f0000001-0000-0000-0000-000000000003', 'https://picsum.photos/seed/xiaomi-redmi-note-13/400/400', 'Xiaomi Redmi Note 13 Pro', 0, true, '2024-03-03 09:00:00', '2024-03-03 09:00:00'),
('10000001-0000-0000-0000-000000000005', 'f0000001-0000-0000-0000-000000000004', 'https://picsum.photos/seed/samsung-a55/400/400', 'Samsung Galaxy A55', 0, true, '2024-03-05 10:00:00', '2024-03-05 10:00:00'),
-- Electronics - Computers
('10000001-0000-0000-0000-000000000006', 'f0000001-0000-0000-0000-000000000005', 'https://picsum.photos/seed/macbook-air-m3/400/400', 'Apple MacBook Air M3', 0, true, '2024-03-06 11:30:00', '2024-03-06 11:30:00'),
('10000001-0000-0000-0000-000000000007', 'f0000001-0000-0000-0000-000000000006', 'https://picsum.photos/seed/lenovo-ideapad-slim3/400/400', 'Lenovo IdeaPad Slim 3', 0, true, '2024-03-07 08:00:00', '2024-03-07 08:00:00'),
('10000001-0000-0000-0000-000000000008', 'f0000001-0000-0000-0000-000000000007', 'https://picsum.photos/seed/monster-abra-a5/400/400', 'Monster Abra A5', 0, true, '2024-03-08 14:00:00', '2024-03-08 14:00:00'),
-- Electronics - TVs
('10000001-0000-0000-0000-000000000009', 'f0000001-0000-0000-0000-000000000008', 'https://picsum.photos/seed/vestel-55-4k/400/400', 'Vestel 55 inc 4K Smart TV', 0, true, '2024-03-09 10:00:00', '2024-03-09 10:00:00'),
('10000001-0000-0000-0000-000000000010', 'f0000001-0000-0000-0000-000000000009', 'https://picsum.photos/seed/samsung-65-qled/400/400', 'Samsung 65 inc QLED 4K TV', 0, true, '2024-03-10 09:00:00', '2024-03-10 09:00:00'),
('10000001-0000-0000-0000-000000000011', 'f0000001-0000-0000-0000-000000000010', 'https://picsum.photos/seed/arcelik-50-4k/400/400', 'Arcelik 50 inc 4K Smart TV', 0, true, '2024-03-11 11:00:00', '2024-03-11 11:00:00'),
-- Electronics - Headphones
('10000001-0000-0000-0000-000000000012', 'f0000001-0000-0000-0000-000000000011', 'https://picsum.photos/seed/airpods-pro-2/400/400', 'Apple AirPods Pro 2', 0, true, '2024-03-12 10:00:00', '2024-03-12 10:00:00'),
('10000001-0000-0000-0000-000000000013', 'f0000001-0000-0000-0000-000000000012', 'https://picsum.photos/seed/jbl-tune-520bt/400/400', 'JBL Tune 520BT', 0, true, '2024-03-13 08:30:00', '2024-03-13 08:30:00'),
('10000001-0000-0000-0000-000000000014', 'f0000001-0000-0000-0000-000000000013', 'https://picsum.photos/seed/sony-wh1000xm5/400/400', 'Sony WH-1000XM5', 0, true, '2024-03-14 12:00:00', '2024-03-14 12:00:00'),
-- Fashion - Women
('10000001-0000-0000-0000-000000000015', 'f0000001-0000-0000-0000-000000000014', 'https://picsum.photos/seed/koton-kadin-gomlek/400/400', 'Koton Kadin Gomlek', 0, true, '2024-03-15 09:00:00', '2024-03-15 09:00:00'),
('10000001-0000-0000-0000-000000000016', 'f0000001-0000-0000-0000-000000000015', 'https://picsum.photos/seed/defacto-kadin-tisort/400/400', 'Defacto Kadin Tisort', 0, true, '2024-03-15 09:30:00', '2024-03-15 09:30:00'),
('10000001-0000-0000-0000-000000000017', 'f0000001-0000-0000-0000-000000000016', 'https://picsum.photos/seed/lcw-kadin-elbise/400/400', 'LC Waikiki Kadin Elbise', 0, true, '2024-03-16 10:00:00', '2024-03-16 10:00:00'),
('10000001-0000-0000-0000-000000000018', 'f0000001-0000-0000-0000-000000000017', 'https://picsum.photos/seed/mavi-kadin-jean/400/400', 'Mavi Kadin Jean', 0, true, '2024-03-17 11:00:00', '2024-03-17 11:00:00'),
('10000001-0000-0000-0000-000000000019', 'f0000001-0000-0000-0000-000000000018', 'https://picsum.photos/seed/koton-kadin-hirka/400/400', 'Koton Kadin Hirka', 0, true, '2024-03-18 09:00:00', '2024-03-18 09:00:00'),
-- Fashion - Men
('10000001-0000-0000-0000-000000000020', 'f0000001-0000-0000-0000-000000000019', 'https://picsum.photos/seed/defacto-erkek-polo/400/400', 'Defacto Erkek Polo Tisort', 0, true, '2024-03-19 10:00:00', '2024-03-19 10:00:00'),
('10000001-0000-0000-0000-000000000021', 'f0000001-0000-0000-0000-000000000020', 'https://picsum.photos/seed/mavi-erkek-jean/400/400', 'Mavi Erkek Jean', 0, true, '2024-03-20 11:00:00', '2024-03-20 11:00:00'),
('10000001-0000-0000-0000-000000000022', 'f0000001-0000-0000-0000-000000000021', 'https://picsum.photos/seed/lcw-erkek-sweatshirt/400/400', 'LC Waikiki Erkek Sweatshirt', 0, true, '2024-03-21 08:00:00', '2024-03-21 08:00:00'),
('10000001-0000-0000-0000-000000000023', 'f0000001-0000-0000-0000-000000000022', 'https://picsum.photos/seed/koton-erkek-gomlek/400/400', 'Koton Erkek Gomlek', 0, true, '2024-03-22 09:00:00', '2024-03-22 09:00:00'),
('10000001-0000-0000-0000-000000000024', 'f0000001-0000-0000-0000-000000000023', 'https://picsum.photos/seed/defacto-erkek-kargo/400/400', 'Defacto Erkek Kargo Pantolon', 0, true, '2024-03-23 10:00:00', '2024-03-23 10:00:00'),
-- Fashion - Shoes
('10000001-0000-0000-0000-000000000025', 'f0000001-0000-0000-0000-000000000024', 'https://picsum.photos/seed/nike-air-force-1/400/400', 'Nike Air Force 1', 0, true, '2024-03-24 10:00:00', '2024-03-24 10:00:00'),
('10000001-0000-0000-0000-000000000026', 'f0000001-0000-0000-0000-000000000025', 'https://picsum.photos/seed/adidas-superstar/400/400', 'Adidas Superstar', 0, true, '2024-03-25 11:00:00', '2024-03-25 11:00:00'),
('10000001-0000-0000-0000-000000000027', 'f0000001-0000-0000-0000-000000000026', 'https://picsum.photos/seed/newbalance-574/400/400', 'New Balance 574', 0, true, '2024-03-26 09:00:00', '2024-03-26 09:00:00'),
('10000001-0000-0000-0000-000000000028', 'f0000001-0000-0000-0000-000000000027', 'https://picsum.photos/seed/lcw-erkek-klasik-ayakkabi/400/400', 'LC Waikiki Klasik Ayakkabi', 0, true, '2024-03-27 10:00:00', '2024-03-27 10:00:00'),
-- Fashion - Bags
('10000001-0000-0000-0000-000000000029', 'f0000001-0000-0000-0000-000000000028', 'https://picsum.photos/seed/koton-omuz-cantasi/400/400', 'Koton Omuz Cantasi', 0, true, '2024-03-28 09:00:00', '2024-03-28 09:00:00'),
('10000001-0000-0000-0000-000000000030', 'f0000001-0000-0000-0000-000000000029', 'https://picsum.photos/seed/defacto-sirt-cantasi/400/400', 'Defacto Sirt Cantasi', 0, true, '2024-03-29 10:00:00', '2024-03-29 10:00:00'),
('10000001-0000-0000-0000-000000000031', 'f0000001-0000-0000-0000-000000000030', 'https://picsum.photos/seed/mavi-tote-canta/400/400', 'Mavi Tote Canta', 0, true, '2024-03-30 11:00:00', '2024-03-30 11:00:00'),
-- Home - Furniture
('10000001-0000-0000-0000-000000000032', 'f0000001-0000-0000-0000-000000000031', 'https://picsum.photos/seed/bellona-kose-koltuk/400/400', 'Bellona Kose Koltuk', 0, true, '2024-04-01 10:00:00', '2024-04-01 10:00:00'),
('10000001-0000-0000-0000-000000000033', 'f0000001-0000-0000-0000-000000000032', 'https://picsum.photos/seed/dogtas-yatak-odasi/400/400', 'Dogtas Yatak Odasi Takimi', 0, true, '2024-04-02 09:00:00', '2024-04-02 09:00:00'),
('10000001-0000-0000-0000-000000000034', 'f0000001-0000-0000-0000-000000000033', 'https://picsum.photos/seed/ikea-kallax/400/400', 'IKEA Kallax Kitaplik', 0, true, '2024-04-03 11:00:00', '2024-04-03 11:00:00'),
('10000001-0000-0000-0000-000000000035', 'f0000001-0000-0000-0000-000000000034', 'https://picsum.photos/seed/cilek-calisma-masasi/400/400', 'Cilek Calisma Masasi', 0, true, '2024-04-04 10:00:00', '2024-04-04 10:00:00'),
-- Home - Decoration
('10000001-0000-0000-0000-000000000036', 'f0000001-0000-0000-0000-000000000035', 'https://picsum.photos/seed/madamecoco-yastik/400/400', 'Madame Coco Yastik Kilifi', 0, true, '2024-04-05 09:00:00', '2024-04-05 09:00:00'),
('10000001-0000-0000-0000-000000000037', 'f0000001-0000-0000-0000-000000000036', 'https://picsum.photos/seed/englishhome-nevresim/400/400', 'English Home Nevresim Takimi', 0, true, '2024-04-06 10:00:00', '2024-04-06 10:00:00'),
('10000001-0000-0000-0000-000000000038', 'f0000001-0000-0000-0000-000000000037', 'https://picsum.photos/seed/tac-hali/400/400', 'Tac Hali', 0, true, '2024-04-07 11:00:00', '2024-04-07 11:00:00'),
-- Home - Kitchen
('10000001-0000-0000-0000-000000000039', 'f0000001-0000-0000-0000-000000000038', 'https://picsum.photos/seed/arzum-okka/400/400', 'Arzum Okka Turk Kahvesi', 0, true, '2024-04-08 09:00:00', '2024-04-08 09:00:00'),
('10000001-0000-0000-0000-000000000040', 'f0000001-0000-0000-0000-000000000039', 'https://picsum.photos/seed/korkmaz-tencere/400/400', 'Korkmaz Basincli Tencere', 0, true, '2024-04-09 10:00:00', '2024-04-09 10:00:00'),
('10000001-0000-0000-0000-000000000041', 'f0000001-0000-0000-0000-000000000040', 'https://picsum.photos/seed/emsan-tava-seti/400/400', 'Emsan Granit Tava Seti', 0, true, '2024-04-10 11:00:00', '2024-04-10 11:00:00'),
('10000001-0000-0000-0000-000000000042', 'f0000001-0000-0000-0000-000000000041', 'https://picsum.photos/seed/beko-kahve-makinesi/400/400', 'Beko Filtre Kahve Makinesi', 0, true, '2024-04-11 08:00:00', '2024-04-11 08:00:00'),
-- Sports
('10000001-0000-0000-0000-000000000043', 'f0000001-0000-0000-0000-000000000042', 'https://picsum.photos/seed/nike-esofman/400/400', 'Nike Esofman Takimi', 0, true, '2024-04-12 09:00:00', '2024-04-12 09:00:00'),
('10000001-0000-0000-0000-000000000044', 'f0000001-0000-0000-0000-000000000043', 'https://picsum.photos/seed/adidas-ultraboost/400/400', 'Adidas Ultraboost Light', 0, true, '2024-04-13 10:00:00', '2024-04-13 10:00:00'),
('10000001-0000-0000-0000-000000000045', 'f0000001-0000-0000-0000-000000000044', 'https://picsum.photos/seed/decathlon-cadir/400/400', 'Decathlon Kamp Cadiri', 0, true, '2024-04-14 11:00:00', '2024-04-14 11:00:00'),
('10000001-0000-0000-0000-000000000046', 'f0000001-0000-0000-0000-000000000045', 'https://picsum.photos/seed/underarmour-tisort/400/400', 'Under Armour Spor Tisort', 0, true, '2024-04-15 09:00:00', '2024-04-15 09:00:00'),
-- Cosmetics
('10000001-0000-0000-0000-000000000047', 'f0000001-0000-0000-0000-000000000046', 'https://picsum.photos/seed/mac-ruby-woo/400/400', 'MAC Ruby Woo Ruj', 0, true, '2024-04-16 10:00:00', '2024-04-16 10:00:00'),
('10000001-0000-0000-0000-000000000048', 'f0000001-0000-0000-0000-000000000047', 'https://picsum.photos/seed/loreal-revitalift/400/400', 'L''Oreal Revitalift Serum', 0, true, '2024-04-17 11:00:00', '2024-04-17 11:00:00'),
('10000001-0000-0000-0000-000000000049', 'f0000001-0000-0000-0000-000000000048', 'https://picsum.photos/seed/rayban-wayfarer/400/400', 'Ray-Ban Wayfarer', 0, true, '2024-04-18 09:00:00', '2024-04-18 09:00:00'),
-- Books & Hobby
('10000001-0000-0000-0000-000000000050', 'f0000001-0000-0000-0000-000000000049', 'https://picsum.photos/seed/sapiens-kitap/400/400', 'Sapiens Kitap', 0, true, '2024-04-19 10:00:00', '2024-04-19 10:00:00'),
('10000001-0000-0000-0000-000000000051', 'f0000001-0000-0000-0000-000000000050', 'https://picsum.photos/seed/atomic-habits/400/400', 'Atomic Habits Kitap', 0, true, '2024-04-20 11:00:00', '2024-04-20 11:00:00'),
('10000001-0000-0000-0000-000000000052', 'f0000001-0000-0000-0000-000000000051', 'https://picsum.photos/seed/lego-ferrari/400/400', 'LEGO Ferrari Daytona', 0, true, '2024-04-21 09:00:00', '2024-04-21 09:00:00'),
-- Supermarket
('10000001-0000-0000-0000-000000000053', 'f0000001-0000-0000-0000-000000000052', 'https://picsum.photos/seed/nescafe-gold/400/400', 'Nescafe Gold', 0, true, '2024-04-22 08:00:00', '2024-04-22 08:00:00'),
('10000001-0000-0000-0000-000000000054', 'f0000001-0000-0000-0000-000000000053', 'https://picsum.photos/seed/ulker-cikolata/400/400', 'Ulker Cikolata Paketi', 0, true, '2024-04-23 09:00:00', '2024-04-23 09:00:00'),
-- Inactive products
('10000001-0000-0000-0000-000000000055', 'f0000001-0000-0000-0000-000000000054', 'https://picsum.photos/seed/mavi-deri-ceket/400/400', 'Mavi Deri Ceket', 0, true, '2024-02-01 10:00:00', '2024-02-01 10:00:00'),
('10000001-0000-0000-0000-000000000056', 'f0000001-0000-0000-0000-000000000055', 'https://picsum.photos/seed/arzum-cay-makinesi/400/400', 'Arzum Cay Makinesi', 0, true, '2024-01-10 10:00:00', '2024-01-10 10:00:00');


-- ===================== REVIEWS =====================
-- Buyer UUIDs: d0000001-0000-0000-0000-00000000000X (1-10)

INSERT INTO reviews (id, product_id, user_id, rating, comment, status, created_at, updated_at) VALUES
-- Samsung Galaxy S24 Ultra reviews
('20000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000001', 5, 'Harika bir telefon! Kamera kalitesi inanilmaz, S Pen cok kullanisli. Kesinlikle tavsiye ederim.', 'APPROVED', '2024-04-01 14:00:00', '2024-04-01 14:00:00'),
('20000001-0000-0000-0000-000000000002', 'f0000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000002', 4, 'Guzel telefon ama fiyati biraz fazla. Batarya omru bekledigimden iyi.', 'APPROVED', '2024-04-05 16:30:00', '2024-04-05 16:30:00'),
('20000001-0000-0000-0000-000000000003', 'f0000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000003', 5, 'Uzun suredir Samsung kullaniyorum, bu model en iyisi. Ekran muhteesem.', 'APPROVED', '2024-04-10 11:00:00', '2024-04-10 11:00:00'),

-- iPhone 15 Pro Max reviews
('20000001-0000-0000-0000-000000000004', 'f0000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000004', 5, 'Apple kalitesi tartisilamaz. USB-C gecis cok iyi olmus. Kamera profesyonel seviyede.', 'APPROVED', '2024-04-03 10:00:00', '2024-04-03 10:00:00'),
('20000001-0000-0000-0000-000000000005', 'f0000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000005', 5, 'En iyi iPhone deneyimi. Action button cok pratik. Video cekim kalitesi muhtesem.', 'APPROVED', '2024-04-08 15:00:00', '2024-04-08 15:00:00'),
('20000001-0000-0000-0000-000000000006', 'f0000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000006', 4, 'Cok iyi telefon ama fiyat cok yuksek. Titanyum kasa cok hafif ve saglan.', 'APPROVED', '2024-04-12 09:00:00', '2024-04-12 09:00:00'),

-- Mavi Jean reviews
('20000001-0000-0000-0000-000000000007', 'f0000001-0000-0000-0000-000000000020', 'd0000001-0000-0000-0000-000000000007', 5, 'Mavi denilince akla gelen kalite. Fit muhtesem, kumas kalitesi cok iyi.', 'APPROVED', '2024-04-15 14:00:00', '2024-04-15 14:00:00'),
('20000001-0000-0000-0000-000000000008', 'f0000001-0000-0000-0000-000000000020', 'd0000001-0000-0000-0000-000000000008', 4, 'Guzel jean ama beden tablosu biraz farkli. Bir beden buyuk almani oneririm.', 'APPROVED', '2024-04-18 10:00:00', '2024-04-18 10:00:00'),

-- Nike Air Force 1 reviews
('20000001-0000-0000-0000-000000000009', 'f0000001-0000-0000-0000-000000000024', 'd0000001-0000-0000-0000-000000000009', 5, 'Klasik ayakkabi, her kiyafetle uyumlu. Kalitesi harika.', 'APPROVED', '2024-04-20 11:30:00', '2024-04-20 11:30:00'),
('20000001-0000-0000-0000-000000000010', 'f0000001-0000-0000-0000-000000000024', 'd0000001-0000-0000-0000-000000000010', 4, 'Cok rahat, cok sik. Sadece temizlemesi zor beyaz renk.', 'APPROVED', '2024-04-22 16:00:00', '2024-04-22 16:00:00'),

-- Arzum Okka reviews
('20000001-0000-0000-0000-000000000011', 'f0000001-0000-0000-0000-000000000038', 'd0000001-0000-0000-0000-000000000001', 5, 'Turk kahvesi icin en iyi makine! Kopugu muhtesem, temizlemesi kolay.', 'APPROVED', '2024-04-25 09:00:00', '2024-04-25 09:00:00'),
('20000001-0000-0000-0000-000000000012', 'f0000001-0000-0000-0000-000000000038', 'd0000001-0000-0000-0000-000000000003', 4, 'Kahve yapimi cok pratik. Tek sorun biraz gurultulu olmasi.', 'APPROVED', '2024-04-28 14:00:00', '2024-04-28 14:00:00'),

-- Sapiens book reviews
('20000001-0000-0000-0000-000000000013', 'f0000001-0000-0000-0000-000000000049', 'd0000001-0000-0000-0000-000000000006', 5, 'Hayatima yon veren kitaplardan biri. Herkesin okumasi gereken bir eser.', 'APPROVED', '2024-05-01 10:00:00', '2024-05-01 10:00:00'),
('20000001-0000-0000-0000-000000000014', 'f0000001-0000-0000-0000-000000000049', 'd0000001-0000-0000-0000-000000000007', 5, 'Muhteesem bir kitap. Insanlik tarihine bakis acinizi degistirecek.', 'APPROVED', '2024-05-03 11:00:00', '2024-05-03 11:00:00'),

-- Emsan Tava reviews
('20000001-0000-0000-0000-000000000015', 'f0000001-0000-0000-0000-000000000040', 'd0000001-0000-0000-0000-000000000002', 4, 'Yapismaz ozelligi gercekten cok iyi. Fiyat/performans olarak basarili.', 'APPROVED', '2024-05-05 15:00:00', '2024-05-05 15:00:00'),
('20000001-0000-0000-0000-000000000016', 'f0000001-0000-0000-0000-000000000040', 'd0000001-0000-0000-0000-000000000004', 5, 'Harika tava seti! 6 aydir kullaniyorum, hala ilk gunku gibi.', 'APPROVED', '2024-05-08 10:00:00', '2024-05-08 10:00:00'),

-- MacBook Air M3 reviews
('20000001-0000-0000-0000-000000000017', 'f0000001-0000-0000-0000-000000000005', 'd0000001-0000-0000-0000-000000000005', 5, 'M3 cip inanilmaz hizli. Fansiz tasarim sessiz calisma imkani sunuyor. Ekran kalitesi muthis.', 'APPROVED', '2024-05-10 12:00:00', '2024-05-10 12:00:00'),
('20000001-0000-0000-0000-000000000018', 'f0000001-0000-0000-0000-000000000005', 'd0000001-0000-0000-0000-000000000009', 5, 'Hem is hem kisisel kullanim icin mukemmel. Batarya tum gun yetiyor.', 'APPROVED', '2024-05-12 14:00:00', '2024-05-12 14:00:00'),

-- Pending reviews
('20000001-0000-0000-0000-000000000019', 'f0000001-0000-0000-0000-000000000012', 'd0000001-0000-0000-0000-000000000010', 3, 'Fiyatina gore idare eder. Ses kalitesi orta seviyede.', 'PENDING', '2024-05-15 09:00:00', '2024-05-15 09:00:00'),
('20000001-0000-0000-0000-000000000020', 'f0000001-0000-0000-0000-000000000035', 'd0000001-0000-0000-0000-000000000008', 4, 'Yastik kiliflari cok sik gorunuyor. Kumas kalitesi iyi.', 'PENDING', '2024-05-16 10:00:00', '2024-05-16 10:00:00');

COMMIT;
