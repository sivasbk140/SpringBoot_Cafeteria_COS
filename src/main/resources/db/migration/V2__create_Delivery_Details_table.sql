CREATE TABLE delivery_details (
                                  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                  email TEXT NOT NULL,
                                  phone_number TEXT NOT NULL CHECK (phone_number ~ '^[0-9]{10}$'),
    location TEXT NOT NULL,
    user_id INTEGER NOT NULL,
    created_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES "Users"(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    UNIQUE (email, user_id)
);

CREATE INDEX idx_delivery_user_id ON delivery_details(user_id);