# 📚 Online Exam Management System

A full-stack web application for managing online exams in an educational environment — built with **Spring Boot** (backend) and **React** (frontend). The system supports three roles (Admin, Professor, Student) with a complete exam lifecycle: course & user management, question banks, exam creation, timed exam-taking, and grading.

> **Note:** I built the **backend** end-to-end (architecture, API, security, database, Docker setup). The **frontend** was built by a teammate as the client for this API — I'm including it here so the project can be run and demoed as a whole, but the frontend code is not my work.

---

## ✨ Features

- **Authentication & Authorization** — JWT-based auth with role-based access control (`ADMIN`, `PROFESSOR`, `STUDENT`)
- **User management** — self-registration with admin approval workflow (pending / approved / rejected)
- **Course management** — admins create courses and assign professors/students to them
- **Question bank** — professors build reusable multiple-choice and descriptive questions
- **Exam builder** — compose exams from the question bank, assign per-question scores
- **Exam taking** — students start timed attempts, save answers as they go, and submit
- **Grading** — professors review submitted attempts and grade descriptive answers
- **API documentation** — interactive Swagger / OpenAPI UI
- **Dockerized** — one command spins up Postgres, Redis, backend, and frontend together

## 🏗️ Tech Stack

**Backend**
- Java 17, Spring Boot 3.2
- Spring Security + JWT (`jjwt`) for stateless authentication
- Spring Data JPA / Hibernate
- PostgreSQL (primary database)
- Redis (caching / session-adjacent data)
- MapStruct (DTO ↔ entity mapping)
- springdoc-openapi (Swagger UI)
- Maven
- Docker / Docker Compose

**Frontend**
- React 18 + React Router
- Vite

## 🗂️ Project Structure

```
.
├── backend/    # Spring Boot REST API (my work) — includes Dockerfile + docker-compose.yml
└── frontend/   # React client (built by a teammate) — includes Dockerfile
```

## 🔐 Roles & Access

| Role      | Can do |
|-----------|--------|
| **Admin**     | Approve/reject users, manage users & courses, assign professors/students to courses |
| **Professor** | Manage own courses' exams, build questions, create/edit exams, grade student attempts |
| **Student**   | View enrolled courses, take exams, save progress, submit attempts |

Endpoints are locked down by role at the security-filter level (e.g. `/admin/**` → `ADMIN` only, `/student/**` → `STUDENT`/`ADMIN`, etc.), on top of a stateless JWT filter that authenticates every request.

## 🚀 Getting Started

### Option A — Docker Compose (recommended)

The whole stack (PostgreSQL, Redis, backend, frontend) is wired up with Docker Compose, so you don't need to install Java, Postgres, Redis, or Node locally — just Docker.

```bash
cd backend/OnlineExamManagement
docker compose up --build
```

This starts:

| Service | URL |
|---|---|
| Backend API | `http://localhost:8080` (Swagger UI at `/swagger-ui.html`) |
| Frontend | `http://localhost:5173` |
| PostgreSQL | `localhost:5433` (mapped from container port 5432) |
| Redis | `localhost:6379` |

All the wiring (DB/Redis connection info, JWT secret, and the frontend's `VITE_API_BASE_URL` pointing at the backend) is already configured in `docker-compose.yml` — `docker compose up` gives you a fully working, seeded instance with no manual setup.

On first run, the app seeds a default admin account and sample students so you can log in immediately:

- **Admin:** `admin@gmail.com` / `admin123`

### Option B — Running services manually

If you'd rather run things outside Docker:

**Prerequisites:** Java 17+, Maven, PostgreSQL, Redis, Node.js 18+

**Backend**

1. Create a PostgreSQL database.
2. Set the required environment variables (or use the defaults below):

   | Variable | Default |
      |---|---|
   | `DB_HOST` | `localhost` |
   | `DB_PORT` | `5432` |
   | `DB_NAME` | `online_exam_management2` |
   | `DB_USERNAME` | `postgres` |
   | `DB_PASSWORD` | `root` |
   | `REDIS_HOST` | `127.0.0.1` |
   | `REDIS_PORT` | `6379` |
   | `JWT_SECRET` | *(required, no default)* |
   | `JWT_EXPIRATION` | *(required, no default)* |

3. Run the app:
   ```bash
   cd backend/OnlineExamManagement
   mvn spring-boot:run
   ```
4. The API is available at `http://localhost:8080`, with Swagger UI at `http://localhost:8080/swagger-ui.html`.

**Frontend**

```bash
cd frontend
npm install
npm run dev
```

The dev server will point the client at the backend API — make sure the backend is running first.

## 📡 API Overview

| Area | Base path | Notes |
|---|---|---|
| Auth | `/auth` | Register, login |
| Admin | `/admin` | Approve/reject users, manage users & course assignments |
| Courses | `/admin/courses`, `/courses` | CRUD + browsing |
| Questions | `/questions` | Question bank CRUD, attach to exams, scoring |
| Exams | `/exams` | Create/update/delete exams, browse by course |
| Student exams | `/student/exams` | Start/resume attempts, save answers, submit |
| Professor exams | `/professor/exams` | View participants, grade answers |

Full request/response schemas are available via Swagger UI once the backend is running.

## 🧠 What I Focused On

- Designing a clean layered architecture (Controller → Service → Repository) with DTOs and MapStruct mappers instead of exposing entities directly
- A stateless JWT auth flow with a custom filter and role-based method/URL security
- Modeling the exam domain (courses, exams, question types, attempts, answers, grading) with proper relational constraints
- Centralized exception handling with meaningful HTTP error responses
- API documentation via OpenAPI/Swagger
- Containerizing the whole stack with Docker Compose for one-command setup

## 📄 License

This project was built as a final/portfolio project and is shared for demonstration purposes.
