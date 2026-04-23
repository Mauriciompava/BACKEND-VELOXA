-- Crear base de datos
DROP DATABASE IF EXISTS veloxa_db;
CREATE DATABASE veloxa_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE veloxa_db;

-- ============================================================================
-- TABLA: users
-- ============================================================================
CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       company VARCHAR(100),
                       phone VARCHAR(20),
                       role ENUM('admin','user','support') DEFAULT 'user',
                       is_active BOOLEAN DEFAULT true,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       last_login TIMESTAMP NULL,
                       PRIMARY KEY (id),
                       INDEX idx_email (email),
                       INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLA: recipients
-- Libreta de direcciones de destinatarios por usuario
-- ============================================================================
CREATE TABLE recipients (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            user_id BIGINT NOT NULL,
                            full_name VARCHAR(100) NOT NULL,
                            phone VARCHAR(20) NOT NULL,
                            email VARCHAR(100),
                            address VARCHAR(255) NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (id),
                            CONSTRAINT recipients_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLA: shipments
-- ============================================================================
CREATE TABLE shipments (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           tracking_number VARCHAR(20) NOT NULL UNIQUE,
                           user_id BIGINT NOT NULL,
                           recipient_id BIGINT NOT NULL,
                           origin VARCHAR(50) NOT NULL,
                           destination VARCHAR(50) NOT NULL,
                           total_weight DECIMAL(10,2) NOT NULL,
                           service_type ENUM('standard', 'express', 'overnight') NOT NULL,
                           total_value_declared DECIMAL(12,2),
                           insurance BOOLEAN DEFAULT false,
                           status ENUM('Pendiente', 'Recogido', 'En tránsito', 'En reparto', 'Entregado', 'Cancelado') DEFAULT 'Pendiente',
                           estimated_cost DECIMAL(12,2) NOT NULL,
                           estimated_delivery_date DATE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (id),
                           CONSTRAINT shipments_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                           CONSTRAINT shipments_ibfk_2 FOREIGN KEY (recipient_id) REFERENCES recipients(id) ON DELETE RESTRICT,
                           INDEX idx_tracking_number (tracking_number),
                           INDEX idx_user_id (user_id),
                           INDEX idx_status (status),
                           INDEX idx_created_at (created_at),
                           INDEX idx_shipment_user_created (user_id, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLA: shipment_items
-- Desglose de artículos dentro de un envío
-- ============================================================================
CREATE TABLE shipment_items (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                shipment_id BIGINT NOT NULL,
                                description VARCHAR(150) NOT NULL,
                                quantity INT NOT NULL DEFAULT 1,
                                weight_per_unit DECIMAL(10,2),
                                value_per_unit DECIMAL(12,2),
                                PRIMARY KEY (id),
                                CONSTRAINT shipment_items_ibfk_1 FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE,
                                INDEX idx_shipment_id (shipment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLA: shipment_timeline
-- ============================================================================
CREATE TABLE shipment_timeline (
                                   id BIGINT NOT NULL AUTO_INCREMENT,
                                   shipment_id BIGINT NOT NULL,
                                   status ENUM('Pendiente', 'Recogido', 'En tránsito', 'En reparto', 'Entregado', 'Cancelado', 'Excepción') NOT NULL,
                                   location VARCHAR(100) NOT NULL,
                                   timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   description TEXT,
                                   PRIMARY KEY (id),
                                   CONSTRAINT shipment_timeline_ibfk_1 FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE,
                                   INDEX idx_shipment_id (shipment_id),
                                   INDEX idx_timestamp (timestamp),
                                   INDEX idx_timeline_shipment_timestamp (shipment_id, timestamp DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLA: contacts
-- ============================================================================
CREATE TABLE contacts (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          full_name VARCHAR(100) NOT NULL,
                          email VARCHAR(100) NOT NULL,
                          phone VARCHAR(20),
                          company VARCHAR(100),
                          subject VARCHAR(255) NOT NULL,
                          message TEXT NOT NULL,
                          category VARCHAR(50),
                          ticket_number VARCHAR(20) NOT NULL UNIQUE,
                          status ENUM('Nuevo', 'En proceso', 'Resuelto', 'Cerrado') DEFAULT 'Nuevo',
                          priority ENUM('Baja', 'Media', 'Alta', 'Crítica') DEFAULT 'Media',
                          assigned_to BIGINT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          responded_at TIMESTAMP NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT contacts_ibfk_1 FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL,
                          INDEX idx_ticket_number (ticket_number),
                          INDEX idx_email (email),
                          INDEX idx_status (status),
                          INDEX idx_created_at (created_at),
                          INDEX idx_contact_status_created (status, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLA: quotes
-- ============================================================================
CREATE TABLE quotes (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        user_id BIGINT,
                        origin VARCHAR(50) NOT NULL,
                        destination VARCHAR(50) NOT NULL,
                        weight DECIMAL(10,2) NOT NULL,
                        service_type ENUM('standard', 'express', 'overnight') NOT NULL,
                        base_cost DECIMAL(12,2) NOT NULL,
                        distance_factor DECIMAL(5,2) DEFAULT 1,
                        total_cost DECIMAL(12,2) NOT NULL,
                        estimated_days VARCHAR(10),
                        valid_until TIMESTAMP,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (id),
                        CONSTRAINT quotes_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
                        INDEX idx_user_id (user_id),
                        INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLA: audit_log
-- ============================================================================
CREATE TABLE audit_log (
                           id BIGINT NOT NULL AUTO_INCREMENT,
                           user_id BIGINT,
                           action VARCHAR(100) NOT NULL,
                           entity_type VARCHAR(50),
                           entity_id BIGINT,
                           details JSON,
                           ip_address VARCHAR(45),
                           timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (id),
                           CONSTRAINT audit_log_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
                           INDEX idx_user_id (user_id),
                           INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- VISTAS
-- ============================================================================

-- Vista: Envíos activos (Actualizada con joins necesarios)
CREATE VIEW view_active_shipments AS
SELECT
    s.id,
    s.tracking_number,
    s.origin,
    s.destination,
    s.status,
    s.estimated_delivery_date,
    u.email as user_email,
    r.full_name as recipient_name
FROM shipments s
         JOIN users u ON s.user_id = u.id
         JOIN recipients r ON s.recipient_id = r.id
WHERE s.status NOT IN ('Entregado', 'Cancelado');

-- Vista: Contactos pendientes
CREATE VIEW view_pending_contacts AS
SELECT
    id,
    ticket_number,
    full_name,
    email,
    category,
    subject,
    created_at,
    status
FROM contacts
WHERE status IN ('Nuevo', 'En proceso');

-- ============================================================================
-- TRIGGERS
-- ============================================================================

DELIMITER $$

CREATE TRIGGER tr_recipients_update_timestamp
    BEFORE UPDATE ON recipients
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

    CREATE TRIGGER tr_shipments_update_timestamp
        BEFORE UPDATE ON shipments
        FOR EACH ROW
    BEGIN
        SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

        CREATE TRIGGER tr_contacts_update_timestamp
            BEFORE UPDATE ON contacts
            FOR EACH ROW
        BEGIN
            SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

            DELIMITER ;

-- ============================================================================
-- DATOS DE PRUEBA
-- ============================================================================

-- 1. Usuarios
            INSERT INTO users (id, email, password_hash, full_name, company, phone, role, is_active)
            VALUES
                (1, 'admin@veloxa.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/VoO', 'Admin Veloxa', 'Veloxa Inc', '+52 55 1234 5678', 'admin', true),
                (2, 'usuario@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/VoO', 'Juan García', 'Mi Negocio S.A.', '+52 55 9876 5432', 'user', true);

-- 2. Libreta de direcciones (Destinatarios)
            INSERT INTO recipients (id, user_id, full_name, phone, email, address)
            VALUES
                (1, 2, 'Juan Pérez', '+52 81 1234 5678', 'destinatario@example.com', 'Calle Principal 123, Apto 4, Monterrey');

-- 3. Envío
            INSERT INTO shipments (id, tracking_number, user_id, recipient_id, origin, destination, total_weight, service_type, total_value_declared, insurance, status, estimated_cost, estimated_delivery_date)
            VALUES
                (1, 'VEL-0000000001', 2, 1, 'CDMX', 'Monterrey', 5.50, 'express', 500.00, true, 'En tránsito', 89.50, DATE_ADD(CURDATE(), INTERVAL 1 DAY));

-- 4. Artículos del envío (Cumpliendo 1NF)
            INSERT INTO shipment_items (shipment_id, description, quantity, weight_per_unit, value_per_unit)
            VALUES
                (1, 'Laptop Dell Latitude', 1, 3.50, 450.00),
                (1, 'Carpeta con documentos legales', 2, 1.00, 25.00);

-- 5. Timeline de prueba
            INSERT INTO shipment_timeline (shipment_id, status, location, description)
            VALUES
                (1, 'Recogido', 'CDMX', 'Envío recogido del remitente'),
                (1, 'En tránsito', 'Ruta Nacional', 'Envío en transporte nacional'),
                (1, 'En reparto', 'Centro Regional Monterrey', 'Envío llegó a centro de distribución local listo para ruta');