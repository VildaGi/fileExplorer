package org.example.db.dao;

import org.example.db.dataSets.UserDataSet;
import org.example.db.executor.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserHandler implements ResultHandler<UserDataSet> {

    @Override
    public UserDataSet handle(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            UserDataSet user = new UserDataSet();
            user.setId(resultSet.getLong("id"));
            user.setLogin(resultSet.getString("login"));
            user.setPassword(resultSet.getString("password"));
            user.setEmail(resultSet.getString("email"));
            return user;
        }
        return null;
    }
}
