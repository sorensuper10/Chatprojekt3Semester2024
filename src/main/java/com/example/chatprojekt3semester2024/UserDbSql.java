package com.example.chatprojekt3semester2024;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDbSql {

    private JdbcTemplate jdbcTemplate;

    public UserDbSql(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Finder en bruger i databasen baseret på username og adgangskode til login
    public User findLogin(String username, String password) {
        try {
            String sql = "SELECT * FROM login WHERE username = ? AND password = ?";
            List<User> users = jdbcTemplate.query(sql, new String[]{username, password}, userRowMapper());
            if (!users.isEmpty()) {
                return users.get(0);
            } else {
                return null;
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Mapper rækker fra databasen til User-objekter
    public RowMapper<User> userRowMapper() {
        // En anononym funktion som tager 2 parametre ResultSet og int
        return (rs, rowNum) -> {
            User user = new User();
            user.setUserid(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            return user;
        };
    }
}
