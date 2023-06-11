package com.example.jdbcpostgres.service;

import com.example.jdbcpostgres.model.User;
import com.example.jdbcpostgres.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private final UserRepository repository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final UserService userService;

    public void registerUser(String username, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        repository.insertPerson(username, encodedPassword);
    }

    public User loginUser(String username, String password) {
            String dbPassword = repository.getPersonByUsername(username).password();

            boolean passwordMatches = passwordEncoder.matches(password, dbPassword);

            if (passwordMatches) {
                Authentication auth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password));

                String token = jwtService.generateToken(userService.loadUserByUsername(username));

                return new User(0, username, password, token, "");
            }

        return null;
    }

    public int addToken(String username, String token) {
        repository.addNewToken(username, token);
        return 1;
    }
}
