package it.unipi.enPassant.service;

import it.unipi.enPassant.model.requests.LoginRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
  // @Autowire ai servizi che ci servono per mongo e redis

  /////////////////////////////////////////////////////////////////////////////
  public boolean PlayerLoginControl(LoginRequest loginRequest) {
    // Prendi da mongo password corrisposta a username e fai il check
    return true;
  }

  public boolean spectatorLoginControl(LoginRequest loginRequest) {
    // Prendi da mongo password corrisposta a username e fai il check
    return true;
  }

}

