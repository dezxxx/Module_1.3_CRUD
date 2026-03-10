package com.dezxxx.hometasks.crud.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Post {

    private Long id;
    private String title;
    private String content;
    private Status status;
    private List<Label> labels = new ArrayList<>();

    public Post() {
    }

    public Post(Long id, String title, String content, Status status, List<Label> labels) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
        this.labels = labels != null ? labels : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels != null ? labels : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", labels=" + labels.stream().map(Label::getName).toList() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : Objects.hash(title, content);
    }
}
