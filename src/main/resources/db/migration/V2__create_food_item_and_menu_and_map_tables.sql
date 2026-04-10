CREATE TABLE food_menu (
                           id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           category TEXT NOT NULL,
                           menu_day VARCHAR(20) NOT NULL,
                           created_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                           updated_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                           UNIQUE (category, menu_day)
);

CREATE TABLE food_item (
                           id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           name TEXT NOT NULL,
                           price NUMERIC(10,2) NOT NULL,
                           quantity INTEGER,
                           category TEXT NOT NULL,
                           description TEXT NOT NULL,
                           created_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                           updated_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

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