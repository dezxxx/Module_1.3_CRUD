# Java Console CRUD Application

A console-based CRUD application for managing a simple blog system — Writers, Posts, and Labels.
Data is persisted via **Hibernate ORM** with support for **MySQL** and **PostgreSQL**.
Schema is managed with **Flyway**. Database is selected at application startup.

---

## Tech Stack

| Technology  | Version      | Purpose                       |
|-------------|--------------|-------------------------------|
| Java        | 21           | Core language                 |
| MySQL       | 8+           | Relational database (option 1)|
| PostgreSQL  | 18+          | Relational database (option 2)|
| Hibernate   | 6.6.3.Final  | ORM (database access)         |
| Flyway      | 10.20.1      | Database schema migrations    |
| JUnit 5     | 5.10.2       | Unit testing                  |
| Mockito     | 5.12.0       | Mocking in tests              |
| Maven       | 3.x          | Build & dependency management |

---

## Architecture

```
View → Controller → Service → Repository → Hibernate → MySQL / PostgreSQL
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
- **Facade** — `ApplicationContext` hides full initialization (DB selection → Flyway → Hibernate → repositories → services → controllers → views); `App.java` only calls `new ApplicationContext().start()`

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

Managed by Flyway, runs automatically on startup:

```
db/migration/              ← MySQL migrations
    V1__create_writer_table.sql
    V2__create_post_table.sql
    V3__create_label_table.sql
    V4__create_post_label_table.sql

db/migration/postgres/     ← PostgreSQL migrations (BIGSERIAL instead of AUTO_INCREMENT)
    V1__create_writer_table.sql
    V2__create_post_table.sql
    V3__create_label_table.sql
    V4__create_post_label_table.sql
```

---

## Features

- Full **CRUD** for Writer, Post, Label
- **Database selection** at startup — choose MySQL or PostgreSQL via console menu
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
- **MySQL 8+** or **PostgreSQL 18+** running locally
- Database `hibernate_db_1` must exist before first launch:

**MySQL:**
```sql
CREATE DATABASE hibernate_db_1;
```

**PostgreSQL:**
```sql
CREATE DATABASE hibernate_db_1;
```

---

## Configuration

Credentials are set in `hibernate.cfg.xml` / `hibernate-postgres.cfg.xml` and `FlywayMigration.java`:

| Database   | Default URL                                       | User     |
|------------|---------------------------------------------------|----------|
| MySQL      | `jdbc:mysql://localhost:3306/hibernate_db_1`      | root     |
| PostgreSQL | `jdbc:postgresql://localhost:5432/hibernate_db_1` | postgres |

---

## How to Run

1. Clone the repository
2. Create the database (see Prerequisites)
3. Set your credentials in the config files if needed
4. Run from IDE or:

```bash
mvn compile exec:java -Dexec.mainClass="com.dezxxx.hometasks.crud.App"
```

5. At startup, select the database:

```
=== Select Database ===
1. MySQL
2. PostgreSQL
Choose:
```

Flyway will apply migrations and Hibernate will validate the schema automatically.

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
│   │   │   └── impl/hibernate/               (Hibernate implementations — work with both DBs)
│   │   ├── service/
│   │   ├── util/
│   │   │   ├── HibernateUtil.java            (Singleton SessionFactory + transactions)
│   │   │   ├── FlywayMigration.java          (runs Flyway on startup)
│   │   │   ├── DatabaseType.java             (Enum: MYSQL, POSTGRES)
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
│       ├── hibernate.cfg.xml                  (MySQL config)
│       ├── hibernate-postgres.cfg.xml         (PostgreSQL config)
│       └── db/migration/
│           ├── V1–V4 (MySQL)
│           └── postgres/
│               └── V1–V4 (PostgreSQL)
└── test/
    └── java/com/dezxxx/hometasks/crud/service/
```

---

## Author

Sergey Zatulsky