package com.example.jdbcpostgres.service;

import com.example.jdbcpostgres.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final UserRepository repository;

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    final String authHeader = request.getHeader("Authorization");

    final String user = repository.findUserByJwtToken(authHeader);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }

    if (user != null) {
      /** removes the token from the db so it cannot be fetched from the client **/
      repository.addNewToken(user, null);
      SecurityContextHolder.clearContext();
    }
  }
}
