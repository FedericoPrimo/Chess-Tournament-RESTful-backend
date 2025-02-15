package it.unipi.enPassant.controller.mongoController;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.enPassant.service.AuthenticationService;
import it.unipi.enPassant.service.mongoService.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/manager")
@Tag(name = "Manager", description = "Manager operations")
public class ManagerController extends GenericUserController {
  // Constructor for the ManagerController
  @Autowired
  public ManagerController(AuthenticationService authservice, DataService dataservice){
    super(authservice, dataservice);
  }
}
