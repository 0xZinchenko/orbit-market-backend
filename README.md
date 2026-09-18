# Orbit Market

[![Build and Test Coverage](https://github.com/0xZinchenko/Cosmo-Cats-Intergalactic-Marketplace/actions/workflows/pull_request.yml/badge.svg)](https://github.com/0xZinchenko/Cosmo-Cats-Intergalactic-Marketplace/actions/workflows/pull_request.yml)

A backend for a marketplace platform, built to demonstrate production-grade Spring Boot architecture rather than a typical CRUD tutorial: layered validation, resilient third-party API integration, feature toggles via AOP, a normalized relational schema with versioned migrations, and a test pyramid backed by real infrastructure (PostgreSQL, WireMock) through Testcontainers.

## Tech stack

- **Java 21**, **Spring Boot 4**
- **Spring Data JPA** + **PostgreSQL**, schema managed via **Liquibase**
- **Spring AOP** for cross-cutting concerns (feature toggles)
- **MapStruct** for entity/DTO mapping
- **RestClient** for outbound HTTP integration
- **JUnit 5**, **Mockito**, **AssertJ**, **Testcontainers** (PostgreSQL, WireMock), **JaCoCo**
- **Docker Compose** for local infrastructure
- **GitHub Actions** CI with an enforced coverage gate

## Architecture highlights

- **Package-by-feature** structure (`product`, `category`, `order`, `cart`, `currency`, `feature`) instead of package-by-layer — each feature owns its controller, service, repository, DTOs and exceptions.
- **RFC 9457 Problem Details** for all error responses, with a centralized `GlobalExceptionHandler` mapping domain exceptions to the correct HTTP status (404, 400, 403, 502).
- **Anti-corruption layer** around the third-party currency-conversion API: an internal DTO decouples the app's stable contract from the upstream provider's actual JSON shape.
- **Feature toggles via Spring AOP** — a custom `@RequiresFeature` annotation and an `@Around` aspect gate functionality based on environment configuration, throwing a typed exception (mapped to `403`) when a feature is disabled.
- **Normalized (3NF) schema** with `order_items`/`cart_items` as first-class entities (not a bare many-to-many) so per-line quantity has somewhere to live, plus category-scoped uniqueness constraints and indexes — all version-controlled through Liquibase changelogs.
- **Natural ID** — orders are also addressable by a business-meaningful `orderNumber`, using Hibernate's `@NaturalId` and `Session.byNaturalId()` alongside the surrogate primary key.
- **JPQL projections** — a "most purchased products" report built with interface-based projections and aggregate JPQL queries (`GROUP BY` / `ORDER BY` / `WHERE`), avoiding native SQL.
- **Transactional service layer** — read-only by default at the class level, with write operations explicitly opted into read-write transactions.

## Running locally

```bash
docker compose up -d      # starts PostgreSQL
./gradlew bootRun          # runs the app; Liquibase applies the schema on startup
```

API docs are served via Swagger UI at `/swagger-ui.html` once the app is running.

## Example

Create a product:

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Galaxy Cat Plush", "price": 19.99}'
```

```json
{
  "id": 1,
  "name": "Galaxy Cat Plush",
  "price": 19.99,
  "categoryId": null
}
```

Get its price converted to another currency (live exchange rate, fetched from a third-party API):

```bash
curl http://localhost:8080/api/v1/products/1/price?currency=EUR
```

```json
{
  "productId": 1,
  "originalPrice": 19.99,
  "currency": "EUR",
  "convertedPrice": 18.39
}
```

## Testing

```bash
./gradlew build
```

Runs the full suite (unit + integration) and enforces per-file JaCoCo coverage thresholds. Integration tests spin up real, disposable infrastructure through Testcontainers — PostgreSQL for repository/CRUD tests, WireMock for the external currency API — so tests exercise real SQL and real HTTP behavior instead of mocks alone.

## Roadmap

- Spring Security with OAuth2 login and API-key authentication for service-to-service calls
- Kafka-based eventing between services
