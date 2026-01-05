# Chess Tournament RESTful Backend

RESTful backend for managing chess tournaments, developed using Java Spring Boot with a multi-database architecture based on MongoDB and Redis.

## 🎯 Project Goal
The goal of this project is to design and implement a backend system capable of:
- managing chess tournaments, matches, and players
- exposing a clean REST API for external clients
- handling both persistent data and fast-access state efficiently

The project was developed for a Large Scale and Multi-Structured Databases course at the University of Pisa.

## 🧠 Skills Demonstrated
- REST API design and implementation
- Spring Boot application architecture
- Domain modeling for non-trivial business logic
- Polyglot persistence:
  - MongoDB for persistent data
  - Redis for fast-access and transient state
- API documentation with Swagger
- Team-based development workflow
- Java backend engineering best practices

## 🏗️ Architecture Overview
- **Spring Boot** as the application framework
- **MongoDB** used for long-term storage of tournaments, players, and match data
- **Redis** used for caching and fast access to frequently updated or transient information
- **Swagger/OpenAPI** for API exploration and documentation

This separation allows the system to balance consistency, scalability, and performance.

## 📂 Project Structure
- `src/` – Application source code
- `test_partita/` – Match-related test data and logic
- `Documentation.pdf` – Detailed project documentation
- `pom.xml` – Maven configuration and dependencies

## 🔌 API Documentation
Swagger is integrated into the project to allow interactive exploration of the REST endpoints and request/response models.

## 🔍 Design Notes
Although developed in an academic context, the project focuses on real backend concerns:
- clear separation between persistence layers
- maintainable service and controller design
- explicit domain logic instead of CRUD-only endpoints
