# Git workflow
This source archive has no fabricated Git history. After reviewing the code:
```bash
git init
git add pom.xml common-events
git commit -m "Add shared claim event contract"
git add claim-service
 git commit -m "Implement claim lifecycle service and Kafka events"
git add assessment-service workload-service reporting-service
 git commit -m "Add assessment, workload and reporting consumers"
git add api-gateway docker-compose.yml docs README.md AI_WORKING_JOURNAL.md
 git commit -m "Add gateway, local Kafka setup and assessment documentation"
```
Make commits that reflect the work you actually perform; adapt this sequence as needed.
