# Java CRUD Console Application

Console CRUD application in Java using Gson and JSON files.

## Entities
- Writer (id, firstName, lastName, List<Post> posts, Status)
- Post (id, title, content, List<Label> labels, Status)
- Label (id, name, Status)

## Storage
- data/writers.json
- data/posts.json
- data/labels.json

## Layers
- model
- repository
- repository.impl
- controller
- view
- util
