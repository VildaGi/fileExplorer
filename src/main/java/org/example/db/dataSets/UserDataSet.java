package org.example.db.dataSets;

/**
 * DataSet для пользователя (только логин, пароль и email)
 */
public class UserDataSet {
    private long id;
    private String login;
    private String password;
    private String email;

    public UserDataSet() {
    }

    public UserDataSet(long id, String login, String password, String email) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public UserDataSet(String login, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
    }

    // Геттеры
    public long getId() { return id; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }

    // Сеттеры
    public void setId(long id) { this.id = id; }
    public void setLogin(String login) { this.login = login; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "UserDataSet{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}