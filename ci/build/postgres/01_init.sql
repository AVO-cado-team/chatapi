CREATE TABLE users (
   id SERIAL PRIMARY KEY,
   username VARCHAR(50) UNIQUE NOT NULL,
   email VARCHAR(255) UNIQUE NOT NULL,
   password_hash VARCHAR(100) NOT NULL,
   is_verified BOOLEAN DEFAULT FALSE,
   created_at TIMESTAMP DEFAULT NOW()
);