package it.unipi.enPassant.controller.mongoController.mongoCRUD;
import it.unipi.enPassant.model.requests.mongoModel.user.DocumentUser;
import it.unipi.enPassant.repositories.CRUDrepositoryUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/userCRUD")
public class CRUDcontrollerUser extends CRUDcontroller<DocumentUser, String> {
    private final PasswordEncoder passwordEncoder;

    public CRUDcontrollerUser(CRUDrepositoryUser repository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @PostMapping("/create")
    public DocumentUser create(@RequestBody DocumentUser entity) {
        String password = entity.getPassword();
        String hashedPsw = passwordEncoder.encode(password);
        entity.setPassword(hashedPsw);
        return super.create(entity);
    }
}
