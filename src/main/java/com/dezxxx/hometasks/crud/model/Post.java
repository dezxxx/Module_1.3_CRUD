package com.dezxxx.hometasks.crud.model;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private Long id;
    private Long writerId; // связь
    private String title;
    private String content;
    private Status status;
    private List<Label> labels = new ArrayList<>();

    public Post() {}

    public Post(Long id, Long writerId, String title, String content, Status status, List<Label> labels) {
        this.id = id;
        this.writerId = writerId;
        this.title = title;
        this.content = content;
        this.status = status;
        if (labels != null) this.labels = labels;
    }

    public Long getId() { return id; }
    public Long getWriterId() { return writerId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Status getStatus() { return status; }
    public List<Label> getLabels() { return labels; }

    public void setId(Long id) { this.id = id; }
    public void setWriterId(Long writerId) { this.writerId = writerId; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setStatus(Status status) { this.status = status; }
    public void setLabels(List<Label> labels) { this.labels = (labels != null) ? labels : new ArrayList<>(); }

    @Override
    public String toString() {
        return "Post{id=" + id +
                ", writerId=" + writerId +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", labels=" + (labels == null ? 0 : labels.size()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post other)) return false;

        if (id == null || other.id == null) return false;

        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}