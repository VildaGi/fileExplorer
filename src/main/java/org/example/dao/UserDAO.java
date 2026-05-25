package org.example.dao;

import org.example.model.dataSets.UserDataSet;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.HibernateException;

import java.util.List;

public class UserDAO {

    private Session session;

    public UserDAO(Session session) {
        this.session = session;
    }

    public UserDataSet getUserById(long id) throws HibernateException {
        return session.get(UserDataSet.class, id);
    }

    public UserDataSet getUserByLogin(String login) throws HibernateException {
        String hql = "FROM UserDataSet WHERE login = :login";
        Query<UserDataSet> query = session.createQuery(hql, UserDataSet.class);
        query.setParameter("login", login);
        return query.uniqueResult();
    }

    public long insertUser(String login, String hashedPassword, String email) throws HibernateException {
        UserDataSet user = new UserDataSet(login, hashedPassword, email);
        return (Long) session.save(user);
    }

    public boolean updateUser(UserDataSet user) throws HibernateException {
        try {
            session.update(user);
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    public boolean deleteUser(String login) throws HibernateException {
        String hql = "DELETE FROM UserDataSet WHERE login = :login";
        Query<?> query = session.createQuery(hql);
        query.setParameter("login", login);
        return query.executeUpdate() > 0;
    }

    public boolean userExists(String login) throws HibernateException {
        String hql = "SELECT COUNT(*) FROM UserDataSet WHERE login = :login";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("login", login);
        return query.uniqueResult() > 0;
    }

    public List<UserDataSet> getAllUsers() throws HibernateException {
        String hql = "FROM UserDataSet ORDER BY login";
        Query<UserDataSet> query = session.createQuery(hql, UserDataSet.class);
        return query.list();
    }
}