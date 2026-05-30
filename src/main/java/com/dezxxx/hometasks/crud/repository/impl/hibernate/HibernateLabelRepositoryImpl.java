package com.dezxxx.hometasks.crud.repository.impl.hibernate;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.repository.LabelRepository;
import com.dezxxx.hometasks.crud.util.HibernateUtil;
import com.dezxxx.hometasks.crud.util.RepositoryException;

import java.util.List;
import java.util.Optional;

public class HibernateLabelRepositoryImpl implements LabelRepository {

    @Override
    public Label save(Label label) {
        return HibernateUtil.executeInTransaction(session -> {
            session.persist(label);
            return label;
        });
    }

    @Override
    public List<Label> findAll() {
        return HibernateUtil.executeInTransaction(session ->
                session.createQuery("FROM Label ORDER BY id", Label.class)
                        .getResultList()
        );
    }

    @Override
    public Optional<Label> findById(Long id) {
        return HibernateUtil.executeInTransaction(session ->
                Optional.ofNullable(session.get(Label.class, id))
        );
    }

    @Override
    public Label update(Label label) {
        return HibernateUtil.executeInTransaction(session ->
                session.merge(label)
        );
    }

    @Override
    public void deleteById(Long id) {
        HibernateUtil.runInTransaction(session -> {
            Label label = session.get(Label.class, id);
            if (label == null) {
                throw new RepositoryException("Label not found: " + id);
            }
            session.remove(label);
        });
    }
}