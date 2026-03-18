CREATE DATABASE veloxa_db;
GO

USE veloxa_db;
GO

CREATE TABLE users (
                       id INT IDENTITY(1,1) PRIMARY KEY,
                       name VARCHAR(100),
                       email VARCHAR(100) UNIQUE,
                       password VARCHAR(255),
                       role VARCHAR(10) DEFAULT 'client' CHECK (role IN ('admin', 'client')),
                       created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE shipments (
                           id INT IDENTITY(1,1) PRIMARY KEY,
                           tracking_number VARCHAR(20) UNIQUE,
                           origin VARCHAR(100),
                           destination VARCHAR(100),
                           weight DECIMAL(10,2),
                           status VARCHAR(20) DEFAULT 'pending'
                               CHECK (status IN ('pending', 'in_transit', 'delivered', 'delayed')),
                           user_id INT,
                           CONSTRAINT FK_shipments_users FOREIGN KEY (user_id) REFERENCES users(id)
);