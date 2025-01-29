package it.unipi.enPassant.controller;

import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
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

  /* Here we have all the API endpoints */

}
