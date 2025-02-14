package it.unipi.enPassant.service;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

  private static String secretKey = "";

  public JWTService() {
    try {
      KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
      SecretKey sk = keyGen.generateKey();
      secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public static String generatetoken(String username) {

    Map<String, Object> claims = new HashMap<>();

    return Jwts.builder()
            .claims()
            .add(claims)
            .subject(username)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .and()
            .signWith(getKey())
            .compact();
  }

  private static SecretKey getKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public static boolean validateToken(String token) {
    try {
      final String username = extractUsername(token);
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false; // Invalid token
    }
  }

  public static String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private static <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
    final Claims claims = extractAllClaims(token);
    return claimResolver.apply(claims);
  }

  private static Claims extractAllClaims(String token) {
    return Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }


  private static boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private static Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

}