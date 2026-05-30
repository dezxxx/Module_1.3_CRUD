package com.dezxxx.hometasks.crud.repository.impl.hibernate;

import com.dezxxx.hometasks.crud.config.PostStatus;
import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.PostRepository;
import com.dezxxx.hometasks.crud.util.HibernateUtil;
import com.dezxxx.hometasks.crud.util.RepositoryException;

import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class HibernatePostRepositoryImpl implements PostRepository {

    private static final String FETCH_QUERY =
            "FROM Post p LEFT JOIN FETCH p.writer LEFT JOIN FETCH p.labels";

    @Override
    public Post save(Post post) {
        return HibernateUtil.executeInTransaction(session -> {
            Writer managedWriter = session.get(Writer.class, post.getWriter().getId());
            post.setWriter(managedWriter);
            post.setLabels(attachLabels(session, post.getLabels()));
            session.persist(post);
            return post;
        });
    }

    @Override
    public List<Post> findAll() {
        return HibernateUtil.executeInTransaction(session ->
                session.createQuery(
                        "SELECT DISTINCT p " + FETCH_QUERY + " ORDER BY p.id",
                        Post.class
                ).getResultList()
        );
    }

    @Override
    public Optional<Post> findById(Long id) {
        return HibernateUtil.executeInTransaction(session ->
                session.createQuery(
                        "SELECT p " + FETCH_QUERY + " WHERE p.id = :id",
                        Post.class
                )
                .setParameter("id", id)
                .uniqueResultOptional()
        );
    }

    @Override
    public Post update(Post post) {
        return HibernateUtil.executeInTransaction(session -> {
            Post managed = session.get(Post.class, post.getId());
            if (managed == null) {
                throw new RepositoryException("Post not found: " + post.getId());
            }
            managed.setTitle(post.getTitle());
            managed.setContent(post.getContent());
            managed.setUpdated(post.getUpdated());
            managed.setStatus(post.getStatus());
            managed.setLabels(attachLabels(session, post.getLabels()));
            return managed;
        });
    }

    private List<Label> attachLabels(Session session, List<Label> labels) {
        if (labels == null || labels.isEmpty()) return Collections.emptyList();
        return labels.stream()
                .map(l -> session.get(Label.class, l.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findAllIncludingDeleted() {
        return HibernateUtil.executeInTransaction(session ->
                session.createNativeQuery("SELECT * FROM post ORDER BY id", Post.class)
                        .getResultList()
        );
    }

    @Override
    public void updateStatus(Long id, PostStatus status) {
        HibernateUtil.runInTransaction(session ->
                session.createNativeQuery(
                        "UPDATE post SET status = ?, updated = ? WHERE id = ?"
                )
                .setParameter(1, status.name())
                .setParameter(2, LocalDateTime.now())
                .setParameter(3, id)
                .executeUpdate()
        );
    }

    @Override
    public void deleteById(Long id) {
        updateStatus(id, PostStatus.DELETED);
    }
}