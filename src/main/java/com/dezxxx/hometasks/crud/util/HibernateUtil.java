package com.dezxxx.hometasks.crud.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.function.Consumer;
import java.util.function.Function;

public final class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private HibernateUtil() {}

    private static SessionFactory buildSessionFactory() {
        SessionFactory sf = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();
        Runtime.getRuntime().addShutdownHook(new Thread(sf::close));
        return sf;
    }

    public static <T> T executeInTransaction(Function<Session, T> action) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                T result = action.apply(session);
                session.getTransaction().commit();
                return result;
            } catch (Exception e) {
                session.getTransaction().rollback();
                throw new RepositoryException("Transaction failed", e);
            }
        }
    }

    public static void runInTransaction(Consumer<Session> action) {
        executeInTransaction(session -> {
            action.accept(session);
            return null;
        });
    }

    public static <T> void deleteById(Class<T> type, Object id) {
        runInTransaction(session -> {
            T entity = session.get(type, id);
            if (entity == null) {
                throw new RepositoryException(type.getSimpleName() + " not found: " + id);
            }
            session.remove(entity);
        });
    }

}