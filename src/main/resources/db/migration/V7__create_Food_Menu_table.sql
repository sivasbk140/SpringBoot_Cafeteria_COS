CREATE TABLE food_menu (
                           id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           category TEXT NOT NULL,
                           created_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                           updated_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);