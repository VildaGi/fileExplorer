package org.example.service;

import org.example.model.User;

import org.mindrot.jbcrypt.BCrypt;

import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private static final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public boolean register(String login, String password, String email) {
        if (users.containsKey(login))
            return false;

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        users.put(login, new User(login, hashedPassword, email));
        return true;
    }

    public User login(String login, String password) {
        User user = users.get(login);

        if (user != null && BCrypt.checkpw(password, user.getHashedPassword()))
            return user;

        return null;
    }
}