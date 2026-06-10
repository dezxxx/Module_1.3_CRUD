package com.dezxxx.hometasks.crud.repository.impl.hibernate;

import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.WriterRepository;
import com.dezxxx.hometasks.crud.util.HibernateUtil;
import com.dezxxx.hometasks.crud.util.RepositoryException;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Optional;

public class HibernateWriterRepositoryImpl implements WriterRepository {

    @Override
    public Writer save(Writer writer) {
        return HibernateUtil.executeInTransaction(session -> {
            session.persist(writer);
            return writer;
        });
    }

    @Override
    public List<Writer> findAll() {
        return HibernateUtil.executeInTransaction(session -> {
            List<Writer> writers = session.createQuery(
                    "SELECT DISTINCT w FROM Writer w LEFT JOIN FETCH w.posts ORDER BY w.id",
                    Writer.class
            ).getResultList();
            writers.stream()
                    .flatMap(w -> w.getPosts().stream())
                    .forEach(p -> Hibernate.initialize(p.getLabels()));
            return writers;
        });
    }

    @Override
    public Optional<Writer> findById(Long id) {
        return HibernateUtil.executeInTransaction(session -> {
            Optional<Writer> result = session.createQuery(
                    "SELECT w FROM Writer w LEFT JOIN FETCH w.posts WHERE w.id = :id",
                    Writer.class
            )
            .setParameter("id", id)
            .uniqueResultOptional();
            result.ifPresent(w -> w.getPosts().forEach(p -> Hibernate.initialize(p.getLabels())));
            return result;
        });
    }

    @Override
    public Writer update(Writer writer) {
        return HibernateUtil.executeInTransaction(session -> {
            session.createMutationQuery(
                    "UPDATE Writer w SET w.firstName = :firstName, w.lastName = :lastName WHERE w.id = :id"
            )
            .setParameter("firstName", writer.getFirstName())
            .setParameter("lastName", writer.getLastName())
            .setParameter("id", writer.getId())
            .executeUpdate();
            return writer;
        });
    }

    @Override
    public void deleteById(Long id) {
        HibernateUtil.deleteById(Writer.class, id);
    }
}