package it.unipi.enPassant.controller.mongoController;

import it.unipi.enPassant.model.requests.LoginRequest;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.mongoService.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller for the Player entity
@RestController
@RequestMapping("api/player")
public class PlayerController extends GenericUserController {

  // Constructor for the PlayerController
  @Autowired
  public PlayerController(AuthenticationService authservice, DataService dataservice) {
    super(authservice, dataservice);
  }

  /* Here we have all the API endpoints */
  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
    try {
      if(authservice.PlayerLoginControl(loginRequest)){
        return new ResponseEntity<>("Login successful", HttpStatus.OK);
      }
      else{
        return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
      }

    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
