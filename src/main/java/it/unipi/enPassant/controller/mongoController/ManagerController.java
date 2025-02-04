package it.unipi.enPassant.controller.mongoController;


import it.unipi.enPassant.model.requests.LoginRequest;
import it.unipi.enPassant.model.requests.redisModel.PlayerRequestModel;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.mongoService.DataService;
import it.unipi.enPassant.service.mongoService.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/manager")
public class ManagerController extends GenericUserController {
  private final ManagerService managerService;
  // Constructor for the ManagerController
  @Autowired
  public ManagerController(AuthenticationService authservice, DataService dataservice, ManagerService managerService) {
    super(authservice, dataservice);
    this.managerService= managerService;
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

}
