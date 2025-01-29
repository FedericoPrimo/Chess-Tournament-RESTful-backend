package it.unipi.enPassant.controller;


import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/manager")
public class ManagerController extends GenericUserController{
  // Services used for authentication and interacting with the databases.
  private final AuthenticationService managerservice;
  private final DataService managerDataService;

  // Constructor for the ManagerController
  @Autowired
  public ManagerController(AuthenticationService managerservice, DataService managerDataService){
    this.managerservice = managerservice;
    this.managerDataService = managerDataService;
  }

  /* Here we have all the API endpoints */
}
