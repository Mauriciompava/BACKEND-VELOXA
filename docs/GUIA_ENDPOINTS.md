# 🚀 GUÍA PARA DESARROLLO BACKEND - VELOXA

## 📋 Resumen Ejecutivo

El frontend está completamente funcional con 3 secciones principales:
1. **ENVÍOS** - Crear, rastrear y cotizar envíos
2. **PLATAFORMA** - Información de servicios y planes
3. **CONTACTO** - Formulario de contacto

Este documento especifica **exactamente** qué endpoints necesitas implementar en el backend.

---

## 🔗 ENDPOINTS REQUERIDOS

### 1️⃣ GESTIÓN DE ENVÍOS

#### 1.1 POST `/api/shipments` - Crear Nuevo Envío
**Descripción**: Registra un nuevo envío en el sistema

**Body esperado**:
```json
{
  "origin": "CDMX",
  "destination": "Monterrey",
  "weight": 5.5,
  "serviceType": "express",
  "recipient": "Juan Pérez",
  "phone": "+52 55 1234 5678",
  "email": "destinatario@example.com",
  "address": "Calle Principal 123, Apto 4",
  "items": "Electrónica, documentos",
  "valueDeclaration": 500,
  "insurance": true
}
```

**Response esperada**:
```json
{
  "success": true,
  "trackingNumber": "VEL-1234567890",
  "estimatedCost": 89.50,
  "estimatedDays": "0-1",
  "shipmentId": "12345",
  "createdAt": "2026-03-12T10:30:00Z"
}
```

**Lógica necesaria**:
- Generar número de seguimiento único
- Calcular costo: `peso * costoPorKg + (origen ≠ destino ? *1.2 : 1) + (insurance ? 2% : 0)`
- Validar ciudades contra lista disponible
- Validar email
- Guardar en base de datos
- Enviar email de confirmación al usuario

---

#### 1.2 GET `/api/shipments/:trackingNumber` - Obtener Estado de Envío
**Descripción**: Consulta el estado actual de un envío

**URL**: `/api/shipments/VEL-1234567890`

**Response esperada**:
```json
{
  "success": true,
  "shipment": {
    "trackingNumber": "VEL-1234567890",
    "status": "En reparto",
    "origin": "CDMX",
    "destination": "Monterrey",
    "recipient": "Juan Pérez",
    "currentLocation": "Centro de Distribución Regional",
    "estimatedDelivery": "2026-03-13",
    "weight": 5.5,
    "cost": 89.50,
    "timeline": [
      {
        "date": "2026-03-12T10:30:00Z",
        "status": "Recogida realizada",
        "location": "CDMX"
      },
      {
        "date": "2026-03-12T14:30:00Z",
        "status": "En tránsito",
        "location": "Ruta Nacional"
      },
      {
        "date": "2026-03-13T08:00:00Z",
        "status": "En reparto",
        "location": "Centro Regional Monterrey"
      }
    ]
  }
}
```

**Estados posibles**:
- `Pendiente`
- `Recogida realizada`
- `En tránsito`
- `En reparto`
- `Entregado`
- `Cancelado`

---

#### 1.3 GET `/api/shipments?userId=X` - Mis Envíos
**Descripción**: Lista todos los envíos del usuario autenticado

**Query parameters**:
- `userId`: ID del usuario
- `status`: (opcional) Filtrar por estado
- `limit`: (opcional) Cantidad de resultados (default: 10)

**Response esperada**:
```json
{
  "success": true,
  "total": 5,
  "shipments": [
    {
      "trackingNumber": "VEL-1234567890",
      "destination": "Monterrey",
      "status": "En reparto",
      "createdAt": "2026-03-12T10:30:00Z",
      "estimatedDelivery": "2026-03-13"
    }
    // ... más envíos
  ]
}
```

---

#### 1.4 POST `/api/quotes` - Calcular Cotización
**Descripción**: Simula el cálculo de costo sin crear el envío

**Body esperado**:
```json
{
  "origin": "CDMX",
  "destination": "Monterrey",
  "weight": 5.5,
  "serviceType": "standard"
}
```

