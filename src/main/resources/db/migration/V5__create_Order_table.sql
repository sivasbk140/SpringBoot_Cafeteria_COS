CREATE TABLE order_table (
                             id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             user_id INTEGER NOT NULL,
                             status TEXT NOT NULL CHECK (
                                 status IN (
                                            'PLACED_ORDER',
                                            'ORDER_DELIVERED',
                                            'ORDER_CANCELLED',
                                            'PENDING_DELIVERY',
                                            'WAITING_FOR_DELIVERY'
                                     )
                                 ),
                             created_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                             updated_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_order_user
                                 FOREIGN KEY (user_id)
                                     REFERENCES "Users"(id)
                                     ON DELETE CASCADE
                                     ON UPDATE CASCADE
);