package it.unipi.enPassant.controller;

import it.unipi.enPassant.model.requests.StatsModel;
import it.unipi.enPassant.model.requests.DataUserModel;
import it.unipi.enPassant.model.requests.LoginRequest;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.DataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/* Abstract class, not a spring bean so it must be called by its child*/
public abstract class GenericUserController {
  protected final AuthenticationService authservice;
  protected final DataService dataservice;

  protected GenericUserController(AuthenticationService authservice, DataService dataservice) {
    this.authservice = authservice;
    this.dataservice = dataservice;
  }

  @PostMapping("/logout")
  protected ResponseEntity<String> logout(){
    return ResponseEntity.ok("Logout successful");
  }

  public abstract ResponseEntity<String> login(@RequestBody LoginRequest loginRequest);

  @GetMapping("/viewData/{username}")
  public ResponseEntity<DataUserModel> viewData(@PathVariable String username){
    DataUserModel data = dataservice.dataUserGet(username);

    if (data==null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    return ResponseEntity.ok(data);
  }

  @GetMapping("/searchPlayerStats/{username}")
  public ResponseEntity<StatsModel> searchPlayerStats(@PathVariable String username){
    StatsModel stats = dataservice.dataGetPlayerStats(username);

    if (stats==null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    return ResponseEntity.ok(stats);
  }
}

