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