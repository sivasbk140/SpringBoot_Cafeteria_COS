CREATE TABLE order_items (
                             id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             order_id INTEGER NOT NULL,
                             food_item_id INTEGER NOT NULL,
                             price NUMERIC(10,2) NOT NULL,
                             quantity INTEGER NOT NULL,
                             created_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                             updated_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES order_table(id)
                                     ON DELETE CASCADE,
                             CONSTRAINT fk_food_item
                                 FOREIGN KEY (food_item_id)
                                     REFERENCES food_item(id)
);