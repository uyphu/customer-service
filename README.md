# 🧾 Customer Service API

This project is a **Spring Boot REST API** for managing customers, including operations like **Create**, **Read**, **Update**, **Delete**, and **tier calculation** based on `annualSpend` and `lastPurchaseDate`.

---

## 📦 Features

- Create customer with unique name and email
- Retrieve customer by ID, name, email, or both
- Update and delete customers with validation
- Tier assignment logic:
  - **Silver**: Default or spend < $1000
  - **Gold**: $1000 ≤ spend < $10,000 and purchase in last 12 months
  - **Platinum**: spend ≥ $10,000 and purchase in last 6 months
- Global exception handling
- Logging to file and console
- Prometheus health endpoints
- Full unit test coverage with JaCoCo and Postman collection

---

## ⚙️ Tech Stack

- Java 17
- Spring Boot 3
- H2 in-memory database
- Spring Data JPA
- Jakarta Validation
- Lombok
- JUnit 5 + Mockito
- Logback
- JaCoCo
- OpenAPI / Swagger
- Prometheus (optional)
- Postman (for API tests)

---

## 🚀 Getting Started

### 🛠 Prerequisites

- Java 17+
- Maven

### 🧪 Run Install

```bash
./mvnw clean install
```

### 🧪 Run Tests

```bash
./mvnw clean test
```

### ▶️ Start Application

```bash
./mvnw spring-boot:run
```
### 🧪 Swagger UI
- Interactive API documentation:
📍 http://localhost:8080/swagger-ui/index.html

- OpenAPI spec:
📄 http://localhost:8080/v3/api-docs

### 💾 H2 Console
In-memory database access:
🌐 http://localhost:8080/h2-console

- Configuration:
- JDBC URL: jdbc:h2:mem:customerdb
- Username: sa
- Password: (leave blank)

### 📤 API Endpoints

| Method | Endpoint                 | Description           |
|--------|--------------------------|-----------------------|
| POST   | `/customers`             | Create new customer   |
| GET    | `/customers/{id}`        | Get customer by ID    |
| GET    | `/customers?name=...`    | Get customer by name  |
| GET    | `/customers?email=...`   | Get customer by email |
| PUT    | `/customers/{id}`        | Update customer       |
| DELETE | `/customers/{id}`        | Delete customer       |
### 📁 Logging
- Logs are stored at: logs/customer-service.log

### 📈 Health Check & Metrics
- Health: GET /actuator/health
- Info: GET /actuator/info
- Prometheus: GET /actuator/prometheus

### 📬 Postman Collection
- postman/Customer-API-Test-Cases.postman_collection.json

### ✅ Test Coverage (JaCoCo)
- Generate the code coverage report:
```bash
./mvnw clean test jacoco:report
```
- View the HTML report:
```bash
open target/site/jacoco/index.html
```

### 👨‍💻 Author
#### Phu Le
📫 Task for assignment June 2nd, 2025
