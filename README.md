# Java Console CRUD Application

Simple console CRUD application written in Java.

The project works with three entities:

-   Writer
-   Post
-   Label

Data is stored in JSON files using the Gson library.

Deletion is implemented as soft delete --- records are not removed from
the file, their status is changed to DELETED.

------------------------------------------------------------------------

# Entities

Writer -- author that can have multiple posts.

Writer ├ id ├ firstName ├ lastName ├ status └ List`<Post>`{=html} posts

Post -- contains text and a list of labels.

Post ├ id ├ title ├ content ├ status └ List`<Label>`{=html} labels

Label -- tag (category) assigned to a post.

Label ├ id ├ name └ status

------------------------------------------------------------------------

# Architecture

The project is divided into layers:

model -- entity classes repository -- data access controller -- business
logic view -- console interface util -- helper classes

------------------------------------------------------------------------

# Data Storage

Data is stored in JSON files:

data/ ├ writers.json ├ posts.json └ labels.json

------------------------------------------------------------------------

# Features

The application supports:

-   create
-   read
-   update
-   soft delete
-   selecting related entities

Relations:

Writer → contains List`<Post>`{=html}\
Post → contains List`<Label>`{=html}

------------------------------------------------------------------------

# Running the Project

1.  Open the project in IntelliJ IDEA
2.  Reload Maven dependencies
3.  Run the class:

Main

The console menu will start.

------------------------------------------------------------------------

# Dependency

Gson

------------------------------------------------------------------------

# Author

Sergey Zatulsky

