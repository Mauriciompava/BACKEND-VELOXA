# 🚀 QUICK START - VELOXA EXPRESS BACKEND

## Instrucciones Rápidas para Comenzar

### Paso 1: Crear la Base de Datos MySQL

```bash
# Ingresa a la carpeta del proyecto
cd ~/GITHUB/CESDE-INTEGRADOR/BACKEND-VELOXA

# Crea la base de datos (requiere contraseña de MySQL)
mysql -u root -p < databaseMySQL.sql

# Cuando pida contraseña, ingresa la contraseña de tu MySQL
```

**Verifica que se creó correctamente:**
```bash
mysql -u root -p
mysql> SHOW DATABASES;
mysql> USE veloxa_db;
mysql> SHOW TABLES;
# Debe mostrar: users, shipments, shipment_timeline, contacts, quotes, audit_log
mysql> EXIT;
```

---

### Paso 2: Configurar Secretos (Contraseña de BD)

```bash
# Copia el archivo de ejemplo
cp src/main/resources/application-secrets.example \
   src/main/resources/application-secrets.properties

# Edita el archivo con tu contraseña MySQL
nano src/main/resources/application-secrets.properties
```

**Contenido a editar:**
```properties
spring.datasource.mysql.password=AQUI_TU_CONTRASEÑA_MYSQL
spring.datasource.mysql.username=root
```

Guarda con `Ctrl+X` → `Y` → `Enter`

---

### Paso 3: Compilar el Proyecto

```bash
./mvnw clean compile
```

Debe terminar con:
```
BUILD SUCCESS
```

---

### Paso 4: Ejecutar la Aplicación

```bash
./mvnw spring-boot:run
```

Espera a ver:
```
Started BackendVeloxaApplication in X.XXX seconds (JVM running for X.XXX)
```

---

### Paso 5: Probar los Endpoints

En otra terminal:

#### ✅ Test 1: Crear Envío
```bash
curl -X POST http://localhost:8080/api/shipments \
  -H "Content-Type: application/json" \
  -d '{
    "origin": "CDMX",
    "destination": "Monterrey",
    "weight": 5.5,
    "serviceType": "express",
    "recipient": "Juan Pérez",
    "phone": "+52 55 1234 5678",
    "email": "juan@example.com",
    "address": "Calle Principal 123",
    "items": "Documentos",
    "valueDeclaration": 500,
    "insurance": true
  }'
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Envío creado exitosamente",
  "data": {
    "trackingNumber": "VEL-1234567890",
    "estimatedCost": 165.00,
    "estimatedDays": "0-1",
    "shipmentId": "1",
    "createdAt": "2026-03-18T13:30:00"
  }
}
```

---

#### ✅ Test 2: Rastrear Envío

Usa el `trackingNumber` de la respuesta anterior:

```bash
curl http://localhost:8080/api/shipments/VEL-1234567890
```

---

#### ✅ Test 3: Calcular Cotización

```bash
curl -X POST http://localhost:8080/api/quotes \
  -H "Content-Type: application/json" \
  -d '{
    "origin": "CDMX",
    "destination": "Monterrey",
    "weight": 5.5,
    "serviceType": "standard"
  }'
```

---

#### ✅ Test 4: Enviar Mensaje de Contacto

```bash
curl -X POST http://localhost:8080/api/contact \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Juan García",
    "email": "juan@example.com",
    "phone": "+52 55 1234 5678",
    "company": "Mi Negocio",
    "subject": "Consulta sobre planes B2B",
    "message": "Quisiera información sobre planes empresariales",
    "category": "Aumento de Volumen"
  }'
```

---

#### ✅ Test 5: Rastreo Público (sin autenticación)

```bash
curl http://localhost:8080/api/track/VEL-1234567890
```

---

## 📋 Checklist

- [ ] Base de datos MySQL creada (`veloxa_db`)
- [ ] Archivo `application-secrets.properties` configurado
- [ ] Proyecto compilado sin errores
- [ ] Aplicación ejecutándose en `http://localhost:8080`
- [ ] Test 1 (Crear envío) funcionando ✅
- [ ] Test 2 (Rastrear) funcionando ✅
- [ ] Test 3 (Cotizar) funcionando ✅
- [ ] Test 4 (Contacto) funcionando ✅
- [ ] Test 5 (Rastreo público) funcionando ✅

---

## 🐛 Troubleshooting

### Error: "Unable to connect to database"
```
mysql -u root -p < databaseMySQL.sql
# Verifica que MySQL está en ejecución
# Verifica la contraseña en application-secrets.properties
```

### Error: "Port 8080 already in use"
```bash
# O usa otro puerto en application.properties
server.port=8081
```

### Error: "Class not found"
```bash
# Recompila
./mvnw clean compile -X
```

---

## 📊 Endpoints Disponibles

| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/shipments` | Crear envío |
| GET | `/api/shipments/{tracking}` | Obtener envío |
| POST | `/api/quotes` | Calcular cotización |
| POST | `/api/contact` | Enviar contacto |
| GET | `/api/track/{tracking}` | Rastreo público |

---

## 📁 Estructura de Carpetas

```
BACKEND-VELOXA/
├── src/main/java/org/cesde/velotax/
│   ├── entity/          (5 entidades)
│   ├── repository/      (5 repositorios)
│   ├── service/         (3 servicios)
│   ├── controller/      (4 controladores)
│   ├── dto/             (8 DTOs)
│   └── config/          (CORS)
├── src/main/resources/
│   ├── application.properties
│   ├── application-secrets.properties (⚠️ NO enviar)
│   └── application-secrets.example
├── databaseMySQL.sql
├── IMPLEMENTATION_SUMMARY.md
├── QUICK_START.md
└── mvnw
```

---

## 🎓 Próximos Pasos

Para implementar funcionalidades adicionales:

1. **Autenticación JWT**
   - Crear `AuthController` con login/register
   - Implementar `JwtTokenProvider`
   - Proteger endpoints con `@PreAuthorize`

2. **Envío de Emails**
   - Agregar `spring-boot-starter-mail`
   - Crear `EmailService`
   - Enviar confirmaciones

3. **Validaciones Avanzadas**
   - Usar `@Valid` en DTOs
   - Agregar `@NotBlank`, `@Email`, etc.
   - Global exception handler

4. **Testing**
   - Crear tests unitarios
   - Agregar `@SpringBootTest`
   - Test de controladores

5. **Documentación API**
   - Agregar Swagger/Springdoc-openapi
   - Documentar cada endpoint
   - URL: `http://localhost:8080/swagger-ui.html`

---

## 💡 Notas

- ✅ Todos los endpoints están funcionando
- ✅ Base de datos completamente configurada
- ✅ Cálculo de costos implementado correctamente
- ✅ CORS habilitado para localhost:3000 y localhost:3001
- ⚠️ Falta: Autenticación JWT (TODO)
- ⚠️ Falta: Envío de emails (TODO)

---

**¡Listo para usar!** 🎉

Si tienes problemas, revisa los logs en la terminal donde ejecutas `./mvnw spring-boot:run`
