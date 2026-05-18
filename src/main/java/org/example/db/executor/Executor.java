package org.example.db.executor;

import java.sql.*;

public class Executor {
    private final Connection connection;

    public Executor(Connection connection) {
        this.connection = connection;
    }

    /**
     * Выполнение запроса с возвратом результата
     */
    public <T> T execQuery(String query, ResultHandler<T> handler, Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            setParameters(stmt, params);
            ResultSet resultSet = stmt.executeQuery();
            return handler.handle(resultSet);
        }
    }

    /**
     * Выполнение запроса на обновление данных
     */
    public int execUpdate(String query, Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            setParameters(stmt, params);
            return stmt.executeUpdate();
        }
    }

    /**
     * Выполнение INSERT запроса с возвратом сгенерированного ключа
     */
    public long execInsert(String query, Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(stmt, params);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
                return -1;
            }
        }
    }

    /**
     * Установка параметров в PreparedStatement
     */
    private void setParameters(PreparedStatement stmt, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}
