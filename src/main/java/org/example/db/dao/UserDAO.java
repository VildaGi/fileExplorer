package org.example.db.dao;

import org.example.db.dataSets.UserDataSet;
import org.example.db.executor.Executor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DAO для работы с пользователями
 */
public class UserDAO {
    private final Executor executor;

    public UserDAO(Connection connection) {
        this.executor = new Executor(connection);
    }

    /**
     * Создание таблицы пользователей
     */
    public void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    login VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(100) NOT NULL
                )
                """;

        String createIndex = """
                CREATE INDEX IF NOT EXISTS idx_users_login ON users(login)
                """;

        executor.execUpdate(sql);
        executor.execUpdate(createIndex);
        System.out.println("Table 'users' is ready");
    }

    /**
     * Получение пользователя по ID
     */
    public UserDataSet get(long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        return executor.execQuery(sql, new UserHandler(), id);
    }

    /**
     * Получение пользователя по логину
     */
    public UserDataSet getByLogin(String login) throws SQLException {
        String sql = "SELECT * FROM users WHERE login = ?";
        return executor.execQuery(sql, new UserHandler(), login);
    }

    /**
     * Получение пользователя по логину и паролю (для авторизации)
     */
    public UserDataSet getByLoginAndPassword(String login, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE login = ? AND password = ?";
        return executor.execQuery(sql, new UserHandler(), login, password);
    }

    /**
     * Вставка нового пользователя
     *
     * @return ID созданного пользователя
     */
    public long insertUser(String login, String password, String email) throws SQLException {
        String sql = "INSERT INTO users (login, password, email) VALUES (?, ?, ?)";
        return executor.execInsert(sql, login, password, email);
    }

    /**
     * Получение ID пользователя по логину
     */
    public long getUserId(String login) throws SQLException {
        String sql = "SELECT id FROM users WHERE login = ?";
        return executor.execQuery(sql, resultSet -> {
            if (resultSet.next()) {
                return resultSet.getLong("id");
            }
            return -1L;
        }, login);
    }

    /**
     * Проверка существования пользователя
     */
    public boolean exists(String login) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE login = ?";
        return executor.execQuery(sql, resultSet -> {
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        }, login);
    }

}