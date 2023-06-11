package com.example.jdbcpostgres.repository;

import com.example.jdbcpostgres.model.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@AllArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    private static User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("token"),
                rs.getString("role"));
    }

    public List<User> getAllPeople() {
        String query = "SELECT * FROM public.\"authTable\"";
        List<User> people = jdbcTemplate.query(query, UserRepository::mapRow);
        return people;
    }

    public void insertPerson(String username, String password) {
        String query = "INSERT INTO public.\"authTable\" (username, password) VALUES (?, ?)";
        jdbcTemplate.update(query, username, password);
    }

    public User getPersonByUsername(String user) {
        try {
            String query = "SELECT * FROM public.\"authTable\" WHERE username=?";
            User foundUser = jdbcTemplate.queryForObject(query, new Object[]{user},
                    UserRepository::mapRow);

            return foundUser;
        }catch (UsernameNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    public void addNewToken(String username, String token) {
        try {
            String query = "UPDATE public.\"authTable\" SET token=? WHERE username=?";
            jdbcTemplate.update(query, token, username);
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String findUserByJwtToken(String token){
        String jwt = token.substring(7);
        String query = "SELECT * FROM public.\"authTable\" WHERE token=?";
        User foundUser = jdbcTemplate.queryForObject(query, new Object[]{jwt}, UserRepository::mapRow);
        assert foundUser != null;
        return foundUser.username();
    }
}
