-- Remove old seed data that used the availability_map structure
DELETE FROM food_menu_items_map;
DELETE FROM food_menu;

-- Drop the availability_map table (no longer needed)
DROP TABLE IF EXISTS availability_map;

-- Add menu_day column to food_menu
ALTER TABLE food_menu ADD COLUMN menu_day VARCHAR(20) NOT NULL DEFAULT 'MONDAY';

-- Add unique constraint on category + menu_day
ALTER TABLE food_menu ADD CONSTRAINT uq_food_menu_category_day UNIQUE (category, menu_day);

-- Remove the default now that the column exists
ALTER TABLE food_menu ALTER COLUMN menu_day DROP DEFAULT;
