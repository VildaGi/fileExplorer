package org.example.service;

import org.example.db.DBService;
import org.example.db.dataSets.UserDataSet;
import org.example.db.executor.DBException;
import org.example.model.User;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private final DBService dbService;
    private static UserService instance;

    private UserService() {
        this.dbService = new DBService();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public boolean register(String login, String password, String email) {
        try {
            // Проверяем, существует ли пользователь
            if (dbService.userExists(login)) {
                System.err.println("User already exists: " + login);
                return false;
            }

            // Добавляем пользователя
            long userId = dbService.addUser(login, password, email);

            if (userId > 0) {
                System.out.println("User registered successfully: " + login + " with ID: " + userId);
                createUserHomeDirectory(login);
                return true;
            }
        } catch (DBException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public User login(String login, String password) {
        try {
            UserDataSet userDataSet = dbService.getUserByLoginAndPassword(login, password);

            if (userDataSet != null) {
                System.out.println("User logged in successfully: " + login);

                // Конвертируем в модель User
                return new User(
                        userDataSet.getLogin(),
                        userDataSet.getPassword(),
                        userDataSet.getEmail()
                );
            }
        } catch (DBException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean userExists(String login) {
        try {
            return dbService.userExists(login);
        } catch (DBException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void createUserHomeDirectory(String login) {
        String homeDirPath = getUserHomePath(login);
        File homeDir = new File(homeDirPath);
        if (!homeDir.exists()) {
            boolean created = homeDir.mkdirs();
            if (created) {
                System.out.println("Created home directory for user " + login + ": " + homeDirPath);
            } else {
                System.err.println("Failed to create home directory for user " + login + ": " + homeDirPath);
            }
        }
    }

    public String getUserHomePath(String login) {
        return "D:\\filemanager\\" + login;
    }
}