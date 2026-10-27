# Architecture and trade-offs — single-application variant

## Deployment shape
This variant is a modular monolith: one Spring Boot process, one executable JAR, one REST port (`8080`), one H2 database, and one Kafka broker dependency. Package boundaries keep responsibilities understandable without the operational cost of running five JVMs. The modules are not independently deployable or independently scalable.

## Module responsibilities
- **Claim package** owns the authoritative claim entity, lifecycle rules, REST endpoints, Kafka event publishing, and application of decision events.
- **Assessment package** validates a decision request and publishes `DECISION_RECORDED` using `KafkaTemplate`.
- **Workload package** maintains the staff workload projection through a Kafka listener.
- **Reporting package** maintains the manager outstanding-claim/liability projection through a Kafka listener.
- **Events package** defines the event contract shared by producers and consumers.

## Why REST + Kafka in one app?
REST provides immediate responses for claim commands and queries. Kafka remains the asynchronous integration boundary for decision application and read-model updates. The decision endpoint returns `202 Accepted`; the authoritative status and read projections update after consumers process the event. This demonstrates asynchronous event processing without requiring multiple local application processes.

## Event contract
`ClaimEvent` includes eventId, type, claimId, status, estimatedLiability, assignedStaff, decision, settlementAmount, and occurredAt. A single topic (`claim-events`) is used for this prototype. Each listener has a distinct consumer group so all listeners receive the events independently.

## Consistency and reliability
Read models are eventually consistent. The prototype does not implement a transactional outbox, deduplication, dead-letter queue, schema registry, or exactly-once processing. Production improvements would include an outbox relay, idempotent consumers keyed by eventId, retries/DLQ, event versioning, durable database, migrations, tracing and metrics.

## Trade-offs
A modular monolith is simpler to run, debug, and explain in a short assessment. Its disadvantages are shared deployment/scaling and a larger failure domain. Kafka still introduces asynchronous delivery and eventual consistency even though producers and consumers run in the same process. If the assessment specifically expects independently deployed microservices, use the multi-module version instead and explain the additional operational cost.
