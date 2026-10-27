# Assessment coverage matrix

| Brief requirement | Implementation | Evidence / how to verify |
|---|---|---|
| Core entities and relationships | Claim, ClaimEvent, WorkloadRecord, ClaimProjection | `docs/architecture-decisions.md` and source packages |
| One or more services, with rationale | Gateway + Claim, Assessment, Workload, Reporting services | Architecture decisions document |
| Sync and event-driven operations | REST commands/queries; Kafka `claim-events`; decision is asynchronous | `docs/api-examples.md` |
| Read/write profiles | Authoritative claim write model plus workload/reporting projections | `docs/architecture-decisions.md` |
| Staff workload retrieval | Workload Service keyed by claim and queried by staff name | `GET /api/staff/workload?staffName=Taylor%20Lee` |
| Lifecycle transitions/business rules | SUBMITTED → UNDER_REVIEW ↔ AWAITING_INFORMATION → SETTLED or REJECTED | `Claim`, `ClaimStatus`, `ClaimLifecycleTest` |
| Outstanding liability exposure | Sum estimate for non-terminal claims | `GET /api/manager/summary` |
| Meaningful Git history | Repository initialised with staged implementation/documentation/test commits | `git log --oneline` |
| Working local app | Docker Compose + Maven commands documented | Must be run on a machine with Java 21, Maven 3.9+, Docker; this environment did not have Maven, so runtime is not claimed as verified |
| AI working journal | `AI_WORKING_JOURNAL.md` | Update it with any further real prompts and decisions before submission |
| Supporting documentation | README, API examples, architecture decisions, walkthrough, checklist | `docs/` |
| Walkthrough | Agenda and demo flow | `docs/walkthrough-guide.md` |

This matrix distinguishes implemented source from verification that must be performed on the candidate's machine. Do not claim a successful build or end-to-end run until you have executed it.
