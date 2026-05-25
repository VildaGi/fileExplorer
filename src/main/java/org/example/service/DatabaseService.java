package org.example.service;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseService {
    private static String url;
    private static String username;
    private static String password;

    static {
        try {
            Properties props = new Properties();
            try (InputStream input = DatabaseService.class.getClassLoader()
                    .getResourceAsStream("application.properties")) {
                if (input != null) {
                    props.load(input);
                }
            }

            url = props.getProperty("db.url", "jdbc:postgresql://localhost:5432/file_explorer");
            username = props.getProperty("db.username", "postgres");
            password = props.getProperty("db.password", "password");

            Class.forName("org.postgresql.Driver");
            createUsersTable();

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private static void createUsersTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                login VARCHAR(50) UNIQUE NOT NULL,
                hashed_password VARCHAR(255) NOT NULL,
                email VARCHAR(100) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("Failed to create users table: " + e.getMessage());
        }
    }
}