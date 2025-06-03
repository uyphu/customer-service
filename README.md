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
- Tracing with `traceId` and `spanId` using Spring Sleuth
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
- Sleuth (for tracing)
- Postman (for API tests)

---

## 🚀 Getting Started

### 🛠 Prerequisites

- Java 17+
- Maven

### 🧪 Run Tests

```bash
mvn clean test
