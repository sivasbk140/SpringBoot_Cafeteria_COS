INSERT INTO users (name, password, role, email, created_on, updated_on)
VALUES ('Siam', 'pass123', 'CUSTOMER', 'siam@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (name, password, role, email, created_on, updated_on)
VALUES ('Siva', 'pass123', 'CUSTOMER', 'siva@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO food_menu (category, menu_day, created_on, updated_on)
VALUES ('Breakfast', 'MONDAY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Lunch',     'MONDAY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Dinner',    'MONDAY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO food_item (name, price, quantity, category, description, created_on, updated_on)
VALUES ('Idli',            30.00,  20, 'Veg',     'Steamed rice cakes',         CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Chicken Biryani', 150.00, 10, 'Non-Veg', 'Spicy biryani',              CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Chapathi',        50.00,  15, 'Veg',     'Wheat flatbread with curry', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO food_menu_items_map (menu_id, food_item_id, is_available, created_on, updated_on)
VALUES (
           (SELECT id FROM food_menu WHERE category = 'Breakfast' AND menu_day = 'MONDAY'),
           (SELECT id FROM food_item WHERE name = 'Idli'),
           TRUE,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       ),
       (
           (SELECT id FROM food_menu WHERE category = 'Lunch' AND menu_day = 'MONDAY'),
           (SELECT id FROM food_item WHERE name = 'Chicken Biryani'),
           TRUE,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       ),
       (
           (SELECT id FROM food_menu WHERE category = 'Dinner' AND menu_day = 'MONDAY'),
           (SELECT id FROM food_item WHERE name = 'Chapathi'),
           TRUE,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO order_table (user_id, status, created_on, updated_on)
VALUES (
           (SELECT id FROM users WHERE email = 'siam@gmail.com'),
           'PLACED_ORDER',
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );


INSERT INTO order_items (order_id, food_item_id, price, quantity, created_on, updated_on)
VALUES (
           (SELECT id FROM order_table ORDER BY id DESC LIMIT 1),
       (SELECT id FROM food_item WHERE name = 'Chicken Biryani'),
    150.00,
    2,
            CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP
    );