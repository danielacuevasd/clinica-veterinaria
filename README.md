# Sistema de Gestión - Clínica Veterinaria

Proyecto Desarrollo FullStack I. Sistema de gestión veterinaria con arquitectura de microservicios.

## Stack tecnológico

- Java 21 / Spring Boot 4.0.6
- MySQL 8 (Docker)
- Apache Kafka (mensajería asíncrona)
- Eureka (Service Discovery)
- API Gateway
- OpenFeign (comunicación síncrona)
- JWT + Spring Security
- Flyway (migraciones SQL)
- Lombok

## Microservicios

| Servicio | Puerto | Descripción |
|---|---|---|
| eureka-server | 8761 | Service Discovery |
| api-gateway | 8080 | Enrutamiento centralizado |
| ms-auth | 8081 | Autenticación JWT |
| ms-usuarios | 8082 | Gestión de dueños |
| ms-mascotas | 8083 | Gestión de mascotas e historial |
| ms-veterinarios | 8084 | Gestión de veterinarios y horarios |
| ms-citas | 8085 | Agendamiento de citas |
| ms-consultas | 8086 | Registro de consultas |
| ms-tratamientos | 8087 | Gestión de tratamientos |
| ms-inventario | 8088 | Control de stock de medicamentos |
| ms-facturacion | 8089 | Emisión de facturas |
| ms-notificaciones | 8090 | Notificaciones a dueños |

## Requisitos previos

- Docker Desktop instalado y corriendo
- Java 21
- IntelliJ IDEA
- Postman (para pruebas)

## Cómo levantar el proyecto

**1. Levantar bases de datos y Kafka:**
```bash
docker-compose up -d
```

**2. Verificar contenedores:**
```bash
docker-compose ps
```

**3. Levantar microservicios en IntelliJ en este orden:**
1. eureka-server
2. api-gateway
3. ms-auth
4. Resto de microservicios en cualquier orden

**4. Verificar Eureka:**
Abrir http://localhost:8761 en el navegador.

## Flujos principales

**Comunicación síncrona (Feign):**
- ms-citas → ms-veterinarios (verificar disponibilidad)
- ms-citas → ms-mascotas (verificar mascota del dueño)

**Comunicación asíncrona (Kafka):**
- ms-consultas publica `consulta.registrada`
- ms-tratamientos, ms-inventario, ms-facturacion y ms-notificaciones consumen `consulta.registrada`

## Roles

| Rol | Descripción |
|---|---|
| ADMIN | Acceso total al sistema |
| VETERINARIO | Consultas, tratamientos, citas |
| DUENO | Sus mascotas, citas y facturas |

## Diagramas Entidad-Relación (DER)

> Las relaciones entre microservicios son lógicas, no físicas, siguiendo el principio de base de datos independiente por microservicio.

### ms-auth
```mermaid
erDiagram
    USUARIOS {
        BIGINT id PK
        VARCHAR username "NOT NULL UNIQUE"
        VARCHAR password "NOT NULL"
        ENUM rol "ADMIN | VETERINARIO | DUENO"
        BOOLEAN activo "DEFAULT TRUE"
        DATETIME created_at "DEFAULT NOW"
    }
```

### ms-usuarios
```mermaid
erDiagram
    DUENOS {
        BIGINT id PK
        VARCHAR nombre "NOT NULL"
        VARCHAR apellido "NOT NULL"
        VARCHAR email "NOT NULL UNIQUE"
        VARCHAR telefono "NULLABLE"
        VARCHAR rut "NOT NULL UNIQUE"
        BOOLEAN activo "DEFAULT TRUE"
        DATETIME created_at "DEFAULT NOW"
    }
```

### ms-mascotas
```mermaid
erDiagram
    MASCOTAS {
        BIGINT id PK
        VARCHAR nombre "NOT NULL"
        VARCHAR especie "NOT NULL"
        VARCHAR raza "NULLABLE"
        DATE fecha_nacimiento "NULLABLE"
        BIGINT id_dueno "NOT NULL (ref ms-usuarios)"
        BOOLEAN activo "DEFAULT TRUE"
        DATETIME created_at "DEFAULT NOW"
    }
    HISTORIAL_MEDICO {
        BIGINT id PK
        BIGINT id_mascota "NOT NULL FK"
        VARCHAR descripcion "NOT NULL"
        DATE fecha "NOT NULL"
    }
    MASCOTAS ||--o{ HISTORIAL_MEDICO : "tiene"
```

