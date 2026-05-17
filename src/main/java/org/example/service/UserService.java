package org.example.service;

import org.example.model.User;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private static final String USERS_FILE_PATH = System.getProperty("catalina.base") + File.separator + "users.ser";
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    private static UserService instance;

    private UserService() {
        loadUsers();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private void loadUsers() {
        File file = new File(USERS_FILE_PATH);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                ConcurrentHashMap<String, User> loadedUsers = (ConcurrentHashMap<String, User>) ois.readObject();
                users.putAll(loadedUsers);
                System.out.println("Loaded " + users.size() + " users from disk");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading users: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No existing users file found, starting with empty user list");
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE_PATH))) {
            oos.writeObject(users);
            System.out.println("Saved " + users.size() + " users to disk");
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean register(String login, String password, String email) {
        if (users.containsKey(login)) {
            return false;
        }

        User user = new User(login, password, email);
        users.put(login, user);
        saveUsers();

        // Создаем домашнюю папку пользователя
        createUserHomeDirectory(login);

        return true;
    }

    public User login(String login, String password) {
        User user = users.get(login);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    private void createUserHomeDirectory(String login) {
        String homeDirPath = "D:\\filemanager\\" + login;
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
        return "C:\\Users\\Student\\filemanager\\" + login;
    }
}