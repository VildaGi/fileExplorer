package org.example.service;

import org.example.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    public boolean register(String login, String password, String email) {
        String sql = "INSERT INTO users (login, hashed_password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, email);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Unique violation
                System.out.println("User already exists: " + login);
                return false;
            }
            e.printStackTrace();
            return false;
        }
    }

    public User login(String login, String password) {
        String sql = "SELECT login, hashed_password, email FROM users WHERE login = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("hashed_password");

                if (BCrypt.checkpw(password, hashedPassword)) {
                    return new User(
                            rs.getString("login"),
                            rs.getString("hashed_password"),
                            rs.getString("email")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean userExists(String login) {
        String sql = "SELECT 1 FROM users WHERE login = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String login) {
        String sql = "DELETE FROM users WHERE login = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT login, hashed_password, email FROM users ORDER BY login";

        try (Connection conn = DatabaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("login"),
                        rs.getString("hashed_password"),
                        rs.getString("email")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
}