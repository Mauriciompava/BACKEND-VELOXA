
CREATE DATABASE veloxa_db;
USE veloxa_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    role ENUM('admin', 'client') DEFAULT 'client',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shipments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tracking_number VARCHAR(20) UNIQUE,
    origin VARCHAR(100),
    destination VARCHAR(100),
    weight DECIMAL(10,2),
    status ENUM('pending', 'in_transit', 'delivered', 'delayed') DEFAULT 'pending',
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
