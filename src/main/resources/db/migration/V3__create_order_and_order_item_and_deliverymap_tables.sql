
CREATE TABLE order_table (
                             id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             user_id INTEGER NOT NULL,
                             status TEXT NOT NULL CHECK (
                                 status IN (
                                            'PLACED_ORDER',
                                            'ORDER_CONFIRMED',
                                            'ORDER_PREPARING',
                                            'ASSIGNED_DELIVERY_STAFF',
                                            'ORDER_DELIVERED',
                                            'ORDER_CANCELLED'
                                     )
                                 ),
                             created_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                             updated_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_order_user
                                 FOREIGN KEY (user_id)
                                     REFERENCES "users"(id)
                                     ON DELETE CASCADE
                                     ON UPDATE CASCADE
);

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


CREATE TABLE order_delivery_map (
                                    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                    delivery_staff_id BIGINT,
                                    order_id INTEGER NOT NULL UNIQUE REFERENCES order_table(id) ON DELETE CASCADE,
                                    name TEXT NOT NULL,
                                    phone TEXT NOT NULL,
                                    address TEXT NOT NULL,
                                    created_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                    updated_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
