# AI working journal

This is a living record. Keep entries factual and add new entries as you use AI. The entries below record the project decisions made while preparing this version; they do not claim that every line was manually written or that the build has passed.

## Entry 1 — Assessment brief and original architecture
- **Prompt/request:** Build a Java backend project addressing the supplied claims assessment brief and its deliverables.
- **Initial approach:** Separate Spring Boot services with Kafka events, a source-of-truth Claim Service, and separate workload/reporting projections.
- **Accepted:** Java 21, Spring Boot, REST for client-facing commands/queries, Kafka for asynchronous event propagation, explicit lifecycle states, API examples, architecture decisions, and walkthrough guide.
- **Qualification:** A modular monolith can be the simpler default for a small time-boxed assessment. The original multi-service variant demonstrated independent boundaries but required multiple processes and ports.

## Entry 2 — Consolidation to one Java application
- **Prompt/request:** Consolidate the individual services into a single Java application with one `pom.xml`, one main class, and Kafka integration using `KafkaTemplate`.
- **Accepted:** One Maven project, one `ClaimsApplication` entry point, one HTTP port (`8080`), one H2 in-memory database, and package-level boundaries for claims, assessment, workload, reporting, and events.
- **Retained:** `KafkaTemplate` publishes decision events; distinct `@KafkaListener` consumer groups apply decisions and update workload/reporting projections asynchronously.
- **Trade-off:** This is a modular monolith, not independently deployable microservices. It simplifies local setup but removes independent deployment, scaling, and process isolation.
- **Verification status:** Maven was unavailable in the authoring environment. Run `mvn clean verify`, start Kafka and the application, exercise the full API flow, and record actual results before submission.

## Candidate verification log
Replace this section with the actual results after running the project. Do not claim a successful build or end-to-end flow until verified.
