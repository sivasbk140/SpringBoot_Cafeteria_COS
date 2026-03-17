CREATE TABLE availability_map (
                                  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                  menu_id INTEGER NOT NULL,
                                  menu_day TEXT NOT NULL CHECK (
                                      menu_day IN (
                                                   'MONDAY',
                                                   'TUESDAY',
                                                   'WEDNESDAY',
                                                   'THURSDAY',
                                                   'FRIDAY',
                                                   'SATURDAY',
                                                   'SUNDAY',
                                                   'ALLDAY'
                                          )
                                      ),
                                  created_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                  updated_on TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                  CONSTRAINT fk_menu
                                      FOREIGN KEY (menu_id)
                                          REFERENCES food_menu(id)
                                          ON DELETE CASCADE
                                          ON UPDATE CASCADE
);