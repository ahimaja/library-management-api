# Library Management System API

A REST API for managing books, library members, and book borrowing.
Built with Java and Spring Boot.

## Features

-   Add, update, search, and remove books.
-   Manage physical copies of each book.
-   Register members and manage member details.
-   Issue books, return books, and view borrowing history.
-   Find overdue borrow records and manage member fines.
-   Void borrow records along with the specification of reason.
-   Allow members to check their details, pay their fines and view their borrowing history
-   Log in using JWT authentication.
-   Manage member, employee and administrator accounts.
-   Control access using three roles: MEMBER, EMPLOYEE, and ADMIN.

## Technologies

-   Java 21 and Spring Boot
-   Spring Web, Spring Data JPA, and Hibernate
-   PostgreSQL 17
-   Flyway database migrations
-   Spring Security, JWT, and BCrypt
-   Maven
-   JUnit and Mockito
-   Docker and Docker Compose
-   Swagger UI (OpenAPI)
-   AWS Lightsail (temporary test deployment)

## Project structure

-   `controller/` --- receives API requests.
-   `service/` --- contains the library rules.
-   `repository/` --- reads and saves database records.
-   `model/` --- contains the database entities.
-   `dto/` --- defines request and response data.
-   `exception/` --- handles application errors.
-   `security/` --- handles login, JWT, and access rules.
-   `src/main/resources/db/migration/` --- contains Flyway SQL
    migrations.
-   `src/test/` --- contains automated tests.
-   `compose.yaml` --- local Docker configuration.
-   `compose.aws.yaml` --- AWS-specific Docker settings.

## Run locally with Docker

You need Docker Engine or Docker Desktop with Docker Compose.

1.  Clone the repository:

    ``` bash
    git clone https://github.com/ahimaja/library-management-api.git
    cd library-management-api
    ```

2.  Create your environment file from the example:

    ``` bash
    cp .env.example .env
    ```

    On Windows PowerShell, use `Copy-Item .env.example .env` instead.

3.  Edit `.env` and set your database password and JWT secret:

    ``` dotenv
    DB_HOST=localhost
    DB_PORT=5432
    DB_NAME=library
    DB_USERNAME=postgres
    DB_PASSWORD=replace-with-your-password
    JWT_SECRET=replace-with-a-valid-base64-secret
    ```

    Generate a random Base64 secret with `openssl rand -base64 32` if
    OpenSSL is installed. Do not commit `.env` to GitHub.

4.  Start the containers:

    ``` bash
    docker compose up -d --build
    ```

5.  Open Swagger UI:

    `http://localhost:8081/swagger-ui/index.html`

The app runs on host port `8081`. PostgreSQL uses host port `5433` for
local development. Inside Docker, the app connects to PostgreSQL at
`postgres:5432`.

To stop the containers while keeping database data:

``` bash
docker compose down
```

**Note:** The first administrator account must be created separately in
the database before administrator-only actions can be used. Public
registration creates member accounts.

## Main API endpoints

| Method |           Endpoint            |                      Purpose |
|:-------|:-----------------------------:|-----------------------------:|   
| POST   |       `/auth/register`        |            Register a member |
| POST   |         `/auth/login`         |         Log in and get a JWT |
| GET    |           `/books`            |                   List books |
| GET    |        `/books/search`        |             Search for books |
| POST   |           `/books`            |                   Add a book |
| GET    |        `/book-records`        |    List physical book copies |
| POST   |        `/book-records`        |          Add a physical copy |     
| PATCH  |  `/book-records/{id}/retire`  |                Retire a copy |  
| POST   |       `/borrow-records`       |                 Issue a book |
| POST   | `/borrow-records/{id}/return` |                Return a book |
| GET    |   `/borrow-records/overdue`   |  View overdue borrow records |
| PATCH  |  `/borrow-records/{id}/void`  |         Void a borrow record |
| GET    |     `/borrow-records/me`      | View your own borrow history |
| GET    |         `/members/me`         | View your own member details |
| POST   |    `/members/me/pay-fine`     |            Pay your own fine |
| POST   |        `/admin/staff`         |       Create a staff account |


These are selected endpoints, not the full API list. Swagger UI shows
all available operations and their request formats.

## User roles

-   **MEMBER:** View books and copies, view personal borrowing records,
    and pay personal fines.
-   **EMPLOYEE:** Manage books, copies, members, and borrowing
    operations.
-   **ADMIN:** Perform employee operations and manage staff accounts.

Most endpoints require a JWT. Log in, copy the token, and use Swagger
UI's **Authorize** button to validate the generated JWT.

## Tests

The project includes unit and integration tests for services,
repositories, security, and borrowing operations.

If Maven is installed, run:

``` bash
mvn test
```

The integration tests need the test database configured in
`src/test/resources/application.properties`.

## AWS Lightsail deployment

The application was also tested on an Ubuntu 24.04 AWS Lightsail
instance.

AWS uses the base Compose file together with `compose.aws.yaml`:

``` bash
sudo docker compose -f compose.yaml -f compose.aws.yaml config --quiet
sudo docker compose -f compose.yaml -f compose.aws.yaml up -d --build
sudo docker compose -f compose.yaml -f compose.aws.yaml ps
```

The AWS override: 
- Does not map PostgreSQL's port to the host port. 
- Restarts both containers automatically after a server reboot.

A Docker volume stores PostgreSQL data, so the data remains when
containers are removed and started again without deleting the volume.

For our temporary deployment, port `8081` was restricted to a trusted
public IP address in the Lightsail firewall. The deployment used HTTP
for learning and testing; it is not a production-ready HTTPS setup.

## Possible future improvements

-   Add HTTPS and stronger production deployment settings.
-   Add more automated tests.
-   Improve deployment automation.
