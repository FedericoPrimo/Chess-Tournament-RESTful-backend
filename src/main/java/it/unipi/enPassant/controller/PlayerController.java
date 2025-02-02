package it.unipi.enPassant.controller;

import it.unipi.enPassant.model.requests.LoginModel;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.DataService;
import it.unipi.enPassant.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

// Controller for the Player entity
@RestController
@RequestMapping("api/player")
public class PlayerController extends GenericUserController {

  private final PlayerService playerService;

  // Constructor for the PlayerController
  @Autowired
  public PlayerController(AuthenticationService authservice, DataService dataservice, AuthenticationManager authenticationManager, PlayerService playerService) {
    super(authservice, dataservice, authenticationManager);
    this.playerService = playerService;
  }

  @PostMapping("/enrollCategory")
  public ResponseEntity<String> enrollCategory(@RequestBody String username, String category) {
    try {
      if(playerService.enrollCategory(username, category)){
        return new ResponseEntity<>("Enroll submitted :)", HttpStatus.OK);
      }
      else{
        return new ResponseEntity<>("please try again something goes wrong", HttpStatus.UNAUTHORIZED);
      }

    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  @PostMapping("/insertMove")
  public ResponseEntity<String> insertMove(@RequestBody String username, String category, String move) {
    try {
      if(playerService.insertMove(username, category, move)){
        return new ResponseEntity<>("move submitted", HttpStatus.OK);
      }
      else{
        return new ResponseEntity<>("please try again something goes wrong", HttpStatus.UNAUTHORIZED);
      }

    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
