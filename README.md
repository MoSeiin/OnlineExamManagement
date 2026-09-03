# 📚 Online Exam Management System

A full-stack web application for managing online examinations in an educational environment, built with **Spring Boot** and **React**.

The system supports three roles — **Admin, Professor, and Student** — and covers the complete exam lifecycle, including user and course management, question banks, exam creation, timed exam attempts, answer submission, and grading.

> **My contribution:** I developed the backend end-to-end, including the application architecture, REST APIs, authentication and authorization, database design, Redis integration, exception handling, and Docker configuration. The frontend was developed by a teammate as the client application for the backend API.

---

## ✨ Features

* 🔐 **Authentication & Authorization** — JWT-based authentication with role-based access control (`ADMIN`, `PROFESSOR`, `STUDENT`)
* 👥 **User Management** — Registration with an admin approval workflow
* 📚 **Course Management** — Create courses and assign professors and students
* ❓ **Question Bank** — Create and manage multiple-choice and descriptive questions
* 📝 **Exam Builder** — Create exams from reusable questions and configure question scores
* ⏱️ **Timed Exams** — Students can start exams, save their answers, and submit attempts
* 💾 **Redis Integration** — Temporary storage of exam attempt answers and progress
* 📊 **Grading** — Automatic handling of objective questions and manual grading for descriptive answers
* 📖 **API Documentation** — Interactive Swagger / OpenAPI documentation
* 🐳 **Dockerized** — PostgreSQL, Redis, backend, and frontend can be started together using Docker Compose

---

## 🏗️ Tech Stack

### Backend

* **Java 17**
* **Spring Boot 3.2**
* **Spring Security**
* **JWT (JJWT)**
* **Spring Data JPA / Hibernate**
* **PostgreSQL**
* **Redis**
* **MapStruct**
* **Springdoc OpenAPI / Swagger**
* **Maven**
* **Docker / Docker Compose**

### Frontend

* **React 18**
* **React Router**
* **Vite**

---

## 🗂️ Project Structure

```text
.
├── src/                    # Spring Boot backend
├── frontend/               # React frontend
├── pom.xml
├── DockerFile              # Backend Dockerfile
├── docker-compose.yml
├── .gitignore
└── README.md
```

---

## 🔐 Roles & Access

| Role          | Responsibilities                                                                    |
| ------------- | ----------------------------------------------------------------------------------- |
| **Admin**     | Approve/reject users, manage users and courses, assign professors and students      |
| **Professor** | Manage courses, create questions and exams, manage exams, grade descriptive answers |
| **Student**   | View courses, take exams, save progress, submit attempts and view results           |

The backend uses **Spring Security** with JWT-based authentication and role-based access control to protect API endpoints.

---

## 🚀 Getting Started

### Option A — Docker Compose (Recommended)

The easiest way to run the complete application is using Docker Compose.

### Prerequisites

* Docker
* Docker Compose

Clone the repository:

```bash
git clone https://github.com/MoSeiin/OnlineExamManagement.git
cd OnlineExamManagement
```

Start all services:

```bash
docker compose up --build
```

This starts the following services:

| Service     | Address                                 |
| ----------- | --------------------------------------- |
| Backend API | `http://localhost:8080`                 |
| Swagger UI  | `http://localhost:8080/swagger-ui.html` |
| Frontend    | `http://localhost:5173`                 |
| PostgreSQL  | `localhost:5433`                        |
| Redis       | `localhost:6379`                        |

The Docker Compose configuration automatically connects the backend to PostgreSQL and Redis and configures the frontend to communicate with the backend API.

### Default Admin Account

A default admin account is available for testing:

```text
Email:    admin@gmail.com
Password: admin123
```

---

## ⚙️ Running Without Docker

If you prefer to run the services manually, install:

* Java 17+
* Maven
* PostgreSQL
* Redis
* Node.js 18+

### 1. Backend Configuration

Create a PostgreSQL database and make sure Redis is running.

The backend uses the following environment variables:

| Variable         | Default / Example         |
| ---------------- | ------------------------- |
| `DB_HOST`        | `localhost`               |
| `DB_PORT`        | `5432`                    |
| `DB_NAME`        | `online_exam_management2` |
| `DB_USERNAME`    | `postgres`                |
| `DB_PASSWORD`    | `root`                    |
| `REDIS_HOST`     | `127.0.0.1`               |
| `REDIS_PORT`     | `6379`                    |
| `JWT_SECRET`     | Required                  |
| `JWT_EXPIRATION` | Required                  |

Run the backend from the project root:

```bash
mvn spring-boot:run
```

The backend will be available at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

### 2. Frontend

Open a terminal inside the `frontend` directory:

```bash
cd frontend
npm install
npm run dev
```

The frontend development server will be available at:

```text
http://localhost:5173
```

Make sure the backend is running before starting the frontend.

---

## 📡 API Overview

| Area            | Base Path          | Description                                |
| --------------- | ------------------ | ------------------------------------------ |
| Authentication  | `/auth`            | Registration and login                     |
| Admin           | `/admin`           | User management and course assignments     |
| Courses         | `/courses`         | Course management and browsing             |
| Questions       | `/questions`       | Question bank management                   |
| Exams           | `/exams`           | Exam creation and management               |
| Student Exams   | `/student/exams`   | Start exams, save answers, submit attempts |
| Professor Exams | `/professor/exams` | View participants and grade answers        |

For complete request and response schemas, run the backend and open Swagger UI.

---

## 🧠 Backend Architecture & Focus

The backend was designed using a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Key backend implementation areas include:

* RESTful API design
* DTO-based request and response models
* MapStruct for DTO ↔ Entity mapping
* Spring Security with JWT authentication
* Role-based authorization
* JPA / Hibernate entity relationships
* PostgreSQL database design
* Redis for temporary exam-attempt data
* Centralized exception handling
* Validation and meaningful HTTP responses
* Swagger / OpenAPI documentation
* Docker and Docker Compose configuration

---

## 🐳 Docker Services

The application consists of four main Docker services:

```text
                    ┌───────────────┐
                    │    Frontend   │
                    │   React/Vite  │
                    └───────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │    Backend    │
                    │ Spring Boot   │
                    └───────┬───────┘
                            │
                  ┌─────────┴─────────┐
                  ▼                   ▼
          ┌──────────────┐    ┌──────────────┐
          │  PostgreSQL  │    │    Redis     │
          └──────────────┘    └──────────────┘
```

All services communicate through a dedicated Docker network defined in `docker-compose.yml`.

---

## 👨‍💻 My Contribution

I was responsible for the backend development, including:

* Backend architecture and project structure
* REST API implementation
* Authentication and authorization
* JWT security
* Role-based access control
* Database modeling and JPA/Hibernate implementation
* PostgreSQL integration
* Redis integration
* Exam and question management logic
* Exam attempt and answer handling
* Grading logic
* Exception handling
* API documentation
* Docker and Docker Compose configuration

The frontend was developed by a teammate and integrated with the backend REST API.

---

## 📄 License

This project was developed as a final/portfolio project and is shared for demonstration purposes.
