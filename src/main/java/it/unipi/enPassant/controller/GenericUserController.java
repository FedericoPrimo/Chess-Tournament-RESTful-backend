package it.unipi.enPassant.controller;

import it.unipi.enPassant.model.requests.LoginRequest;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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

}