**Response esperada**:
```json
{
  "success": true,
  "quote": {
    "origin": "CDMX",
    "destination": "Monterrey",
    "weight": 5.5,
    "serviceType": "standard",
    "baseCost": 66,
    "distanceFactor": 1.2,
    "totalCost": 79.20,
    "estimatedDays": "2-3",
    "breakdown": {
      "weightCost": 66,
      "distanceSurcharge": 13.2
    },
    "validUntil": "2026-03-15T10:30:00Z"
  }
}
```

**Tabla de costos por servicio**:
| Servicio | Días | Costo/kg |
|----------|------|----------|
| Express | 0-1 | $25 |
| Premium | 1 | $18 |
| Estándar | 2-3 | $12 |
| Económico | 4-5 | $8 |

---

### 2️⃣ CONTACTO

#### 2.1 POST `/api/contact` - Enviar Mensaje de Contacto
**Descripción**: Procesa formulario de contacto

**Body esperado**:
```json
{
  "fullName": "Juan García",
  "email": "juan@example.com",
  "phone": "+52 55 1234 5678",
  "company": "Mi Negocio S.A.",
  "subject": "Consulta sobre B2B",
  "message": "Quisiera información sobre planes empresariales...",
  "category": "Aumento de Volumen/B2B"
}
```

**Response esperada**:
```json
{
  "success": true,
  "message": "Mensaje recibido exitosamente",
  "contactId": "CONT-12345",
  "ticketNumber": "TKT-2026-001234"
}
```

**Lógica necesaria**:
- Validar email
- Guardar en tabla de contactos
- Asignar ticket automático
- Enviar email de confirmación al usuario
- Enviar email interno al equipo de soporte
- Enviar auto-respuesta con número de ticket

---

### 3️⃣ AUTENTICACIÓN

#### 3.1 POST `/api/auth/login` - Login de Usuario
**Body esperado**:
```json
{
  "email": "usuario@example.com",
  "password": "micontraseña123"
}
```

