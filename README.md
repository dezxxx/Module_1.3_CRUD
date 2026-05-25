# Java Console CRUD Application

A console-based CRUD application for managing a simple blog system — Writers, Posts, and Labels.
Data is persisted in a **MySQL** database via raw **JDBC**. Schema is managed with **Liquibase**.

---

## Tech Stack

| Technology       | Version | Purpose                        |
|------------------|---------|--------------------------------|
| Java             | 21      | Core language                  |
| MySQL            | 8+      | Relational database            |
| JDBC             | —       | Database access (no ORM)       |
| Liquibase        | 4.29.1  | Database schema migrations     |
| JUnit 5          | 5.10.2  | Unit testing                   |
| Mockito          | 5.12.0  | Mocking in tests               |
| Maven            | 3.x     | Build & dependency management  |

---

## Architecture

The project follows a strict layered architecture:

```
View → Controller → Service → Repository → MySQL
```

| Layer        | Package         | Responsibility                        |
|--------------|-----------------|---------------------------------------|
| View         | `view`          | Console UI, user input/output         |
| Controller   | `controller`    | Delegates between View and Service    |
| Service      | `service`       | Business logic, validation            |
| Repository   | `repository`    | Data access via JDBC                  |
| Config/Util  | `util`, `config`| Connection, migrations, helpers       |

---

## Design Patterns

- **Singleton** — `ConnectionManager` holds a single database connection for the app lifetime
- **Strategy** — `ValidationStrategy<T>` interface with `NotBlankStrategy`, `PositiveIdStrategy`, and `CompositeStrategy` (chains multiple rules) — injected into services, eliminates duplicated validation code
- **Facade** — `ApplicationContext` hides the full initialization complexity (connection → migrations → repositories → services → controllers → views); `App.java` only calls `new ApplicationContext().start()`

---

## Entities

**Writer** — author with a list of posts
```
id | first_name | last_name
```

**Post** — blog post linked to a writer and labels
```
id | title | content | created | updated | status | writer_id (FK)
```
Post status: `ACTIVE` · `UNDER_REVIEW` · `DELETED`

**Label** — tag assigned to posts (many-to-many)
```
id | name (UNIQUE)
```

### Relationships
```
Writer  ──< Post >── Label
(1:N)         (M:N via post_label)
```

---

## Database Schema

Managed by Liquibase (`src/main/resources/db/changelog/`):

```
db.changelog-master.xml   ← master (includes SQL files)
001-writer.sql            ← CREATE TABLE writer
002-post.sql              ← CREATE TABLE post + FK to writer
003-label.sql             ← CREATE TABLE label
004-relations.sql         ← CREATE TABLE post_label (M:N junction)
005-not-null-writer-names.sql   ← ALTER writer: first_name/last_name NOT NULL
006-not-null-post-writer-id.sql ← ALTER post: writer_id NOT NULL
```

Liquibase runs automatically on application startup.

---

## Features

- Full **CRUD** for Writer, Post, Label
- **Soft delete** for Post — sets `status = DELETED`, record stays in DB
- **Hard delete** for Writer (cascades to posts) and Label
- **Post status change** — switch between `ACTIVE` and `UNDER_REVIEW`
- **Partial update** — press Enter to keep the current field value
- **Pagination** — 5 records per page with `n` / `p` navigation
- **Search / filter** — search by name or title in Get All
- **Delete confirmation** — `(y/n)` prompt before any deletion
- **Cancel** — enter `0` at any selection screen to return to menu
- **Input validation** — minimum length enforced, non-numeric input handled

---

## Prerequisites

- **Java 21** or higher
- **MySQL 8+** running on `localhost:3306`
- Database `my_rdb` must exist:

```sql
CREATE DATABASE my_rdb;
```

---

## Configuration

Database credentials are set in `ConnectionManager.java`:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/my_rdb";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

---

## How to Run

1. Clone the repository
2. Create the database (see Prerequisites)
3. Set your credentials in `ConnectionManager.java`
4. Open the project in **IntelliJ IDEA**
5. Run Maven: `mvn clean compile`
6. Run `App.java` — Liquibase will create all tables automatically on first launch

Or via Maven:
```bash
mvn compile exec:java -Dexec.mainClass="com.dezxxx.hometasks.crud.App"
```

---

## Running Tests

```bash
mvn test
```

Tests cover the service layer using **JUnit 5** and **Mockito** (repositories are mocked — no DB required):

```
LabelServiceTest   — 13 tests
PostServiceTest    — 17 tests
WriterServiceTest  — 16 tests
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/dezxxx/hometasks/crud/
│   │   ├── App.java
│   │   ├── ApplicationContext.java        (Facade)
│   │   ├── config/
│   │   │   └── PostStatus.java            (Enum: ACTIVE, UNDER_REVIEW, DELETED)
│   │   ├── controller/
│   │   ├── model/
│   │   ├── repository/
│   │   │   └── impl/jdbc/                 (JDBC implementations)
│   │   ├── service/
│   │   ├── util/
│   │   │   ├── ConnectionManager.java     (Singleton)
│   │   │   ├── InputUtil.java
│   │   │   ├── LiquibaseMigration.java
│   │   │   ├── Pager.java
│   │   │   ├── RepositoryException.java
│   │   │   └── UserCancelledException.java
│   │   └── validation/
│   │       ├── ValidationStrategy.java    (Strategy interface)
│   │       ├── NotBlankStrategy.java
│   │       ├── PositiveIdStrategy.java
│   │       └── CompositeStrategy.java     (chains multiple rules)
│   └── resources/
│       └── db/changelog/
└── test/
    └── java/com/dezxxx/hometasks/crud/service/
```

---

## Author

Sergey Zatulsky
