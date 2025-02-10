package it.unipi.enPassant.controller.mongoController;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.mongoService.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/spectator")
public class SpectatorController extends GenericUserController {

  // Constructor for the SpectatorController
  @Autowired
  public SpectatorController(AuthenticationService authservice, DataService dataservice){
    super(authservice, dataservice);
  }
}
