package it.unipi.enPassant.controller;



import it.unipi.enPassant.model.requests.LoginRequest;
import it.unipi.enPassant.model.requests.PlayerRequestModel;
import it.unipi.enPassant.model.requests.LoginModel;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.DataService;
import it.unipi.enPassant.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/manager")
public class ManagerController extends GenericUserController{
  private final ManagerService managerService;
  // Constructor for the ManagerController
  @Autowired
  public ManagerController(AuthenticationService authservice, DataService dataservice, AuthenticationManager authenticationManager, ManagerService managerService){
    super(authservice, dataservice, authenticationManager);
    this.managerService= managerService;
  }

  @PostMapping("/addNewPlayer")
  public ResponseEntity<String> addPlayer(@RequestBody String username) {
    try {
      if(managerService.addPlayer(username)){
        return new ResponseEntity<>("added player succesfully", HttpStatus.OK);
      }
      else{
        return new ResponseEntity<>("failed to add player", HttpStatus.UNAUTHORIZED);
      }

    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/disqualifyPlayer")
  public ResponseEntity<String> disqualifyPlayer(@RequestBody String username) {
    try {
      if(managerService.disqualifyPlayer(username)){
        return new ResponseEntity<>("player disqualified succesfully", HttpStatus.OK);
      }
      else{
        return new ResponseEntity<>("failed to disqualify player", HttpStatus.UNAUTHORIZED);
      }

    }catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/deleteMatch")
  public ResponseEntity<String> deleteMatch(@RequestBody String White, String Black) {
    try {
      if(managerService.deleteMatch(White,Black)){
        return new ResponseEntity<>("Match Deleted succesfully", HttpStatus.OK);
      }
      else{
        return new ResponseEntity<>("failed to delete match", HttpStatus.UNAUTHORIZED);
      }

    }catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/createMaindraw")
  public ResponseEntity<String> createMaindraw() {
    try {
      if(managerService.createMaindraw()){
        return new ResponseEntity<>("Main draw created succesfully", HttpStatus.OK);
      }
      else{
        return new ResponseEntity<>("failed to create main draw", HttpStatus.UNAUTHORIZED);
      }

    }catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/checkPlayerRequest")
  public ResponseEntity<PlayerRequestModel> checkPlayerRequest() {
    PlayerRequestModel textRequest = managerService.checkPlayerRequest();
    return new ResponseEntity<>(textRequest, HttpStatus.OK);
  }
}
