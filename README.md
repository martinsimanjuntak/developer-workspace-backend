# Developer Workspace Backend

Backend API untuk aplikasi personal `Developer Workspace`.

Project ini dibuat sebagai backend terpisah yang menyediakan REST API untuk frontend Angular. Scope saat ini fokus pada MVP internal/personal, tanpa auth, tanpa multi-user, dan tanpa fitur di luar kebutuhan inti.

## Scope MVP

Module yang sudah tersedia:

- `Task`
  - create task
  - list task
  - detail task
  - update task
  - delete task
  - update task status
- `Work Log`
  - create work log by task
  - list work logs by task
  - update work log
  - delete work log
- `Snippet`
  - create snippet
  - list snippet
  - detail snippet
  - update snippet
  - delete snippet
  - search snippet

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- PostgreSQL JDBC driver for runtime
- H2 for integration tests
- Maven Wrapper

## Project Structure

Project memakai pendekatan package by feature dengan layer yang tetap jelas.

```text
src/main/java/com/id/martin/workspace/martinworkspace/
  common/
  task/
  worklog/
  snippet/
```

Isi utama:

- `common`
  - `BaseEntity`
  - `GlobalExceptionHandler`
  - common error response
- `task`
  - entity, repository, service, controller, DTO
- `worklog`
  - entity, repository, service, controller, DTO
- `snippet`
  - entity, repository, service, controller, DTO

## Configuration

Konfigurasi utama ada di [src/main/resources/application.yaml](src/main/resources/application.yaml).

Default runtime datasource:

- URL: `jdbc:postgresql://localhost:5432/developer_workspace`
- Username: `developer_workspace`
- Password: `Password123`

Environment variables yang bisa dipakai:

- `APP_DATASOURCE_URL`
- `APP_DATASOURCE_USERNAME`
- `APP_DATASOURCE_PASSWORD`

Catatan:

- test memakai H2 in-memory lewat [src/test/resources/application.yaml](src/test/resources/application.yaml)
- saat ini schema masih memakai `spring.jpa.hibernate.ddl-auto=update`
- migration tool belum ditambahkan

## API Endpoints

### Task

- `POST /api/tasks`
- `GET /api/tasks`
- `GET /api/tasks/{taskId}`
- `PUT /api/tasks/{taskId}`
- `DELETE /api/tasks/{taskId}`
- `PATCH /api/tasks/{taskId}/status`

### Work Log

- `POST /api/tasks/{taskId}/work-logs`
- `GET /api/tasks/{taskId}/work-logs`
- `PUT /api/tasks/{taskId}/work-logs/{workLogId}`
- `DELETE /api/tasks/{taskId}/work-logs/{workLogId}`

### Snippet

- `POST /api/snippets`
- `GET /api/snippets`
- `GET /api/snippets/{snippetId}`
- `PUT /api/snippets/{snippetId}`
- `DELETE /api/snippets/{snippetId}`
- `GET /api/snippets?query=keyword`

## Data Model Summary

### Task

- `id`
- `title`
- `description`
- `status`
- `createdAt`
- `updatedAt`

### WorkLog

- `id`
- `taskId`
- `logDate`
- `note`
- `createdAt`
- `updatedAt`

### Snippet

- `id`
- `title`
- `content`
- `category`
- `createdAt`
- `updatedAt`

## Running Locally

Jalankan test:

```bash
./mvnw test
```

Jalankan aplikasi:

```bash
./mvnw spring-boot:run
```

Pastikan PostgreSQL instance tersedia jika ingin menjalankan aplikasi dengan config default.

## Running With Docker Compose

Project backend ini juga sudah punya full stack `docker-compose.yml` untuk:

- PostgreSQL
- Spring Boot backend
- Angular frontend

Build dan jalankan stack:

```bash
docker compose up --build
```

Jalankan stack:

```bash
docker compose up
```

Jalankan di background:

```bash
docker compose up --build -d
```

Stop stack:

```bash
docker compose down
```

Hapus volume database juga:

```bash
docker compose down -v
```

Endpoint local:

- Frontend: `http://localhost:4200`
- Backend API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

Cek status container:

```bash
docker compose ps
```

## Testing Status

Backend sudah memiliki integration test berbasis `MockMvc + H2` untuk:

- Task CRUD + update status
- Work Log CRUD by task
- Snippet CRUD + search
- validation error
- not found handling
- persistence verification

## Current Notes

- backend sudah siap dipakai oleh frontend
- belum ada auth
- belum ada multi-user
- belum ada Dockerfile
- belum ada migration tool
- belum ada success response wrapper global

## Next Recommended Step

Tahap berikut yang paling masuk akal:

1. frontend Angular phase per module
2. tambah migration tool
3. siapkan profile local/prod
4. Dockerize backend
