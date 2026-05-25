package org.example.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String login;
    private String hashedPassword;
    private String email;

    public User(String login, String hashedPassword, String email) {
        this.login = login;
        this.hashedPassword = hashedPassword;
        this.email = email;
    }

    public String getLogin() { return login; }
    public String getHashedPassword() { return hashedPassword; }
    public String getEmail() { return email; }

}