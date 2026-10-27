# API examples

Create claim:
```http
POST /api/claims
Content-Type: application/json
{"claimantName":"Alex Smith","incidentDescription":"Vehicle damage","estimatedLiability":12500}
```
Assign staff:
```http
POST /api/claims/{claimId}/assign
Content-Type: application/json
{"staffName":"Taylor Lee"}
```
Request more information: `POST /api/claims/{claimId}/information-requests`
Record information received: `POST /api/claims/{claimId}/information`
Submit decision:
```http
POST /api/assessments/{claimId}/decision
Content-Type: application/json
{"decision":"SETTLED","settlementAmount":11000}
```
Decision endpoint returns 202 Accepted. Poll `GET /api/claims/{claimId}` until the event is processed. Staff and manager projections may also need a short delay to update.
