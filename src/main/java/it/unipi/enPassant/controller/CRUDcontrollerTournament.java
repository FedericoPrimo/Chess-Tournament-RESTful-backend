package it.unipi.enPassant.controller;
import it.unipi.enPassant.model.requests.DocumentTournament;
import it.unipi.enPassant.repositories.CRUDrepositoryTournament;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/tournamentsCRUD")
public class CRUDcontrollerTournament extends CRUDcontroller<DocumentTournament, String> {
    public CRUDcontrollerTournament(CRUDrepositoryTournament repository) {
        super(repository);
    }
}
