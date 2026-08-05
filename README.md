# ClaimFlow

ClaimFlow is a small event-driven insurance platform built with Java 17 and Spring Boot. It accepts
claims over REST, exposes the claim read model through REST and GraphQL, publishes lifecycle events,
and turns those events into a notification feed.

The codebase contains two independently runnable services and one shared contract module:

| Module | Purpose | Port |
| --- | --- | ---: |
| `common` | Event records, lifecycle enums, and the shared HTTP error shape | — |
| `claims-service` | Claim intake, lookup, approval, rejection, REST, and GraphQL | 8080 |
| `notification-service` | Claim-event projection and notification API | 8081 |

## How it fits together

```text
                                    claim.submitted
REST / GraphQL                      claim.approved
      clients                       claim.rejected
         │                                │
         ▼                                ▼
┌──────────────────────┐          ┌──────────────┐          ┌──────────────────────┐
│ claim-intake-service │ ────────►│ Spring/Kafka │────────► │ notification-service │
│ REST + GraphQL + H2  │          │ event bridge │          │ in-memory projection │
└──────────────────────┘          └──────────────┘          └──────────────────────┘
```

The default profile uses Spring application events and needs no external infrastructure. The
`kafka` profile uses Apache Kafka and is the real cross-process service-to-service path. Both
transports implement the same event gateway, so workflow code is transport-agnostic.

## Requirements

- JDK 17 or newer
- Maven 3.9 or newer
- Docker with Compose, only for the Kafka stack

## Build and test

Run the complete reactor build from the repository root:

```bash
mvn verify
```

The suite covers lifecycle rules, event dispatch, REST validation/error responses, application
startup, ordered notification storage, and the local event-to-notification projection.

## Run without Kafka

Start each service in its own terminal:

```bash
mvn -pl claims-service spring-boot:run
```

```bash
mvn -pl notification-service spring-boot:run
```

This mode is useful for developing either service in isolation. Because application events do not
cross JVM boundaries, use the Docker/Kafka mode below to exercise the complete two-service flow.

## Run the complete stack

```bash
docker compose up --build
```

Compose starts ZooKeeper, Kafka, the intake service on `localhost:8080`, and the notification
service on `localhost:8081`. Both applications are automatically given the `kafka` profile.

Stop the stack with `docker compose down`.

## Claim REST API

Base URL: `http://localhost:8080/api/claims`

| Method | Route | Result |
| --- | --- | --- |
| `POST` | `/api/claims` | File a claim (`201 Created`) |
| `GET` | `/api/claims` | List claims newest first |
| `GET` | `/api/claims?status=SUBMITTED` | Filter by lifecycle status |
| `GET` | `/api/claims/{id}` | Fetch one claim |
| `POST` | `/api/claims/{id}/approve` | Approve a submitted claim |
| `POST` | `/api/claims/{id}/reject` | Reject a submitted claim |

Supported claim types are `AUTO`, `HOME`, `HEALTH`, `LIFE`, and `TRAVEL`. A claim can move from
`SUBMITTED` to either `APPROVED` or `REJECTED`; completed claims cannot transition again.

File a claim:

```bash
curl --request POST http://localhost:8080/api/claims \
  --header 'Content-Type: application/json' \
  --data '{
    "policyNumber": "POL-44218",
    "claimantName": "Jane Doe",
    "type": "AUTO",
    "description": "Rear-end collision",
    "amount": 4200.00
  }'
```

Approve it:

```bash
curl --request POST http://localhost:8080/api/claims/1/approve
```

Or reject it while it is still submitted:

```bash
curl --request POST http://localhost:8080/api/claims/1/reject \
  --header 'Content-Type: application/json' \
  --data '{"reason":"Missing police report"}'
```

Validation failures use HTTP 400, unknown ids use 404, and invalid lifecycle changes use 409. All
three return the same shape:

```json
{
  "timestamp": "2026-10-29T18:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Cannot approve claim CLM-12345678 in status APPROVED",
  "path": "/api/claims/1/approve"
}
```

## GraphQL API

Send queries to `POST http://localhost:8080/graphql`. The interactive GraphiQL page is available at
`http://localhost:8080/graphiql`.

```graphql
query ClaimOverview {
  claims {
    id
    claimNumber
    claimantName
    type
    amount
    status
  }
}
```

Fetch one record with `claim(id: 1)`.

## Notification API

After filing or adjudicating claims in the Kafka stack, inspect the resulting projection:

```bash
curl http://localhost:8081/api/notifications
```

Example response:

```json
[
  {
    "type": "CLAIM_SUBMITTED",
    "claimNumber": "CLM-12345678",
    "message": "Claim CLM-12345678 submitted by Jane Doe",
    "receivedAt": "2026-10-29T18:00:00Z"
  }
]
```

The notification projection is intentionally in memory and is cleared whenever its service
restarts. Claim data uses an in-memory H2 database and is likewise intended for demonstration and
local development.

## Source layout

```text
common/src/main/java/io/claimflow/contracts/
├── events/       # transport-neutral lifecycle event records
├── http/         # shared REST error document
└── model/        # coverage and processing enums

claims-service/src/main/java/io/claimflow/intake/
├── api/          # request commands and response view
├── application/  # lifecycle orchestration
├── errors/       # application failures
├── graphql/      # GraphQL query adapter
├── messaging/    # local and Kafka event gateways
├── model/        # JPA aggregate
├── persistence/  # Spring Data repository
└── web/          # REST adapter and error mapping

notification-service/src/main/java/io/claimflow/notifications/
├── application/  # notification ledger and event projector
├── messaging/    # local and Kafka consumers
├── model/        # notification read model
└── web/          # notification REST adapter
```

## Configuration

The default H2 database and service ports are defined in each module's `application.yml`.
Kafka-specific serialization is isolated in `application-kafka.yml`. Override the broker address
with `KAFKA_BOOTSTRAP_SERVERS`; it defaults to `localhost:9092` outside Docker.

The GitHub Actions workflow runs `mvn -B verify` on JDK 17 for pushes and pull requests to `main`.
