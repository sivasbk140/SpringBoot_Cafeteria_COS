CREATE TABLE food_menu_items_map (
                                     id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     menu_id INTEGER NOT NULL,
                                     food_item_id INTEGER NOT NULL,
                                     is_available BOOLEAN DEFAULT TRUE,
                                     created_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                     updated_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                     CONSTRAINT fk_menu
                                         FOREIGN KEY (menu_id)
                                             REFERENCES food_menu(id)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE,
                                     CONSTRAINT fk_food_item
                                         FOREIGN KEY (food_item_id)
                                             REFERENCES food_item(id)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE,
                                     UNIQUE (menu_id, food_item_id)
);