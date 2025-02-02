package it.unipi.enPassant.controller;

import it.unipi.enPassant.model.requests.StatsModel;
import it.unipi.enPassant.model.requests.DataUserModel;
import it.unipi.enPassant.model.requests.LoginModel;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.DataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/* Abstract class, not a spring bean so it must be called by its child*/
public abstract class GenericUserController {
  protected final AuthenticationService authservice;
  protected final DataService dataservice;
  private final AuthenticationManager authenticationManager;

  protected GenericUserController(AuthenticationService authservice, DataService dataservice, AuthenticationManager authenticationManager) {
    this.authservice = authservice;
    this.dataservice = dataservice;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/login")
  protected ResponseEntity<String> login(@RequestBody LoginModel loginModel) {
    try {

      // Create a new UsernamePasswordAuthenticationToken with the username and password
      // and authenticate using spring security
      UsernamePasswordAuthenticationToken authReq =
              new UsernamePasswordAuthenticationToken(
                      loginModel.getUsername(),
                      loginModel.getPassword()
              );
      Authentication auth = authenticationManager.authenticate(authReq);

      if (auth.isAuthenticated()) {
        return ResponseEntity.ok("Login successful");
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
      }
    }
    catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
    catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }

  @PostMapping("/logout")
  protected ResponseEntity<String> logout(){
    // Still not implemented
    return ResponseEntity.ok("Logout successful");
  }

  @GetMapping("/viewData/{username}")
  protected ResponseEntity<DataUserModel> viewData(@PathVariable String username){
    DataUserModel data = dataservice.dataUserGet(username);

    if (data==null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    return ResponseEntity.ok(data);
  }

  @GetMapping("/viewUser")
  public ResponseEntity<List<String>> getAllUserIds(){
    List<String> data = dataservice.getAllUserIds();

    if (data==null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    return ResponseEntity.ok(data);
  }

  @GetMapping("/searchPlayerStats/{username}")
  protected ResponseEntity<StatsModel> searchPlayerStats(@PathVariable String username){
    StatsModel stats = dataservice.dataGetPlayerStats(username);

    if (stats==null) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    return ResponseEntity.ok(stats);
  }
}

