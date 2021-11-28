CREATE TABLE patients (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    age INTEGER NOT NULL,
    gender VARCHAR(45) NOT NULL,
    phone_number VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL
);