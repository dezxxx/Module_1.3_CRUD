package com.dezxxx.hometasks.crud.repository.impl.hibernate;

import com.dezxxx.hometasks.crud.config.PostStatus;
import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.PostRepository;
import com.dezxxx.hometasks.crud.util.HibernateUtil;
import com.dezxxx.hometasks.crud.util.RepositoryException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class HibernatePostRepositoryImpl implements PostRepository {

    @Override
    public Post save(Post post) {
        return HibernateUtil.executeInTransaction(session -> {
            Writer managedWriter = session.get(Writer.class, post.getWriter().getId());
            post.setWriter(managedWriter);

            if (post.getLabels() != null && !post.getLabels().isEmpty()) {
                List<Label> managedLabels = post.getLabels().stream()
                        .map(l -> session.get(Label.class, l.getId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                post.setLabels(managedLabels);
            }

            session.persist(post);
            return post;
        });
    }

    @Override
    public List<Post> findAll() {
        return HibernateUtil.executeInTransaction(session ->
                session.createQuery(
                        "SELECT DISTINCT p FROM Post p " +
                        "LEFT JOIN FETCH p.writer " +
                        "LEFT JOIN FETCH p.labels " +
                        "ORDER BY p.id",
                        Post.class
                ).getResultList()
        );
    }

    @Override
    public Optional<Post> findById(Long id) {
        return HibernateUtil.executeInTransaction(session ->
                session.createQuery(
                        "SELECT p FROM Post p " +
                        "LEFT JOIN FETCH p.writer " +
                        "LEFT JOIN FETCH p.labels " +
                        "WHERE p.id = :id",
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

            if (post.getLabels() != null) {
                List<Label> managedLabels = post.getLabels().stream()
                        .map(l -> session.get(Label.class, l.getId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                managed.setLabels(managedLabels);
            }

            return managed;
        });
    }

    @Override
    public void deleteById(Long id) {
        HibernateUtil.runInTransaction(session ->
                session.createMutationQuery(
                        "UPDATE Post p SET p.status = :status, p.updated = :updated WHERE p.id = :id"
                )
                .setParameter("status", PostStatus.DELETED)
                .setParameter("updated", LocalDateTime.now())
                .setParameter("id", id)
                .executeUpdate()
        );
    }
}