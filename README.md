# Finance Backend

API REST del sistema personal de administración financiera.

Desarrollada con Java y Spring Boot. Administra autenticación, usuarios, tarjetas, estados de cuenta, movimientos y pagos utilizando PostgreSQL como base de datos.

## Stack

- Java 21+
- Spring Boot 4.x
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT
- BCrypt
- PostgreSQL
- Maven

## Arquitectura

```text
Frontend Next.js
      ↓
Spring Boot REST API
      ↓
PostgreSQL
```

El ETL también consume esta API:

```text
Python ETL
    ↓
Spring Boot REST API
    ↓
PostgreSQL
```

El ETL no escribe directamente en PostgreSQL.

Spring Boot no procesa archivos Excel.

## Requisitos

Para ejecutar el proyecto necesitas:

- Java 21+
- Maven o Maven Wrapper
- PostgreSQL

## Variables de entorno

Configura:

```env
DB_URL=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
JWT_EXPIRATION=
```

Las credenciales y secretos nunca deben almacenarse directamente en el código ni subirse al repositorio.

## Puerto

La aplicación se ejecuta en:

```text
9000
```

La API está disponible en:

```text
http://localhost:9000/api
```

## Ejecución

Linux / macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

También puedes verificar la compilación con:

```bash
./mvnw clean compile
```

En Windows:

```bash
mvnw.cmd clean compile
```

## Base de datos

El proyecto utiliza PostgreSQL.

Durante desarrollo:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Actualmente no se utiliza Flyway.

## Autenticación

El backend utiliza JWT y Spring Security.

Endpoints públicos:

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/verify-email
POST /api/auth/forgot-password
POST /api/auth/reset-password
```

Las demás rutas requieren autenticación.

Las peticiones protegidas utilizan:

```http
Authorization: Bearer <token>
```

El usuario utiliza su email como username para Spring Security.

## Roles

```text
ADMIN
USER
DEBTOR
```

Todo usuario nuevo inicia como:

```text
DEBTOR
```

y:

```text
emailVerified = false
```

Un usuario no puede iniciar sesión hasta verificar su correo electrónico.

## Módulos principales

```text
User
CardProduct
Card
Concept
Statement
StatementEntry
Payment
```

No existe `Person` ni `People`.

Las relaciones personales utilizan `User` y `userId`.

## Modelo financiero

### User

El usuario representa tanto usuarios del sistema como personas relacionadas con operaciones financieras.

### CardProduct

```text
productId
bank
cardName
```

### Card

```text
cardId
cardCode
product
user
active
```

Relaciones:

```text
Card N:1 CardProduct
Card N:1 User
```

### Concept

```text
conceptId
name
```

### Statement

```text
statementId
card
year
month
periodStart
periodEnd
paymentDate
```

Relación:

```text
Statement N:1 Card
```

Existe una restricción única para:

```text
card + year + month
```

### StatementEntry

```text
entryId
statement
concept
user
description
purchaseDate
installmentAmount
paid
msiCurrent
msiTotal
purchaseTotal
remainingMonths
remainingTotal
```

Relaciones:

```text
StatementEntry N:1 Statement
StatementEntry N:1 Concept
StatementEntry N:1 User
```

### Payment

```text
paymentId
statement
user
amount
paymentType
```

Relaciones:

```text
Payment N:1 Statement
Payment N:1 User
```

## API

La API utiliza como prefijo:

```text
/api
```

Ejemplo:

```text
http://localhost:9000/api
```

Los Controllers no exponen Entities directamente.

Los contratos de entrada y salida utilizan DTOs.

## Seguridad

La aplicación utiliza:

- Spring Security
- JWT
- BCrypt
- sesiones stateless
- autorización mediante roles
- validación de email
- recuperación de contraseña

Las variables sensibles utilizadas por seguridad deben mantenerse fuera del código fuente.

## ETL

El ETL está desarrollado de manera independiente en Python.

Su flujo es:

```text
Excel
  ↓
Python ETL
  ↓
Spring Boot REST API
  ↓
PostgreSQL
```

El ETL debe utilizar los contratos expuestos por la API.

No debe escribir directamente en PostgreSQL.

## Desarrollo local

Orden recomendado:

1. Iniciar PostgreSQL.
2. Configurar las variables de entorno.
3. Iniciar Spring Boot.
4. Verificar la API en el puerto `9000`.
5. Iniciar el frontend.
6. Ejecutar el ETL cuando sea necesario cargar información.

```text
Backend
http://localhost:9000

API
http://localhost:9000/api
```