### ms-veterinarios
```mermaid
erDiagram
    VETERINARIOS {
        BIGINT id PK
        VARCHAR nombre "NOT NULL"
        VARCHAR apellido "NOT NULL"
        VARCHAR especialidad "NOT NULL"
        VARCHAR email "NOT NULL UNIQUE"
        VARCHAR telefono "NULLABLE"
        BOOLEAN disponible "DEFAULT TRUE"
        BOOLEAN activo "DEFAULT TRUE"
        DATETIME created_at "DEFAULT NOW"
    }
    HORARIOS {
        BIGINT id PK
        BIGINT id_veterinario "NOT NULL FK"
        VARCHAR dia_semana "NOT NULL"
        TIME hora_inicio "NOT NULL"
        TIME hora_fin "NOT NULL"
    }
    VETERINARIOS ||--o{ HORARIOS : "tiene"
```

### ms-citas
```mermaid
erDiagram
    CITAS {
        BIGINT id PK
        BIGINT id_mascota "NOT NULL (ref ms-mascotas)"
        BIGINT id_veterinario "NOT NULL (ref ms-veterinarios)"
        BIGINT id_dueno "NOT NULL (ref ms-usuarios)"
        DATETIME fecha_hora "NOT NULL"
        VARCHAR motivo "NULLABLE"
        ENUM estado "PENDIENTE | CONFIRMADA | CANCELADA"
        DATETIME created_at "DEFAULT NOW"
    }
```

### ms-consultas
```mermaid
erDiagram
    CONSULTAS {
        BIGINT id PK
        BIGINT id_cita "NOT NULL (ref ms-citas)"
        BIGINT id_mascota "NOT NULL (ref ms-mascotas)"
        BIGINT id_veterinario "NOT NULL (ref ms-veterinarios)"
        BIGINT id_dueno "NOT NULL (ref ms-usuarios)"
        VARCHAR diagnostico "NOT NULL"
        DECIMAL peso "NULLABLE"
        DECIMAL temperatura "NULLABLE"
        TEXT observaciones "NULLABLE"
        DATETIME fecha "DEFAULT NOW"
    }
```

### ms-tratamientos
```mermaid
erDiagram
    TRATAMIENTOS {
        BIGINT id PK
        BIGINT id_consulta "NOT NULL (ref ms-consultas)"
        BIGINT id_mascota "NOT NULL (ref ms-mascotas)"
        VARCHAR medicamento "NOT NULL"
        VARCHAR dosis "NOT NULL"
        VARCHAR frecuencia "NOT NULL"
        INT duracion_dias "NOT NULL"
        TEXT indicaciones "NULLABLE"
        DATETIME created_at "DEFAULT NOW"
    }
```

### ms-inventario
```mermaid
erDiagram
    MEDICAMENTOS {
        BIGINT id PK
        VARCHAR nombre "NOT NULL UNIQUE"
        INT stock "NOT NULL DEFAULT 0"
        VARCHAR unidad "NOT NULL"
        DECIMAL precio_unitario "NOT NULL"
        BOOLEAN activo "DEFAULT TRUE"
        DATETIME created_at "DEFAULT NOW"
    }
    MOVIMIENTOS_STOCK {
        BIGINT id PK
        BIGINT id_medicamento "NOT NULL FK"
        ENUM tipo "ENTRADA | SALIDA"
        INT cantidad "NOT NULL"
        VARCHAR motivo "NULLABLE"
        DATETIME fecha "DEFAULT NOW"
    }
    MEDICAMENTOS ||--o{ MOVIMIENTOS_STOCK : "registra"
```

### ms-facturacion
```mermaid
erDiagram
    FACTURAS {
        BIGINT id PK
        BIGINT id_consulta "NOT NULL (ref ms-consultas)"
        BIGINT id_mascota "NOT NULL (ref ms-mascotas)"
        BIGINT id_dueno "NOT NULL (ref ms-usuarios)"
        DECIMAL total "NOT NULL DEFAULT 0"
        ENUM estado "PENDIENTE | PAGADA | ANULADA"
        TEXT observaciones "NULLABLE"
        DATETIME created_at "DEFAULT NOW"
    }
```

### ms-notificaciones
```mermaid
erDiagram
    NOTIFICACIONES {
        BIGINT id PK
        VARCHAR tipo "CONSULTA_REGISTRADA | CITA_CONFIRMADA"
        BIGINT id_destinatario "NOT NULL (ref ms-usuarios)"
        VARCHAR mensaje "NOT NULL"
        BOOLEAN enviado "DEFAULT FALSE"
        DATETIME fecha_envio "DEFAULT NOW"
    }
```
