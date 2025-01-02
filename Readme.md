# MS-Auth

## Table of Contents:

1. [General Info](#general-info)
2. [Technologies](#technologies)
3. [Prerequisites](#prerequisites)
4. [Installation](#installation)
5. [Configuration](#configuration)
6. [Running the Application](#running-the-application)
7. [Docker Stack](#docker-stack)
8. [Test](#test)
9. [API Documentation](#api-documentation)
10. [Contact](#contact)

## General Info

***
MS-Auth is an authentication microservice built with Spring Boot. This service provides functionalities for user
authentication and authorization using JWT (JSON Web Tokens), integrating various modern technologies for secure
credential management.

## Technologies

***
A list of technologies used within the project:

- **Spring Boot 3.3.6**: Framework for rapid Java application development.
- **Spring Security**: Implementation for authentication and authorization.
- **Spring Data JPA**: Data persistence using JPA.
- **JWT**: For authentication based on JSON Web Tokens.
- **MapStruct**: For efficient conversion between DTOs and entities.
- **H2 Database**: In-memory database for testing.
- **SpringDoc OpenAPI**: Automatic generation of API documentation in OpenAPI format.
- **JUnit and Mockito**: For unit testing and dependency mocking.

## Prerequisites

***
Before you begin, ensure you have met the following prerequisites:

* JDK 21 or higher installed.
* Maven 3.6+ installed.
* You have MySQL running.
* Docker and Docker Compose installed(if you prefer to run the application with Docker).

## Installation

***

1. **Clone the repository**:
    ```bash
    git clone https://github.com/jmarqb/ms-auth.git
    cd ms-auth
    mvn clean install
    ```

## Configuration

***
Before running the project, make sure to define the following environment variables:

### For Unix/Linux systems:

```bash
export DB_URL=jdbc:mysql://localhost:3306/ms_auth_db?createDatabaseIfNotExist=true&serverTimezone=UTC
export DB_USERNAME=root
export DB_PASSWORD=sasa
```

### For PowerShell (Windows):

```bash
$env:DB_URL="jdbc:mysql://localhost:3306/ms_auth_db?createDatabaseIfNotExist=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="sasa"
```

## Running the Application

***
There are two ways to run the application:
Build and run with Maven or Build and run with Docker.

1. **Build and run with Maven**:
    - To build and run the application using Maven, use the following command:
      ```bash
      mvn spring-boot:run
      ```

2. **Access the application**:
   Once the application are up and running, the documentation will be accessible
   at `http://localhost:8081/swagger-ui/index.html` (or any port specified in your `application.properties` file).

## Docker Stack

If you have Docker and Docker Compose installed, running the application becomes even easier. First, clone the
repository and navigate to the project directory:
To run the application using Docker:

```bash
git clone https://github.com/jmarqb/ms-auth.git
cd ms-auth
mvn clean install
```

**Build and run with Docker**:

- To build the Docker image and start the application, use the following command:

```bash
docker-compose up --build
```

This will set up and start both the application and necessary services like the database.

1. **Access the application**:
   Once the containers are up and running, the application documentation will be accessible
   at `http://localhost:8081/swagger-ui/index.html` (or any port specified in your `docker-compose.yml` file).

2. **Stop the containers**:
   To stop the running containers, use:
    ```bash
    docker-compose down
    ```

## Test

To ensure everything runs smoothly, this project includes both Unit and Integration tests using the tools JUnit and Mockito.
To execute them, follow these steps:

Dependency Installation: Before running the tests, ensure you've installed all the project dependencies. If you haven't done so yet, you can install them by executing the command `mvn clean install`.

To run the tests on controllers and services, and verify the complete flow and functioning of the application use the following command:

```bash
$ mvn test
```
It's important to highlight that these e2e tests utilize a H2 database for testing.

## API Documentation

You can access the API documentation at `localhost:<port>/swagger-ui/index.html` or `localhost:<port>/v3/api-docs`.

For more detailed information about the endpoints, responses, and status codes, visit the API documentation.

---

## Contact

Thank you for checking out my project! If you have any questions, feedback, or just want to connect, here's where you
can find me:

**GitHub**: [jmarqb](https://github.com/jmarqb)

Feel free to [open an issue](https://github.com/jmarqb/ms-auth/issues) or submit a PR if you find any bugs or have some
suggestions for improvements.

© 2025 Jacmel Márquez. All rights reserved.
