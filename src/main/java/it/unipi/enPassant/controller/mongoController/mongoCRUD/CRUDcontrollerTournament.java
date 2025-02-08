package it.unipi.enPassant.controller.mongoController.mongoCRUD;

import it.unipi.enPassant.model.requests.DocumentMatch;
import it.unipi.enPassant.model.requests.DocumentTournament;
import it.unipi.enPassant.repositories.CRUDrepositoryTournament;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/tournamentsCRUD")
public class CRUDcontrollerTournament extends CRUDcontroller<DocumentTournament, String> {
    public CRUDcontrollerTournament(CRUDrepositoryTournament repository) {
        super(repository);
    }
    @PatchMapping("/{id}/addRawMatch")
    public ResponseEntity<DocumentTournament> addRawMatchesToTournament(@PathVariable String id, @RequestBody List<DocumentMatch> newMatches) {
        Optional<DocumentTournament> optionalTournament = repository.findById(id);

        if (optionalTournament.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DocumentTournament tournament = optionalTournament.get();

        // Inizializza rawMatches se è null (per evitare NullPointerException)
        if (tournament.getRawMatches() == null) {
            tournament.setRawMatches(new ArrayList<>());
        }

        // Aggiunge tutti i nuovi match alla lista esistente
        tournament.getRawMatches().addAll(newMatches);
        repository.save(tournament); // Salva il documento aggiornato

        return ResponseEntity.ok(tournament);
    }
}
