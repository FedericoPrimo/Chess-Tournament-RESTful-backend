package it.unipi.enPassant.controller;

import it.unipi.enPassant.model.requests.LoginModel;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/spectator")
public class SpectatorController extends GenericUserController{

  // Constructor for the SpectatorController
  @Autowired
  public SpectatorController(AuthenticationService authservice, DataService dataservice){
    super(authservice, dataservice);
  }
}
