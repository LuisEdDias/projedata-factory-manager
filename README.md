# Factory Manager

A full-stack industrial management system designed to manage raw materials, product recipes, and generate an **optimized production plan** to maximize revenue using a **Greedy Algorithm**.

## Getting Started

### Prerequisites

* **Docker** and **Docker Compose** installed.

### Running the Application

To start the entire ecosystem (Database, API, and Frontend) with a single command from the project root:

```bash
docker-compose up --build

```

> **Note:** The API uses a **Healthcheck** to ensure it only starts after the PostgreSQL database is fully ready to accept connections.

---

## Access Links

| Service | URL | Note |
| --- | --- | --- |
| **Frontend (Vue 3)** | [http://localhost:3000](https://www.google.com/search?q=http://localhost:3000) | Main User Interface |
| **API Base URL** | [http://localhost:8000/api](https://www.google.com/search?q=http://localhost:8000/api) | Spring Boot REST Endpoints |
| **Swagger UI** | [http://localhost:8000/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8000/swagger-ui/index.html) | Interactive API Documentation |
| **Database** | `localhost:5400` | PostgreSQL (factory_db) |

### Database Credentials

* **User:** `factory`
* **Password:** `factorymanager`
* **Database:** `factory_db`

---

## Technical Highlights

### Backend (Spring Boot 3)

* **Greedy Algorithm:** Implemented a profit-maximization strategy that sorts products by value and calculates production based on raw material bottlenecks (O(n log n) complexity).
* **RFC 7807:** Consistent error handling using *Problem Details* for HTTP APIs.
* **i18n:** Support for localized error messages based on the `Accept-Language` header.
* **Architecture:** Clean Domain-Driven approach with **extensive test coverage** of the business logic.

### Frontend (Vue 3)

* **Vite + TypeScript:** Fast development and type safety across the application.
* **Axios Interceptors:** Global handling of API responses and validation errors.
* **Bootstrap 5:** Responsive and clean UI for industrial dashboards.

---

## Architecture Overview

The application follows a containerized micro-services approach:

1. **PostgreSQL:** Persistent data storage.
2. **Spring Boot API:** Handles business logic and the optimization engine.
3. **Nginx (Frontend Container):** Serves the compiled Vue 3 static files and acts as a reverse proxy.

---

## Author

Luís Eduardo Dias  
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Perfil-blue?logo=linkedin)](https://www.linkedin.com/in/luisvdias94)  
Backend / Full Stack Developer  
Java • Spring Boot • Angular • PostgreSQL