-- Add category column to products table
ALTER TABLE products 
ADD COLUMN IF NOT EXISTS category VARCHAR(50);

-- Add index for better query performance
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);

-- Optional: Update existing products with default category
-- UPDATE products SET category = 'Khác' WHERE category IS NULL;
