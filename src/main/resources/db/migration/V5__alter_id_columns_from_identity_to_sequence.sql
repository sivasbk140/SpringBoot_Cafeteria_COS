-- Create sequences for all tables
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE delivery_detail_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE food_menu_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE food_item_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE food_menu_item_map_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE order_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE order_item_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE order_delivery_map_seq START WITH 1 INCREMENT BY 1;

-- Drop identity from columns and set default to use sequences
ALTER TABLE "users" ALTER COLUMN id DROP IDENTITY;
ALTER TABLE "users" ALTER COLUMN id SET DEFAULT nextval('user_seq');

ALTER TABLE delivery_details ALTER COLUMN id DROP IDENTITY;
ALTER TABLE delivery_details ALTER COLUMN id SET DEFAULT nextval('delivery_detail_seq');

ALTER TABLE food_menu ALTER COLUMN id DROP IDENTITY;
ALTER TABLE food_menu ALTER COLUMN id SET DEFAULT nextval('food_menu_seq');

ALTER TABLE food_item ALTER COLUMN id DROP IDENTITY;
ALTER TABLE food_item ALTER COLUMN id SET DEFAULT nextval('food_item_seq');

ALTER TABLE food_menu_items_map ALTER COLUMN id DROP IDENTITY;
ALTER TABLE food_menu_items_map ALTER COLUMN id SET DEFAULT nextval('food_menu_item_map_seq');

ALTER TABLE order_table ALTER COLUMN id DROP IDENTITY;
ALTER TABLE order_table ALTER COLUMN id SET DEFAULT nextval('order_seq');

ALTER TABLE order_items ALTER COLUMN id DROP IDENTITY;
ALTER TABLE order_items ALTER COLUMN id SET DEFAULT nextval('order_item_seq');

ALTER TABLE order_delivery_map ALTER COLUMN id DROP IDENTITY;
ALTER TABLE order_delivery_map ALTER COLUMN id SET DEFAULT nextval('order_delivery_map_seq');

-- Sync sequences with existing data so new IDs continue after the current max
SELECT setval('user_seq', COALESCE((SELECT MAX(id) FROM "users"), 0) + 1, false);
SELECT setval('delivery_detail_seq', COALESCE((SELECT MAX(id) FROM delivery_details), 0) + 1, false);
SELECT setval('food_menu_seq', COALESCE((SELECT MAX(id) FROM food_menu), 0) + 1, false);
SELECT setval('food_item_seq', COALESCE((SELECT MAX(id) FROM food_item), 0) + 1, false);
SELECT setval('food_menu_item_map_seq', COALESCE((SELECT MAX(id) FROM food_menu_items_map), 0) + 1, false);
SELECT setval('order_seq', COALESCE((SELECT MAX(id) FROM order_table), 0) + 1, false);
SELECT setval('order_item_seq', COALESCE((SELECT MAX(id) FROM order_items), 0) + 1, false);
SELECT setval('order_delivery_map_seq', COALESCE((SELECT MAX(id) FROM order_delivery_map), 0) + 1, false);
