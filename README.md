# Java Console CRUD Application

A console-based CRUD application for managing a simple blog system — Writers, Posts, and Labels.
Data is persisted in a **MySQL** database via **Hibernate ORM**. Schema is managed with **Flyway**.

---

## Tech Stack

| Technology  | Version      | Purpose                       |
|-------------|--------------|-------------------------------|
| Java        | 21           | Core language                 |
| MySQL       | 8+           | Relational database           |
| Hibernate   | 6.6.3.Final  | ORM (database access)         |
| Flyway      | 10.20.1      | Database schema migrations    |
| JUnit 5     | 5.10.2       | Unit testing                  |
| Mockito     | 5.12.0       | Mocking in tests              |
| Maven       | 3.x          | Build & dependency management |

---

## Architecture

```
View → Controller → Service → Repository → Hibernate → MySQL
```

| Layer       | Package          | Responsibility                     |
|-------------|------------------|------------------------------------|
| View        | `view`           | Console UI, user input/output      |
| Controller  | `controller`     | Delegates between View and Service |
| Service     | `service`        | Business logic, validation         |
| Repository  | `repository`     | Data access via Hibernate Session  |
| Config/Util | `util`, `config` | SessionFactory, migrations, helpers|

---

## Design Patterns

- **Singleton** — `HibernateUtil` builds and holds a single `SessionFactory` for the app lifetime
- **Strategy** — `ValidationStrategy<T>` interface with `NotBlankStrategy`, `PositiveIdStrategy`, and `CompositeStrategy` (chains multiple rules) — injected into services
- **Facade** — `ApplicationContext` hides full initialization (Flyway → Hibernate → repositories → services → controllers → views); `App.java` only calls `new ApplicationContext().start()`

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

Managed by Flyway (`src/main/resources/db/migration/`):

```
V1__create_writer_table.sql      ← CREATE TABLE writer
V2__create_post_table.sql        ← CREATE TABLE post + FK to writer
V3__create_label_table.sql       ← CREATE TABLE label
V4__create_post_label_table.sql  ← CREATE TABLE post_label (M:N junction)
```

Flyway runs automatically on application startup before Hibernate initializes.

---

## Features

- Full **CRUD** for Writer, Post, Label
- **Soft delete** for Post — sets `status = DELETED`, record stays in DB
- **Hard delete** for Writer (cascades to posts) and Label
- **Post restore** — Change Status shows all posts including DELETED, allows restoring back to `ACTIVE`
- **Post status change** — `ACTIVE` · `UNDER_REVIEW` · `DELETED` (via Change Status menu)
- **Partial update** — press Enter to keep the current field value
- **Pagination** — 5 records per page with `n` / `p` navigation
- **Search / filter** — search by name or title in Get All
- **Delete confirmation** — `(y/n)` prompt before any deletion
- **Cancel** — enter `0` at any selection screen to return to menu
- **Input validation** — minimum length enforced, non-numeric input handled

---

## Prerequisites

- **Java 21** or higher
- **MySQL 8.4+** running on `localhost:3306`
- Database `hibernate_db_1` must exist:

```sql
CREATE DATABASE hibernate_db_1;
```

---

## Configuration

Credentials are set directly in `hibernate.cfg.xml` and `FlywayMigration.java`:

```
URL:      jdbc:mysql://localhost:3306/hibernate_db_1
User:     root
Password: your_password
```

---

## How to Run

1. Clone the repository
2. Create the database (see Prerequisites)
3. Set your credentials in `hibernate.cfg.xml` and `FlywayMigration.java`
4. Run:

```bash
mvn compile exec:java -Dexec.mainClass="com.dezxxx.hometasks.crud.App"
```

Flyway will apply migrations and Hibernate will validate the schema automatically on first launch.

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
│   │   ├── ApplicationContext.java             (Facade)
│   │   ├── config/
│   │   │   └── PostStatus.java                (Enum: ACTIVE, UNDER_REVIEW, DELETED)
│   │   ├── controller/
│   │   ├── model/                             (JPA entities: Writer, Post, Label)
│   │   ├── repository/
│   │   │   └── impl/hibernate/               (Hibernate implementations)
│   │   ├── service/
│   │   ├── util/
│   │   │   ├── HibernateUtil.java            (Singleton SessionFactory + transactions)
│   │   │   ├── FlywayMigration.java          (runs Flyway on startup)
│   │   │   ├── InputUtil.java
│   │   │   ├── Pager.java
│   │   │   ├── RepositoryException.java
│   │   │   └── UserCancelledException.java
│   │   └── validation/
│   │       ├── ValidationStrategy.java        (Strategy interface)
│   │       ├── NotBlankStrategy.java
│   │       ├── PositiveIdStrategy.java
│   │       └── CompositeStrategy.java         (chains multiple rules)
│   └── resources/
│       ├── hibernate.cfg.xml
│       └── db/migration/
│           ├── V1__create_writer_table.sql
│           ├── V2__create_post_table.sql
│           ├── V3__create_label_table.sql
│           └── V4__create_post_label_table.sql
└── test/
    └── java/com/dezxxx/hometasks/crud/service/
```

---

## Author

Sergey Zatulsky
