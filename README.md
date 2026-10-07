# atlas-bank-services

Ledger transaccional fintech con microservicios event-driven. Proyecto de portafolio: contabilidad de doble entrada inmutable, saga orquestada con compensación, idempotencia, outbox y event sourcing.

Plan completo y decisiones: [`docs/`](docs/Plan%20de%20Portafolio%20Ledger%20Transaccional%20Fintech%20(Microservicios).md).

## Servicios

| Servicio | Puerto | Base de datos (host) | Rol |
| --- | --- | --- | --- |
| `api-gateway` | 18080 | — | Entrada única, routing hacia el orquestador |
| `transaction-orchestrator` | 18081 | `localhost:15434` | Saga de transacciones y su estado |
| `ledger-service` | 18082 | `localhost:15432` | Dueño exclusivo del ledger (event store + balance) |
| `risk-service` | 18083 | `localhost:15433` | Límites y blacklist |
| `notification-service` | 18084 | — | Consumidor Kafka de eventos de dominio |

Cada servicio es dueño exclusivo de su base de datos. Las migraciones se gestionan con **Liquibase** (`src/main/resources/db/changelog/`) y se aplican al arrancar.

Kafka queda en `localhost:19092` y Schema Registry en `localhost:8085`. Los puertos del host usan rangos altos para no chocar con servicios locales comunes.

## Stack

Java 21, Spring Boot 3.5, Gradle (multi-módulo), PostgreSQL 16, Liquibase, Kafka, Resilience4j, Spring Cloud Gateway, JUnit 5, ArchUnit.

## Requisitos

- JDK 21
- Docker (para Postgres y Kafka)

El wrapper de Gradle está incluido; no hace falta instalar Gradle.

## Uso

```bash
# infraestructura local
docker compose up -d

# compilar y correr tests
./gradlew build

# levantar un servicio (aplica sus migraciones Liquibase al arrancar)
./gradlew :ledger-service:bootRun
```

Salud de cada servicio: `http://localhost:<puerto>/actuator/health`.

## Arquitectura del código

`ledger-service` sigue arquitectura hexagonal: el paquete `domain` no depende de Spring ni de JPA (verificado con ArchUnit en `HexagonalArchitectureTest`). Dominio actual: `Money`, `LedgerEntry`, `Journal` (asiento balanceado por moneda) y `Account` (balance por replay de apuntes).

La tabla `ledger_entries` es append-only a nivel de base de datos: un trigger rechaza `UPDATE` y `DELETE`.

## Estado

Bases listas (build, migraciones, dominio inicial del ledger, Compose). Siguiente: capas `application` e `infrastructure` de `ledger-service` (paso 1 del roadmap).
