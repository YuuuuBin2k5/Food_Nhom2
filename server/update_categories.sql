-- ===================================
-- SQL Script: Update Product Categories
-- ===================================
-- Chạy script này để gán category cho products hiện có

-- 1. RAU CỦ QUẢ (VEGETABLES)
UPDATE products 
SET category = 'VEGETABLES'
WHERE LOWER(name) LIKE '%rau%' 
   OR LOWER(name) LIKE '%củ%'
   OR LOWER(name) LIKE '%cà chua%'
   OR LOWER(name) LIKE '%khoai%'
   OR LOWER(name) LIKE '%bắp cải%'
   OR LOWER(name) LIKE '%cải%'
   OR LOWER(name) LIKE '%su hào%'
   OR LOWER(name) LIKE '%đậu%'
   OR LOWER(name) LIKE '%bí%';

-- 2. TRÁI CÂY (FRUITS)
UPDATE products 
SET category = 'FRUITS'
WHERE LOWER(name) LIKE '%táo%'
   OR LOWER(name) LIKE '%cam%'
   OR LOWER(name) LIKE '%chuối%'
   OR LOWER(name) LIKE '%xoài%'
   OR LOWER(name) LIKE '%dưa%'
   OR LOWER(name) LIKE '%nho%'
   OR LOWER(name) LIKE '%dứa%'
   OR LOWER(name) LIKE '%ổi%'
   OR LOWER(name) LIKE '%mận%'
   OR LOWER(name) LIKE '%thanh long%'
   OR LOWER(name) LIKE '%sầu riêng%'
   OR LOWER(name) LIKE '%măng cụt%';

-- 3. THỊT TƯƠI (MEAT)
UPDATE products 
SET category = 'MEAT'
WHERE LOWER(name) LIKE '%thịt%'
   OR LOWER(name) LIKE '%heo%'
   OR LOWER(name) LIKE '%lợn%'
   OR LOWER(name) LIKE '%gà%'
   OR LOWER(name) LIKE '%bò%'
   OR LOWER(name) LIKE '%vịt%'
   OR LOWER(name) LIKE '%ngan%';

-- 4. HẢI SẢN (SEAFOOD)
UPDATE products 
SET category = 'SEAFOOD'
WHERE LOWER(name) LIKE '%cá%'
   OR LOWER(name) LIKE '%tôm%'
   OR LOWER(name) LIKE '%mực%'
   OR LOWER(name) LIKE '%ghẹ%'
   OR LOWER(name) LIKE '%cua%'
   OR LOWER(name) LIKE '%ngao%'
   OR LOWER(name) LIKE '%hàu%'
   OR LOWER(name) LIKE '%sò%';

-- 5. SỮA & PHÔ MAI (DAIRY)
UPDATE products 
SET category = 'DAIRY'
WHERE LOWER(name) LIKE '%sữa%'
   OR LOWER(name) LIKE '%pho mai%'
   OR LOWER(name) LIKE '%yogurt%'
   OR LOWER(name) LIKE '%yaourt%'
   OR LOWER(name) LIKE '%bơ sữa%';

-- 6. BÁNH MÌ & BÁNH NGỌT (BAKERY)
UPDATE products 
SET category = 'BAKERY'
WHERE LOWER(name) LIKE '%bánh mì%'
   OR LOWER(name) LIKE '%bánh ngọt%'
   OR LOWER(name) LIKE '%bánh bông lan%'
   OR LOWER(name) LIKE '%bánh quy%'
   OR LOWER(name) LIKE '%croissant%';

-- 7. SNACK & ĐỒ ĂN VẶT (SNACKS)
UPDATE products 
SET category = 'SNACKS'
WHERE LOWER(name) LIKE '%snack%'
   OR LOWER(name) LIKE '%kẹo%'
   OR LOWER(name) LIKE '%socola%'
   OR LOWER(name) LIKE '%chocolate%'
   OR LOWER(name) LIKE '%bim bim%'
   OR LOWER(name) LIKE '%mứt%';

-- 8. ĐỒ UỐNG (BEVERAGES)
UPDATE products 
SET category = 'BEVERAGES'
WHERE LOWER(name) LIKE '%nước%'
   OR LOWER(name) LIKE '%trà%'
   OR LOWER(name) LIKE '%cà phê%'
   OR LOWER(name) LIKE '%coffee%'
   OR LOWER(name) LIKE '%coca%'
   OR LOWER(name) LIKE '%pepsi%'
   OR LOWER(name) LIKE '%sting%';

-- 9. THỰC PHẨM ĐÔNG LẠNH (FROZEN)
UPDATE products 
SET category = 'FROZEN'
WHERE LOWER(name) LIKE '%đông lạnh%'
   OR LOWER(name) LIKE '%frozen%'
   OR LOWER(name) LIKE '%kem%';

-- 10. Set OTHER for any products without category
UPDATE products 
SET category = 'OTHER'
WHERE category IS NULL;

-- Verify results
SELECT category, COUNT(*) as product_count
FROM products
WHERE status = 'ACTIVE'
GROUP BY category
ORDER BY category;
