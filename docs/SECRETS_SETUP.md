# 🔐 Configuración de Secretos - BACKEND-VELOXA

## Resumen

Este proyecto utiliza **archivos de secretos separados** para proteger datos sensibles (contraseñas, API keys, etc.) evitando que se envíen al repositorio de Git.

## Estructura

```
src/main/resources/
├── application.properties           ✓ Enviado al repo (valores públicos)
├── application-secrets.example      ✓ Enviado al repo (plantilla)
└── application-secrets.properties   ✗ IGNORADO en Git (secretos reales)
```

## 🚀 Configuración Inicial

### 1️⃣ Copia el archivo de ejemplo

```bash
cp src/main/resources/application-secrets.example \
   src/main/resources/application-secrets.properties
```

### 2️⃣ Edita con tus credenciales reales

```bash
nano src/main/resources/application-secrets.properties
```

### 3️⃣ Completa los valores (ejemplo)

```properties
# MySQL
spring.datasource.mysql.password=tu_contraseña_mysql
spring.datasource.mysql.username=root

# SQL Server (si lo usas)
spring.datasource.sqlserver.password=tu_contraseña_sqlserver
```

### 4️⃣ ¡Listo! 

Git ignorará automáticamente `application-secrets.properties`. Verifica:

```bash
git status  # No debería aparecer application-secrets.properties
```

## 📝 Variables Disponibles

El archivo `application-secrets.properties` soporta:

```properties
# Base de Datos
spring.datasource.mysql.password=password
spring.datasource.mysql.username=usuario
spring.datasource.sqlserver.password=password

# Otros secretos (agrega los que necesites)
# spring.security.jwt.secret=tu_jwt_secret
# spring.mail.password=tu_email_password
# app.api.key=tu_api_key
```

## 🔄 Cómo Funciona

El flujo de propiedades es:

1. **Spring carga** `application.properties` (público)
2. **Encuentra placeholders** como `${spring.datasource.mysql.password}`
3. **Importa** `application-secrets.properties` (privado)
4. **Resuelve** los valores faltantes

**Resultado:** Los valores sensibles se cargan en tiempo de ejecución sin exponerse en el código.

## ⚙️ Configuración en .gitignore

Estos archivos están ignorados:

```
application-secrets.properties
application-local.properties
.env
.env.local
secrets.properties
```

## ✅ Checklist para Desarrolladores

- [ ] Copié `application-secrets.example` a `application-secrets.properties`
- [ ] Llené `application-secrets.properties` con mis credenciales
- [ ] Ejecuté `git status` y NO veo `application-secrets.properties`
- [ ] La aplicación inicia correctamente con `./mvnw spring-boot:run`

## 🚨 Seguridad

**NUNCA:**
- ❌ Envíes `application-secrets.properties` al repositorio
- ❌ Commits con credenciales visibles
- ❌ Compartas tus contraseñas

**SIEMPRE:**
- ✅ Mantén `application-secrets.properties` local
- ✅ Usa valores diferentes por ambiente
- ✅ Usa variables de entorno en producción

## 🐳 Para Docker/Producción

En lugar de archivos, usa variables de entorno:

```bash
docker run -e DB_PASSWORD=secret -e DB_USER=root my-app
```

Y en `application.properties`:

```properties
spring.datasource.password=${DB_PASSWORD}
spring.datasource.username=${DB_USER}
```

## 📞 Preguntas Frecuentes

**P: ¿Qué pasa si olvido crear `application-secrets.properties`?**
R: La aplicación intentará usar valores por defecto (inválidos). Spring mostrará un error claro.

**P: ¿Puedo tener múltiples archivos de secretos?**
R: Sí, puedes crear:
- `application-secrets-dev.properties`
- `application-secrets-prod.properties`
- Y cargarlos según el ambiente

**P: ¿Cómo paso credenciales a otros desarrolladores?**
R: Usa un gestor de secretos seguro (1Password, AWS Secrets Manager, Vault) o comparte por canal seguro (no por email).

---

**Última actualización:** 2026-03-18
