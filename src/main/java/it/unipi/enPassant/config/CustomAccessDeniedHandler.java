package it.unipi.enPassant.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

  @Override
  public void handle(HttpServletRequest request,
                     HttpServletResponse response,
                     AccessDeniedException accessDeniedException) throws IOException, ServletException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String roles = "";
    if (authentication != null) {
      roles = authentication.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.joining(", "));
      logger.warn("Accesso negato per l'utente: {} con ruoli: {}", authentication.getName(), roles);
    } else {
      logger.warn("Accesso negato per utente non autenticato.");
    }
    // Imposta il content type e lo status
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    // Crea un body in formato JSON (puoi adattarlo secondo le tue esigenze)
    String jsonBody = String.format("{\"error\":\"Accesso negato!\",\"roles\":\"%s\"}", roles);

    // Scrivi il body nella response
    PrintWriter writer = response.getWriter();
    writer.write(jsonBody);
    writer.flush();
  }
}