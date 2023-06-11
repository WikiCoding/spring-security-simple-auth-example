package com.example.jdbcpostgres.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record User(Integer id,
                   @JsonProperty("username") String username,
                   @JsonProperty("password") String password,
                   @JsonProperty("token") String token,
                   @JsonProperty("role") String role) {
}
