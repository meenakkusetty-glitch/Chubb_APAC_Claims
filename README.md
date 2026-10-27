# Chubb APAC Claims — Single Spring Boot Application

A backend-only Java 21 / Spring Boot assessment prototype. This version consolidates the earlier multi-module/multi-process project into **one Maven project, one Spring Boot entry point, one HTTP port, and one H2 database**, while retaining Kafka-based asynchronous decision processing and event-driven read projections.

> This is a **modular monolith**, not independently deployable microservices. Package boundaries preserve the claim, assessment, workload, reporting, and event responsibilities. Kafka remains a real external dependency and must be running for event publishing/consumption.

## Architecture

- `com.chubb.claims.claim`: authoritative claim record, lifecycle rules, REST API, event publisher, decision event consumer.
- `com.chubb.claims.assessment`: decision validation and asynchronous decision-event publishing using `KafkaTemplate`.
- `com.chubb.claims.workload`: staff workload projection updated by `@KafkaListener`.
- `com.chubb.claims.reporting`: manager outstanding-claim/liability projection updated by `@KafkaListener`.
- `com.chubb.claims.events`: shared Kafka event record.
- `ClaimsApplication.java`: the only Spring Boot `main` class.

REST runs on `http://localhost:8080`. All persistence uses one H2 in-memory database; the app's tables remain separate, but data resets when the application restarts. Kafka topic: `claim-events`. Consumer groups remain distinct so each projection and the claim decision handler independently receives the events.

## Prerequisites

- JDK 21+
- Maven 3.9+
- Docker with Docker Compose

## Run

From this folder:

```bash
docker compose up -d
mvn clean verify
mvn spring-boot:run
```

Or build and run the single executable JAR:

```bash
mvn clean package
java -jar target/chubb-claims-application.jar
```

Stop Kafka with `docker compose down`.

## API walkthrough

Create a claim:

```bash
curl -i -X POST http://localhost:8080/api/claims \
  -H 'Content-Type: application/json' \
  -d '{"claimantName":"Alex Smith","incidentDescription":"Vehicle damage","estimatedLiability":12500}'
```

Copy the returned claim `id` and use it as `{id}`:

```bash
curl http://localhost:8080/api/claims/{id}
curl -X POST http://localhost:8080/api/claims/{id}/assign \
  -H 'Content-Type: application/json' -d '{"staffName":"Taylor Lee"}'
curl -X POST http://localhost:8080/api/claims/{id}/information-requests
curl -X POST http://localhost:8080/api/claims/{id}/information
curl -X POST http://localhost:8080/api/assessments/{id}/decision \
  -H 'Content-Type: application/json' -d '{"decision":"SETTLED","settlementAmount":11000}'
curl 'http://localhost:8080/api/staff/workload?staffName=Taylor%20Lee'
curl http://localhost:8080/api/manager/summary
```

The decision endpoint returns `202 Accepted`; the claim status changes after the Kafka listener consumes `DECISION_RECORDED`. Workload and reporting projections are also eventually consistent, so their responses can briefly lag behind the command API.

## Lifecycle rules

`SUBMITTED → UNDER_REVIEW ↔ AWAITING_INFORMATION → SETTLED | REJECTED`

- Assignment moves a submitted claim into review.
- Information can be requested only during review; providing it returns the claim to review.
- Decisions are permitted only in `UNDER_REVIEW`.
- `SETTLED` requires a non-negative settlement amount; `REJECTED` must not include one.
- Settled and rejected claims are terminal.
- Manager exposure is the sum of estimated liability for non-terminal claims. This is a prototype simplification, not an actuarial reserve calculation.

## Design trade-offs and limitations

Consolidation simplifies local setup and debugging but removes independent deployment, scaling, and failure isolation. Kafka still demonstrates asynchronous event-driven processing, though an in-process event handler would be simpler if asynchronous integration were not a requirement. This prototype has no authentication, durable database, schema migrations, transactional outbox, idempotency, retry/DLQ strategy, event schema registry, audit trail, or production-grade telemetry. Kafka is configured for local plaintext development.

## Before submission

Run `mvn clean verify`, start Kafka and the application, execute the full API flow, and record the **actual** results in `AI_WORKING_JOURNAL.md`. The project was assembled in an environment where Maven was unavailable, so the build and end-to-end Kafka flow have **not been verified here**. Review and understand every line before presenting it.
