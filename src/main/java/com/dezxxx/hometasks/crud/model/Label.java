package com.dezxxx.hometasks.crud.model;

public class Label {
    // Entity / Сущность: Label
    private Long id;
    private String name;
    private Status status;
    public Label() {
    }
    public Label(Long id, String name , Status status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    // getters / геттеры
    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    // setters / сеттеры
    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Label{" +
                "id=" + id +
                ", name='" + getName()+ '\'' +
                ", status=" + status +
                '}';
    }

    // equals/hashCode по id (обычно так делают для сущностей)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Label other)) return false;

        if (id == null || other.id == null) return false;

        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}


