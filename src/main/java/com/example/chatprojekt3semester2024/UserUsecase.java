package com.example.chatprojekt3semester2024;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserUsecase {

    @Autowired
    private UserDbSql userDbSql;

    public User findLogin(String username, String password) {
        return userDbSql.findLogin(username, password);
    }
}
