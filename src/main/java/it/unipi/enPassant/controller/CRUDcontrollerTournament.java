package it.unipi.enPassant.controller;
import it.unipi.enPassant.model.requests.DocumentMatch;
import it.unipi.enPassant.model.requests.DocumentTournament;
import it.unipi.enPassant.repositories.CRUDrepositoryTournament;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/tournamentsCRUD")
public class CRUDcontrollerTournament extends CRUDcontroller<DocumentTournament, String> {
    public CRUDcontrollerTournament(CRUDrepositoryTournament repository) {
        super(repository);
    }
    @PatchMapping("/{id}/addRawMatch")
    public ResponseEntity<DocumentTournament> addRawMatchToTournament(@PathVariable String id, @RequestBody DocumentMatch newMatch) {
        Optional<DocumentTournament> optionalTournament = repository.findById(id);

        if (optionalTournament.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DocumentTournament tournament = optionalTournament.get();
        tournament.getRawMatches().add(newMatch); // Aggiungi il nuovo match alla lista esistente
        repository.save(tournament); // Salva il documento aggiornato

        return ResponseEntity.ok(tournament);
    }
}
