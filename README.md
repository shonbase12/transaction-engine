# NovaPay Transaction Engine

A backend transaction processing engine for the NovaPay payment platform, responsible for validating, routing, and persisting payment transactions.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Key Components](#key-components)
- [Logging & Observability](#logging--observability)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

The Transaction Engine handles the core lifecycle of a payment transaction — from initial validation through gateway communication to final persistence. It is designed for reliability, auditability, and extensibility.

---

## Architecture
PaymentController (HTTP)
│
▼
PaymentService ──► TransactionRepository (DB)
│
▼
PaymentGatewayClient (External PSP)
│
▼
AuditService (Audit log)

---

## Project Structure
transaction-engine/
├── src/                        # Main source code
├── docs/                       # Design docs and API references
├── PaymentService.java         # Payment processing logic
├── TokenService.java           # Token issuance and validation
├── logback.xml                 # Logback configuration
├── logback-unified.xml         # Unified logging profile
└── LOGGING_AND_OBSERVABILITY_STANDARDS.md

---

## Prerequisites

- Java 17+
- Maven 3.8+

---

## Getting Started

```bash
# Clone the repository
git clone https://github.com/shonbase12/transaction-engine.git
cd transaction-engine

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

---

## Key Components

### PaymentService

Coordinates the payment flow end-to-end.

| Method | Description |
|---|---|
| `processPayment(Transaction)` | Validates and processes a transaction |
| `refundPayment(String transactionId)` | Initiates a refund for an approved transaction |

**Dependencies:** `TransactionRepository`, `PaymentGatewayClient`, `AuditService`

### TokenService

Handles issuance and validation of payment tokens used for secure transaction references.

---

## Logging & Observability

This project follows structured logging standards documented in [`LOGGING_AND_OBSERVABILITY_STANDARDS.md`](./LOGGING_AND_OBSERVABILITY_STANDARDS.md).

Two Logback profiles are provided:
- `logback.xml` — default development profile
- `logback-unified.xml` — unified profile for production/staging environments

---

## Contributing

1. Fork the repository and create a new branch:
```bash
   git checkout -b feat/your-feature-name
```
2. Make your changes with clear, focused commits.
3. Open a pull request with a description of what you changed and why.
4. Link any relevant issue (e.g. `Closes #51`).

---

## License

This project is licensed under the MIT License.