**Response esperada**:
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user123",
    "email": "usuario@example.com",
    "fullName": "Juan García",
    "company": "Mi Negocio"
  }
}
```

---

#### 3.2 POST `/api/auth/register` - Registro de Usuario
**Body esperado**:
```json
{
  "fullName": "Juan García",
  "email": "juan@example.com",
  "password": "micontraseña123",
  "company": "Mi Negocio S.A.",
  "phone": "+52 55 1234 5678"
}
```

**Response esperada**:
```json
{
  "success": true,
  "message": "Usuario registrado exitosamente",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user123",
    "email": "juan@example.com"
  }
}
```

---

### 4️⃣ RASTREO RÁPIDO

#### 4.1 GET `/api/track/:trackingNumber` - Rastreo Público
**Descripción**: Permite rastrear sin autenticación (información pública)

**Response** - Mismo que `GET /api/shipments/:trackingNumber`

---

## 🗄️ ESTRUCTURA DE BASE DE DATOS

### Tabla: `shipments`
```sql
CREATE TABLE shipments (
  id INT PRIMARY KEY AUTO_INCREMENT,
  tracking_number VARCHAR(20) UNIQUE,
  user_id INT,
  origin VARCHAR(50),
  destination VARCHAR(50),
  weight DECIMAL(10,2),
  service_type VARCHAR(20),
  recipient_name VARCHAR(100),
  recipient_phone VARCHAR(20),
  recipient_email VARCHAR(100),
  recipient_address VARCHAR(255),
  items_description TEXT,
  value_declared DECIMAL(12,2),
  insurance BOOLEAN,
  status VARCHAR(50),
  estimated_cost DECIMAL(12,2),
  estimated_delivery_date DATE,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Tabla: `shipment_timeline`
```sql
CREATE TABLE shipment_timeline (
  id INT PRIMARY KEY AUTO_INCREMENT,
  shipment_id INT,
  status VARCHAR(50),
  location VARCHAR(100),
  timestamp TIMESTAMP,
  FOREIGN KEY (shipment_id) REFERENCES shipments(id)
);
```

### Tabla: `contacts`
```sql
CREATE TABLE contacts (
  id INT PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(100),
  email VARCHAR(100),
  phone VARCHAR(20),
  company VARCHAR(100),
  subject VARCHAR(255),
  message TEXT,
  category VARCHAR(50),
  ticket_number VARCHAR(20) UNIQUE,
  status VARCHAR(20),
  created_at TIMESTAMP,
  responded_at TIMESTAMP
);
```

### Tabla: `users`
```sql
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(100) UNIQUE,
  password_hash VARCHAR(255),
  full_name VARCHAR(100),
  company VARCHAR(100),
  phone VARCHAR(20),
  created_at TIMESTAMP,
  last_login TIMESTAMP
);
```

---

## 🔒 SEGURIDAD

### Autenticación
- Usar JWT tokens
- Expiración: 24 horas
- Refresh tokens para sesiones prolongadas
- Hash contraseñas con bcrypt (minimo salt: 10)

### Validación
- Validar entrada en servidor
- Sanitizar datos (SQL injection prevention)
- Validar formato de email
- Limitar tamaño de requests

### CORS
```javascript
const cors = require('cors');
app.use(cors({
  origin: process.env.FRONTEND_URL || 'http://localhost:3000',
  credentials: true
}));
```

---

## 📧 EMAILS REQUERIDOS

### 1. Confirmación de Envío
**Cuando**: POST `/api/shipments`
**Para**: Destinatario
**Asunto**: "Tu envío está en camino - VEL-123456789"
**Contenido**:
- Número de seguimiento
- Origen y destino
- Peso
- Costo
- Estimado de entrega
- Link para rastrear

### 2. Auto-respuesta Contacto
**Cuando**: POST `/api/contact`
**Para**: Remitente
**Asunto**: "Hemos recibido tu mensaje - Ticket #TKT-2026-001234"
**Contenido**:
- Número de ticket
- Resumen del mensaje
- Tiempo estimado de respuesta
- Datos de contacto directo

### 3. Alerta de Contacto
**Cuando**: POST `/api/contact`
**Para**: Team de soporte
**Asunto**: "[NUEVO CONTACTO] Consulta General - Juan García"
**Contenido**: Todos los datos completos

---

## 🧪 TESTING

### Endpoints a Probar
```bash
# Crear envío
curl -X POST http://localhost:3001/api/shipments \
  -H "Content-Type: application/json" \
  -d '{"origin":"CDMX","destination":"Monterrey","weight":5.5,...}'

# Rastrear
curl http://localhost:3001/api/shipments/VEL-123456

# Cotizar
curl -X POST http://localhost:3001/api/quotes \
  -H "Content-Type: application/json" \
  -d '{"origin":"CDMX","destination":"Monterrey","weight":5.5}'

# Contacto
curl -X POST http://localhost:3001/api/contact \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Juan",...}'
```

---

## 📊 EJEMPLOS DE RESPUESTAS

Todos los endpoints deben retornar en este formato:

```json
{
  "success": true|false,
  "message": "Descripción opcional",
  "data": { /* datos específicos */ },
  "error": "Descripción del error (si success=false)"
}
```

---

## ⚡ CHECKLIST DE IMPLEMENTACIÓN

- [ ] Configurar ambiente Node.js/Express (o tu framework)
- [ ] Configurar base de datos (MySQL/PostgreSQL)
- [ ] Implementar autenticación JWT
- [ ] Crear todas las tablas
- [ ] Implementar endpoint shipments CREATE
- [ ] Implementar endpoint shipments GET
- [ ] Implementar endpoint quotes
- [ ] Implementar endpoint contact
- [ ] Implementar auth login/register
- [ ] Configurar envío de emails
- [ ] Validar CORS
- [ ] Testing de endpoints
- [ ] Documentación API (Swagger)
- [ ] Deploy en servidor

---

## 📝 NOTAS FINALES

- El frontend consume desde `process.env.REACT_APP_API_URL`
- Asegurate que CORS permita `http://localhost:3000`
- Los timestamps deben ser ISO 8601
- Las monedas están en USD (ajustar según necesidad)
- Los días de entrega son hábiles
- Implementar logging para debugging

¡Éxito con la implementación! 🚀
