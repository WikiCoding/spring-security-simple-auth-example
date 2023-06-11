package com.example.jdbcpostgres.controller;

import com.example.jdbcpostgres.model.User;
import com.example.jdbcpostgres.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@CrossOrigin
@AllArgsConstructor
public class UserController {
    private final AuthService authService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public ResponseEntity<String> insert(@RequestBody User user) {
        authService.registerUser(user.username(), user.password());
        return ResponseEntity.status(201).body("Inserted");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody User user) {
        User userWithToken = authService.loginUser(user.username(), user.password());
        int updateResponse = authService.addToken(user.username(), userWithToken.token());
        if (updateResponse != 1) return null;

        return ResponseEntity.ok(new LoginResponse(user.username(), userWithToken.token()));
    }
}
