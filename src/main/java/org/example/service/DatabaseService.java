package org.example.service;

import org.example.dao.UserDAO;
import org.example.model.dataSets.UserDataSet;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseService {
    private static final String HIBERNATE_SHOW_SQL = "true";
    private static final String HIBERNATE_HBM2DDL_AUTO = "update"; // update вместо create чтобы не терять данные

    private final SessionFactory sessionFactory;

    public DatabaseService() {
        Configuration configuration = getPostgreSQLConfiguration();
        sessionFactory = createSessionFactory(configuration);
    }

    private Configuration getPostgreSQLConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/filemanager");
        configuration.setProperty("hibernate.connection.username", "postgres");
        configuration.setProperty("hibernate.connection.password", "1234");
        configuration.setProperty("hibernate.show_sql", HIBERNATE_SHOW_SQL);
        configuration.setProperty("hibernate.hbm2ddl.auto", HIBERNATE_HBM2DDL_AUTO);
        configuration.setProperty("hibernate.current_session_context_class", "thread");

        return configuration;
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public UserDataSet getUser(long id) throws DBException {
        try (Session session = sessionFactory.openSession()) {
            UserDAO dao = new UserDAO(session);
            return dao.getUserById(id);
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public UserDataSet getUserByLogin(String login) throws DBException {
        try (Session session = sessionFactory.openSession()) {
            UserDAO dao = new UserDAO(session);
            return dao.getUserByLogin(login);
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public long addUser(String login, String hashedPassword, String email) throws DBException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            UserDAO dao = new UserDAO(session);
            long id = dao.insertUser(login, hashedPassword, email);
            transaction.commit();
            return id;
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            throw new DBException(e);
        }
    }

    public boolean updateUser(UserDataSet user) throws DBException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            UserDAO dao = new UserDAO(session);
            boolean result = dao.updateUser(user);
            transaction.commit();
            return result;
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            throw new DBException(e);
        }
    }

    public boolean deleteUser(String login) throws DBException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            UserDAO dao = new UserDAO(session);
            boolean result = dao.deleteUser(login);
            transaction.commit();
            return result;
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            throw new DBException(e);
        }
    }

    public boolean userExists(String login) throws DBException {
        try (Session session = sessionFactory.openSession()) {
            UserDAO dao = new UserDAO(session);
            return dao.userExists(login);
        } catch (HibernateException e) {
            throw new DBException(e);
        }
    }

    public void printConnectInfo() {
        try (Session session = sessionFactory.openSession()) {
            session.doWork(connection -> {
                System.out.println("DB name: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
                System.out.println("Driver: " + connection.getMetaData().getDriverName());
                System.out.println("Autocommit: " + connection.getAutoCommit());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}