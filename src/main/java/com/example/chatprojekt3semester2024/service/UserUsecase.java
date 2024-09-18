package com.example.chatprojekt3semester2024.service;

import com.example.chatprojekt3semester2024.DBController.UserDbSql;
import com.example.chatprojekt3semester2024.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserUsecase {

    @Autowired
    private UserDbSql userDbSql;

    public void createUser(User user) {
        userDbSql.createUser(user);
    }

    public void deleteUser(Long id) {
        userDbSql.deleteUser(id);
    }

    public User findLogin(String username, String password) {
        return userDbSql.findLogin(username, password);
    }
}
