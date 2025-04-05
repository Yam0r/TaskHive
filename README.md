# TaskHive

## Description

**TaskHive** is a project and task management web application with file attachment support.  
It allows users to create projects, add and manage tasks, comment on them, and upload attachments.  
The system integrates with Dropbox for file storage and includes secure user authentication with role-based access.

This project was built for educational purposes to practice skills in Spring Boot, REST API design, security, ORM, Docker, and integration with external APIs.

---

## 🛠️ Technologies & Tools

- **Spring Boot** — Main framework for building the REST API.
- **Spring Security** — Secures the API with authentication and authorization.
- **Spring Data JPA** — Handles database interaction.
- **MySQL** — Main relational database.
- **Liquibase** — Manages database migrations.
- **Dropbox SDK** — For uploading and retrieving files from Dropbox.
- **JUnit + MockMvc** — For unit and integration testing.
- **Swagger** — API documentation and testing tool.
- **Docker + Docker Compose** — For containerization and easy environment setup.

---

## 📋 Features

- 👤 **User Registration & Authentication**
- ✅ **CRUD operations for tasks, projects, and comments**
- 📁 **Upload and download file attachments via Dropbox**
- 🔐 **Role-based access control (User / Admin)**
- 📊 **Interactive API documentation via Swagger UI**
- 🧪 **Integration tests with test containers**

---

## 🧩 Project's API

- [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- Postman collection available upon request

---

## 🧪 DB schema

![taskhive.png](taskhive.png)[ER Diagram](images/taskhive_er_diagram.png)

---

## 🧰 Prerequisites

- **Java 17+**
- **Docker & Docker Compose**
- **MySQL** or Dockerized MySQL instance
- IDE (e.g., IntelliJ IDEA)

---

## 🚀 How to Use
- **Clone the Repository**:
   ```bash
   https://github.com/Yam0r/Spring-Boot-intro.git

- Create the **.env** File: Create a file named **.env** in the root directory of the project and add the following variables:
   ```dotenv
  MYSQLDB_USER=yourname
  MYSQLDB_PASSWORD=yourpassword
  MYSQLDB_DATABASE=yourdatabase
  MYSQL_LOCAL_PORT=3307
  MYSQL_DOCKER_PORT=3306

  SPRING_LOCAL_PORT=8081
  SPRING_DOCKER_PORT=8080
  DEBUG_PORT=5005
- Run Docker Compose:
   ```bash
  docker-compose up --build
- Access the Application: Open your browser and go to http://localhost:8080.
