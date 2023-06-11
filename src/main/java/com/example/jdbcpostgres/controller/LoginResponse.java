package com.example.jdbcpostgres.controller;

import com.fasterxml.jackson.annotation.JsonProperty;


public class LoginResponse {
    @JsonProperty("username")
    private String username;

    @JsonProperty("token")
    private String token;

    public LoginResponse(String username, String token) {
        this.username = username;
        this.token = token;
    }
}
