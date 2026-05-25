package org.example.service;

import org.example.model.User;
import org.example.model.dataSets.UserDataSet;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final DatabaseService dbService;

    public UserService() {
        this.dbService = new DatabaseService();
        dbService.printConnectInfo();
    }

    public boolean register(String login, String password, String email) {
        try {
            if (dbService.userExists(login)) {
                return false;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            dbService.addUser(login, hashedPassword, email);
            return true;

        } catch (DBException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User login(String login, String password) {
        try {
            UserDataSet userDataSet = dbService.getUserByLogin(login);

            if (userDataSet != null && BCrypt.checkpw(password, userDataSet.getHashedPassword())) {
                return new User(
                        userDataSet.getLogin(),
                        userDataSet.getHashedPassword(),
                        userDataSet.getEmail()
                );
            }

        } catch (DBException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean userExists(String login) {
        try {
            return dbService.userExists(login);
        } catch (DBException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String login) {
        try {
            return dbService.deleteUser(login);
        } catch (DBException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void shutdown() {
        dbService.shutdown();
    }
}