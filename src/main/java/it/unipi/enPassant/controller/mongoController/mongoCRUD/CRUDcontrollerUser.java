package it.unipi.enPassant.controller.mongoController.mongoCRUD;
import it.unipi.enPassant.model.requests.mongoModel.user.DocumentUser;
import it.unipi.enPassant.repositories.CRUDrepositoryUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Override
    @PutMapping("/update/{id}")
    public ResponseEntity<DocumentUser> update(@PathVariable String id, @RequestBody DocumentUser entity) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        String password = entity.getPassword();
        String hashedPsw = passwordEncoder.encode(password);
        entity.setPassword(hashedPsw);
        return ResponseEntity.ok(repository.save(entity));
    }
}
