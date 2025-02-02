package it.unipi.enPassant.controller;

import it.unipi.enPassant.model.requests.LoginModel;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

// Controller for the Player entity
@RestController
@RequestMapping("api/player")
public class PlayerController extends GenericUserController {

  // Constructor for the PlayerController
  @Autowired
  public PlayerController(AuthenticationService authservice, DataService dataservice, AuthenticationManager authenticationManager) {
    super(authservice, dataservice, authenticationManager);
  }


}
