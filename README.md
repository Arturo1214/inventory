# Microservicios Vulcan Ltda

Este repositorio contiene tres microservicios:

- **auth-service**: Autenticación JWT y gestión de usuarios.
- **inventory-service**: Gestión de productos y órdenes.
- **api-gateway**: API Gateway con Spring Cloud Gateway.

Además, incluye un `docker-compose.yml` en la raíz para orquestar los servicios y sus bases de datos.

---

## Prerrequisitos

- Java 17
- Maven 3.8+
- PostgreSQL 14 (solo para ejecución sin Docker)
- Docker y Docker Compose (para ejecución con contenedores)

---

## Ejecución sin Docker

1. **Crear bases de datos** en tu instancia de PostgreSQL local:

   ```sql
   CREATE DATABASE authdb;
   CREATE DATABASE inventorydb;
   ```

2. **Configurar credenciales** en cada servicio:

   - En `auth-service/src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
     spring.datasource.username=postgres
     spring.datasource.password=tu_password
     ```
   - En `inventory-service/src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/inventorydb
     spring.datasource.username=postgres
     spring.datasource.password=tu_password
     ```
   - En `api-gateway/src/main/resources/application.yml` define puertos y rutas.

3. **Iniciar servicios** en tres terminales distintas:

   ```bash
   # Auth Service
   cd auth-service
   mvn clean package
   mvn spring-boot:run

   # Inventory Service
   cd ../inventory-service
   mvn clean package
   mvn spring-boot:run

   # API Gateway
   cd ../api-gateway
   mvn clean package
   mvn spring-boot:run
   ```

4. **Verificar**:

   - Auth: `http://localhost:8081/auth/login`
   - Inventory: `http://localhost:8082/api/products`
   - Gateway:   `http://localhost:8080/api/products`

---

## Ejecución con Docker

1. Asegúrate de tener Docker y Docker Compose instalados.
2. En la raíz del repositorio, construye y levanta los contenedores:
   ```bash
   docker-compose build
   docker-compose up -d
   ```
3. El `docker-compose.yml` define:
   - Contenedores `auth-db` y `inventory-db` con Postgres.
   - Servicios `auth-service`, `inventory-service` y `api-gateway`.
4. **Endpoints**:
   - Auth:       `http://localhost:8081/auth/login`
   - Inventory:  `http://localhost:8082/api/products`
   - Gateway:    `http://localhost:8080/api/products`

---

## Limpieza

Para detener y eliminar contenedores y volúmenes:

```bash
docker-compose down -v
```

---

## Notas

- Ajusta contraseñas y variables de entorno según tu entorno.
- Las migraciones Liquibase se ejecutan al iniciar cada microservicio.
- El API Gateway enruta las solicitudes a los puertos 8081 y 8082 desde 8080.

