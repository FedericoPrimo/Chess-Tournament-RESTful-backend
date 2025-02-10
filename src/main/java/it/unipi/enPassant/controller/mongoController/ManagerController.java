package it.unipi.enPassant.controller.mongoController;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.mongoService.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/manager")
public class ManagerController extends GenericUserController {
  // Constructor for the ManagerController
  @Autowired
  public ManagerController(AuthenticationService authservice, DataService dataservice){
    super(authservice, dataservice);
  }
}
