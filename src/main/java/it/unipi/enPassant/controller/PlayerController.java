package it.unipi.enPassant.controller;

import it.unipi.enPassant.model.requests.LoginRequest;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;

// Controller for the Player entity
@RestController
@RequestMapping("api/player")
public class PlayerController extends GenericUserController {
  // Services used for authentication and interacting with the databases.
  private final AuthenticationService playerservice;
  private final DataService playerDataService;

  // Constructor for the PlayerController
  @Autowired
  public PlayerController(AuthenticationService playerservice, DataService playerDataService) {
    this.playerservice = playerservice;
    this.playerDataService = playerDataService;
  }

  /* Here we have all the API endpoints */
  @PostMapping("/login")
  public ResponseEntity<String> loginPlayer(@RequestBody LoginRequest loginRequest) {
    try {

      if(playerservice.PlayerLoginControl(loginRequest)){
        return new ResponseEntity<>("Login successful", HttpStatus.OK);
      }
      else{
        return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
      }

    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // All the endpoints below are for the live game feature, exclusively for the player
  // @PostMapping("/livematch/{gameid}/move") //Example to insert a move in a live game
}
