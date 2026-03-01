-- Root categories
INSERT INTO categories (id, name, slug, parent_id, level, is_active) VALUES
('a0000001-0000-0000-0000-000000000001', 'Elektronik', 'elektronik', NULL, 0, true),
('a0000001-0000-0000-0000-000000000002', 'Moda', 'moda', NULL, 0, true),
('a0000001-0000-0000-0000-000000000003', 'Ev & Yasam', 'ev-yasam', NULL, 0, true),
('a0000001-0000-0000-0000-000000000004', 'Spor & Outdoor', 'spor-outdoor', NULL, 0, true),
('a0000001-0000-0000-0000-000000000005', 'Kitap & Hobi', 'kitap-hobi', NULL, 0, true),
('a0000001-0000-0000-0000-000000000006', 'Kozmetik', 'kozmetik', NULL, 0, true),
('a0000001-0000-0000-0000-000000000007', 'Bebek & Oyuncak', 'bebek-oyuncak', NULL, 0, true),
('a0000001-0000-0000-0000-000000000008', 'Gida & Icecek', 'gida-icecek', NULL, 0, true),
('a0000001-0000-0000-0000-000000000009', 'Oto & Bahce', 'oto-bahce', NULL, 0, true),
('a0000001-0000-0000-0000-000000000010', 'Supermarket', 'supermarket', NULL, 0, true);

-- Subcategories for Elektronik
INSERT INTO categories (id, name, slug, parent_id, level, is_active) VALUES
('b0000001-0000-0000-0000-000000000001', 'Cep Telefonu', 'cep-telefonu', 'a0000001-0000-0000-0000-000000000001', 1, true),
('b0000001-0000-0000-0000-000000000002', 'Bilgisayar', 'bilgisayar', 'a0000001-0000-0000-0000-000000000001', 1, true),
('b0000001-0000-0000-0000-000000000003', 'Televizyon', 'televizyon', 'a0000001-0000-0000-0000-000000000001', 1, true),
('b0000001-0000-0000-0000-000000000004', 'Kulaklik', 'kulaklik', 'a0000001-0000-0000-0000-000000000001', 1, true);

-- Subcategories for Moda
INSERT INTO categories (id, name, slug, parent_id, level, is_active) VALUES
('b0000002-0000-0000-0000-000000000001', 'Kadin Giyim', 'kadin-giyim', 'a0000001-0000-0000-0000-000000000002', 1, true),
('b0000002-0000-0000-0000-000000000002', 'Erkek Giyim', 'erkek-giyim', 'a0000001-0000-0000-0000-000000000002', 1, true),
('b0000002-0000-0000-0000-000000000003', 'Ayakkabi', 'ayakkabi', 'a0000001-0000-0000-0000-000000000002', 1, true),
('b0000002-0000-0000-0000-000000000004', 'Canta', 'canta', 'a0000001-0000-0000-0000-000000000002', 1, true);

-- Subcategories for Ev & Yasam
INSERT INTO categories (id, name, slug, parent_id, level, is_active) VALUES
('b0000003-0000-0000-0000-000000000001', 'Mobilya', 'mobilya', 'a0000001-0000-0000-0000-000000000003', 1, true),
('b0000003-0000-0000-0000-000000000002', 'Dekorasyon', 'dekorasyon', 'a0000001-0000-0000-0000-000000000003', 1, true),
('b0000003-0000-0000-0000-000000000003', 'Mutfak', 'mutfak', 'a0000001-0000-0000-0000-000000000003', 1, true);
