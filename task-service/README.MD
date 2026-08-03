# Microservices Demo: Java + Go

A small portfolio project showing two microservices in different languages
talking to each other over REST/HTTP.

```
                 POST /api/tasks
   ┌────────┐  ──────────────────►  ┌──────────────────┐
   │ client │                       │  task-service     │  (Java / Spring Boot, :8080)
   └────────┘  ◄──────────────────  │  - owns task data │
                     Task JSON      │  - business logic │
                                    └─────────┬─────────┘
                                              │ POST /webhook/notify
                                              │ (on create / complete)
                                              ▼
                                    ┌──────────────────────┐
                                    │ notification-service │  (Go, :8081)
                                    │ - simulates sending  │
                                    │   email/SMS          │
                                    └──────────────────────┘
```

## Services

### `task-service` (Java 21 / Spring Boot 3.3)
Owns the task domain. In-memory store (swap for JPA/Mongo later if you want
persistence). On task creation and completion it calls the Go service via
`RestClient`, but failures there never break the task workflow — notification
is a side effect, not a source of truth.

REST API:
| Method | Path                        | Description         |
|--------|-----------------------------|---------------------|
| POST   | `/api/tasks`                | Create a task       |
| GET    | `/api/tasks`                | List tasks          |
| GET    | `/api/tasks/{id}`           | Get one task        |
| PATCH  | `/api/tasks/{id}/complete`  | Mark task done      |
| DELETE | `/api/tasks/{id}`           | Delete a task       |

### `notification-service` (Go 1.26, stdlib only)
Stateless webhook receiver. Logs the event and returns a simulated delivery
confirmation — swap in a real email/SMS provider call later.

| Method | Path                | Description               |
|--------|---------------------|----------------------------|
| GET    | `/health`             | Health check                |
| POST   | `/webhook/notify`     | Receive a notification event |

## Running locally (without Docker)

Terminal 1 — Go service:
```bash
cd notification-service
go run main.go
```

Terminal 2 — Java service:
```bash
cd task-service
mvn spring-boot:run
```

Then:
```bash
curl -X POST localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Write report","description":"Q3 summary"}'

curl -X PATCH localhost:8080/api/tasks/1/complete
```

Watch the Go service's stdout — you'll see the `TASK_CREATED` and
`TASK_COMPLETED` events land as webhook calls.

## Running with Docker Compose

```bash
docker compose up --build
```

This builds both images and wires them together on the same Docker network;
`task-service` talks to `notification-service` by container name.
