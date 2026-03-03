# CRUD Console Application (Module 1.3)

## Description

This is a console CRUD application implemented in Java.
The application allows managing Writers, Posts, and Labels using JSON files as storage.

The project follows a layered architecture:
- model
- repository
- controller
- view

Soft delete is implemented using Status enum (ACTIVE / DELETED).

---

## Entities

### Writer
- id
- firstName
- lastName
- List<Post> posts
- Status status

### Post
- id
- writerId
- title
- content
- List<Label> labels
- Status status

### Label
- id
- name
- Status status

---

## Storage

Data is stored in JSON files:
- data/writers.json
- data/posts.json
- data/labels.json

Files are generated automatically during application execution.

---

## Technologies Used

- Java 21
- Maven
- Gson (JSON serialization/deserialization)
- Stream API
- Generics
- OOP principles (polymorphism, abstraction)
- Soft delete pattern

---


## Project Structure


src/main/java/com/dezxxx/hometasks/crud
├── model
├── repository
│ ├── impl
├── controller
├── view
├── App.java
├── ApplicationContext.java


---

## How to Run

1. Clone the repository
2. Open in IntelliJ IDEA
3. Reload Maven dependencies
4. Run App.java

---

## Features

- Create / Read / Update / Delete Writers
- Create / Read / Update / Delete Posts
- Create / Read / Update / Delete Labels
- Soft delete implementation
- Relationship handling via IDs
- JSON-based storage

---

## Author

Sergey Zatulsky