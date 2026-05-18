package org.example.db;

import org.example.db.dao.UserDAO;
import org.example.db.dataSets.UserDataSet;
import org.example.db.executor.DBException;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBService {
    private final Connection connection;

    public DBService() {
        this.connection = getPostgresConnection();
    }

    public UserDataSet getUser(long id) throws DBException {
        try {
            return new UserDAO(connection).get(id);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public UserDataSet getUserByLogin(String login) throws DBException {
        try {
            return new UserDAO(connection).getByLogin(login);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public UserDataSet getUserByLoginAndPassword(String login, String password) throws DBException {
        try {
            return new UserDAO(connection).getByLoginAndPassword(login, password);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public long addUser(String login, String password, String email) throws DBException {
        try {
            connection.setAutoCommit(false);
            UserDAO dao = new UserDAO(connection);
            dao.createTable(); // Таблица создастся только если её нет
            long userId = dao.insertUser(login, password, email);
            connection.commit();
            return userId;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }


    public boolean userExists(String login) throws DBException {
        try {
            return new UserDAO(connection).exists(login);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getPostgresConnection() {
        try {
            String url = "jdbc:postgresql://localhost:5432/filemanager";
            String username = "postgres";
            String password = "your_password";

            // Регистрируем драйвер
            DriverManager.registerDriver((Driver) Class.forName("org.postgresql.Driver").newInstance());

            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to PostgreSQL database");
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
}