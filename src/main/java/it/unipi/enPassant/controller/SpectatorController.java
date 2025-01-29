package it.unipi.enPassant.controller;

import it.unipi.enPassant.model.requests.LoginRequest;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/spectator")
public class SpectatorController {
  // Services used for authentication and interacting with the databases.
  private final AuthenticationService spectatorservice;
  private final DataService spectatorDataService;

  // Constructor for the SpectatorController
  @Autowired
  public SpectatorController(AuthenticationService spectatorservice, DataService spectatorDataService){
    this.spectatorservice = spectatorservice;
    this.spectatorDataService = spectatorDataService;
  }

  @PostMapping("/login")
  public ResponseEntity<String> loginSpectator(@RequestBody LoginRequest loginRequest) {
    try {

      if(spectatorservice.spectatorLoginControl(loginRequest)){
        return new ResponseEntity<>("Login successful", HttpStatus.OK);
      }
      else{
        return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
      }

    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /* Here we have all the API endpoints */

}
