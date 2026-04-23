
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
    tracking_number VARCHAR(20) UNIQUE NOT NULL,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    weight DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'in_transit', 'delivered', 'delayed', 'cancelled') DEFAULT 'pending',
    recipient_name VARCHAR(100) NOT NULL,
    recipient_phone VARCHAR(20) NOT NULL,
    recipient_email VARCHAR(100) NOT NULL,
    recipient_address VARCHAR(255) NOT NULL,
    items_description TEXT,
    value_declared DECIMAL(12,2) DEFAULT 0,
    insurance BOOLEAN DEFAULT FALSE,
    estimated_cost DECIMAL(12,2) DEFAULT 0,
    user_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
