# Architecture decisions and trade-offs — single-application variant

## Scope and assumptions
This is a time-boxed assessment implementation, not a production insurance platform. A claim has one claimant, one estimated liability amount in a single assumed currency, an optional assigned officer, and one terminal decision. `SETTLED` and `REJECTED` are terminal. Settlement requires a non-negative amount; rejection forbids a settlement amount. Only claims in `UNDER_REVIEW` can be decided. Providing requested information returns the claim to review.

## Domain model
- **Claim**: authoritative lifecycle record, estimate, assignment, decision and timestamps.
- **ClaimEvent**: integration event with event ID, type, claim ID, state snapshot and occurrence time.
- **WorkloadRecord**: projection optimised for staff workload lookup.
- **ClaimProjection**: projection optimised for management status counts and outstanding liability.

## Why a modular monolith?
The original version separated these responsibilities into multiple Spring Boot processes. This variant intentionally consolidates them into one Maven project and one application entry point to simplify setup and local demonstration. Package boundaries still separate claim lifecycle, assessment, workload, reporting, and event contracts. This is a reasonable time-boxed choice when independent deployment/scaling is not required. It trades away process isolation and independent scaling.

## Synchronous versus asynchronous
REST is used for claim submission, assignment, information request/response, claim lookup, decision submission, workload queries, and manager queries. The assessment endpoint validates the current claim state using the shared repository and returns **202 Accepted** after publishing a decision event. The claim decision listener applies that event asynchronously. Workload and reporting projections also update asynchronously and can briefly lag behind the authoritative claim record.

## Data storage
All entities use a single H2 in-memory database for easy local setup. Entity tables remain separate, but the database is shared and data resets when the application restarts. A durable database and schema migrations would be needed beyond the assessment prototype.

## Liability exposure
Outstanding liability is the sum of `estimatedLiability` for claims whose status is not `SETTLED` or `REJECTED`. This is an explicit simplifying assumption; a real insurer would distinguish reserves, recoveries, currency, policy limits, and paid amounts. The manager response labels the projection as eventually consistent.

## Reliability decisions and production gaps
Kafka events are published after the claim save, so a crash between those operations can lose an event. Production improvements include a transactional outbox, idempotent consumers, event schema/versioning, retry/backoff and dead-letter topics, correlation IDs, metrics/tracing, durable storage, migrations, and authentication/authorisation. Kafka is configured for local plaintext development only.

## Alternatives considered
1. **Modular monolith (selected for this variant):** simplest local execution and one deployable artifact; no independent scaling or process isolation.
2. **Multiple microservices (previous variant):** clearer independent ownership/scaling, but more ports, network calls, deployment steps, and failure modes.
3. **Synchronous decision update:** simpler immediate consistency, but does not demonstrate Kafka-driven asynchronous decision application. The event-based path is retained because Kafka is part of the platform context.
