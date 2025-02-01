package it.unipi.enPassant.controller;
import it.unipi.enPassant.model.requests.DocumentUser;
import it.unipi.enPassant.repositories.CRUDrepositoryUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/userCRUD")
public class CRUDcontrollerUser extends CRUDcontroller<DocumentUser, String> {
    public CRUDcontrollerUser(CRUDrepositoryUser repository) {
        super(repository);
    }
}
