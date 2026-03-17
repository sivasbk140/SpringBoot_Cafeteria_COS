CREATE TABLE "users" (
                         id GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         name TEXT NOT NULL,
                         password TEXT NOT NULL,
                         role TEXT NOT NULL CHECK (role IN ('ADMIN', 'STAFF', 'CUSTOMER', 'DELIVERY_STAFF')),
                         email TEXT NOT NULL UNIQUE,
                         created_on TIMESTAMP,
                         updated_on TIMESTAMP
);