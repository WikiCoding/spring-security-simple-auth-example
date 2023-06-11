package com.example.jdbcpostgres.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
  @Value("${application.security.jwt.secret-key}")
  private String secretKey;
  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  /** method to extract a username from token **/
  public String extractUsername(String token) {
    System.out.println("jwt service extracts username");
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /** method to generate a token **/
  public String generateToken(UserDetails userDetails) {
    System.out.println("jwt service generates token");
    return generateToken(new HashMap<>(), userDetails);
  }

  /** method to generate a token **/
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    System.out.println("jwt service generates token");
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  /** if we don't want to have any extra Claims we can use the below's method **/
  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(new HashMap<>(), userDetails, refreshExpiration);
  }

  /** method to build a Token**/
  private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
    System.out.println("jwt service builds token");
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  /** method to validate token **/
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    System.out.println("Checking if token is valid -> " + ((username.equals(userDetails.getUsername())) && !isTokenExpired(token)));
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  /** method to validate if token is expired **/
  private boolean isTokenExpired(String token) {
    System.out.println("Checking if token is expired -> " + (extractExpiration(token).before(new Date())));
    return extractExpiration(token).before(new Date());
  }

  /** method to extract expiration from a loaded token **/
  private Date extractExpiration(String token) {
    System.out.println("jwt service extracts Expiration");
    return extractClaim(token, Claims::getExpiration);
  }

  /** method from io.jsonwebtoken.Claims **/
  private Claims extractAllClaims(String token) {
    System.out.println("jwt service extracts all claims");
    return Jwts /** this is from io.jsonwebtoken **/
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /** Key for token claim generation **/
  private Key getSignInKey() {
    System.out.println("jwt secret key, on JWT Service");
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
