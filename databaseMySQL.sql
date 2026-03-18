-- ============================================================================
-- BASE DE DATOS VELOXA - VELOXA EXPRESS
-- ============================================================================
-- Crear base de datos
DROP DATABASE IF EXISTS veloxa_db;
CREATE DATABASE veloxa_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE veloxa_db;

-- ============================================================================
-- TABLA: USERS (Usuarios del sistema)
-- ============================================================================
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    company VARCHAR(100),
    phone VARCHAR(20),
    role ENUM('admin', 'user', 'support') DEFAULT 'user',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    INDEX idx_email (email),
    INDEX idx_is_active (is_active)
);

-- ============================================================================
-- TABLA: SHIPMENTS (Envíos)
-- ============================================================================
CREATE TABLE shipments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tracking_number VARCHAR(20) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    origin VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    weight DECIMAL(10,2) NOT NULL,
    service_type VARCHAR(20) NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    recipient_phone VARCHAR(20) NOT NULL,
    recipient_email VARCHAR(100) NOT NULL,
    recipient_address VARCHAR(255) NOT NULL,
    items_description TEXT,
    value_declared DECIMAL(12,2),
    insurance BOOLEAN DEFAULT false,
    status VARCHAR(50) DEFAULT 'Pendiente',
    estimated_cost DECIMAL(12,2) NOT NULL,
    estimated_delivery_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_tracking_number (tracking_number),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- ============================================================================
-- TABLA: SHIPMENT_TIMELINE (Historial de seguimiento)
-- ============================================================================
CREATE TABLE shipment_timeline (
    id INT PRIMARY KEY AUTO_INCREMENT,
    shipment_id INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    location VARCHAR(100) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE,
    INDEX idx_shipment_id (shipment_id),
    INDEX idx_timestamp (timestamp)
);

-- ============================================================================
-- TABLA: CONTACTS (Contactos/Mensajes)
-- ============================================================================
CREATE TABLE contacts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    company VARCHAR(100),
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    category VARCHAR(50),
    ticket_number VARCHAR(20) UNIQUE NOT NULL,
    status VARCHAR(20) DEFAULT 'Nuevo',
    priority VARCHAR(20) DEFAULT 'Media',
    assigned_to INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    responded_at TIMESTAMP NULL,
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_ticket_number (ticket_number),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- ============================================================================
-- TABLA: QUOTES (Cotizaciones)
-- ============================================================================
CREATE TABLE quotes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    origin VARCHAR(50) NOT NULL,
    destination VARCHAR(50) NOT NULL,
    weight DECIMAL(10,2) NOT NULL,
    service_type VARCHAR(20) NOT NULL,
    base_cost DECIMAL(12,2) NOT NULL,
    distance_factor DECIMAL(5,2) DEFAULT 1,
    total_cost DECIMAL(12,2) NOT NULL,
    estimated_days VARCHAR(10),
    valid_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);

-- ============================================================================
-- TABLA: AUDIT_LOG (Log de auditoría)
-- ============================================================================
CREATE TABLE audit_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INT,
    details JSON,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_timestamp (timestamp)
);

-- ============================================================================
-- DATOS INICIALES
-- ============================================================================

-- Usuario admin de prueba
INSERT INTO users (email, password_hash, full_name, company, phone, role) 
VALUES ('admin@veloxa.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/VoO', 'Admin Veloxa', 'Veloxa Inc', '+52 55 1234 5678', 'admin');

-- Usuario regular de prueba
INSERT INTO users (email, password_hash, full_name, company, phone, role) 
VALUES ('usuario@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/VoO', 'Juan García', 'Mi Negocio S.A.', '+52 55 9876 5432', 'user');

-- Envío de prueba
INSERT INTO shipments (
    tracking_number, user_id, origin, destination, weight, service_type, 
    recipient_name, recipient_phone, recipient_email, recipient_address, 
    items_description, value_declared, insurance, status, estimated_cost, estimated_delivery_date
) VALUES (
    'VEL-0000000001', 2, 'CDMX', 'Monterrey', 5.5, 'express',
    'Juan Pérez', '+52 81 1234 5678', 'destinatario@example.com', 'Calle Principal 123, Apto 4',
    'Electrónica, documentos', 500, true, 'Pendiente', 89.50, DATE_ADD(CURDATE(), INTERVAL 1 DAY)
);

-- Timeline de prueba
INSERT INTO shipment_timeline (shipment_id, status, location, description) VALUES
(1, 'Recogida realizada', 'CDMX', 'Envío recogido del remitente'),
(1, 'En tránsito', 'Ruta Nacional', 'Envío en transporte nacional'),
(1, 'En reparto', 'Centro Regional Monterrey', 'Envío llegó a centro de distribución local');

-- ============================================================================
-- VISTAS ÚTILES
-- ============================================================================

-- Vista: Envíos activos con información de usuario
CREATE VIEW view_active_shipments AS
SELECT 
    s.id,
    s.tracking_number,
    s.origin,
    s.destination,
    s.status,
    s.estimated_delivery_date,
    u.email as user_email,
    u.full_name as user_name
FROM shipments s
JOIN users u ON s.user_id = u.id
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
-- ÍNDICES ADICIONALES PARA PERFORMANCE
-- ============================================================================

CREATE INDEX idx_shipment_user_created ON shipments(user_id, created_at DESC);
CREATE INDEX idx_contact_status_created ON contacts(status, created_at DESC);
CREATE INDEX idx_timeline_shipment_timestamp ON shipment_timeline(shipment_id, timestamp DESC);

-- ============================================================================
-- TRIGGER: Actualizar updated_at en shipments
-- ============================================================================

DELIMITER $$
CREATE TRIGGER tr_shipments_update_timestamp
BEFORE UPDATE ON shipments
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$
DELIMITER ;

-- ============================================================================
-- TRIGGER: Actualizar updated_at en contacts
-- ============================================================================

DELIMITER $$
CREATE TRIGGER tr_contacts_update_timestamp
BEFORE UPDATE ON contacts
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$
DELIMITER ;

