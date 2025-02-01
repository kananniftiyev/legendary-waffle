CREATE TABLE users (
                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                       username VARCHAR(50) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL

);