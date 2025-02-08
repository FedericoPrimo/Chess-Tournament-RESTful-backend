package it.unipi.enPassant.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String roles = "";
    if (authentication != null) {
      roles = authentication.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.joining(", "));
      logger.warn("Access denied for user: {} with roles: {}", authentication.getName(), roles);
    } else {
      logger.warn("Access denied for unauthenticated user.");
    }
    String body = String.format("{\"error\":\"Access denied!\",\"roles\":\"%s\"}", roles);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
  }
}
