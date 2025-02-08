package it.unipi.enPassant.controller.mongoController.mongoCRUD;


import it.unipi.enPassant.model.requests.mongoModel.tournament.DocumentMatch;
import it.unipi.enPassant.model.requests.mongoModel.tournament.DocumentTournament;
import it.unipi.enPassant.repositories.CRUDrepositoryTournament;
import it.unipi.enPassant.repositories.UserUpdateRepository;
import it.unipi.enPassant.service.mongoService.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;

import java.util.*;

@RestController
@RequestMapping("api/tournamentsCRUD")
public class CRUDcontrollerTournament extends CRUDcontroller<DocumentTournament, String> {

    @Autowired
    private final UserUpdateService userUpdateService;

    public CRUDcontrollerTournament(CRUDrepositoryTournament repository, UserUpdateService userUpdateService) {
        super(repository);
        this.userUpdateService = userUpdateService;
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

        // Raccolta i giocatori coinvolti senza duplicati
        Set<String> uniquePlayers = new HashSet<>();
        for (DocumentMatch match : newMatches) {
            uniquePlayers.add(match.getWhite()); // Aggiunge il giocatore che gioca con il bianco
            uniquePlayers.add(match.getBlack()); // Aggiunge il giocatore che gioca con il nero
        }

        // Aggiunge tutti i nuovi match alla lista esistente
        tournament.getRawMatches().addAll(newMatches);

        repository.save(tournament); // Salva il documento aggiornato

        //Aggiorna i campi dei giocatori coinvolti
        for (String player : uniquePlayers) {
            userUpdateService.updateFields(player);
        }

        return ResponseEntity.ok(tournament);
    }

    @Override
    @PostMapping("/create")
    public DocumentTournament create(@RequestBody DocumentTournament tournament) {
        DocumentTournament newTournament = super.create(tournament);

        // Verifica se il torneo ha rawMatches e raccoglie i giocatori unici
        if (newTournament.getRawMatches() != null) {
            Set<String> uniquePlayers = new HashSet<>();
            for (DocumentMatch match : newTournament.getRawMatches()) {
                uniquePlayers.add(match.getWhite()); // Aggiunge il giocatore con il bianco
                uniquePlayers.add(match.getBlack()); // Aggiunge il giocatore con il nero
            }

            // Aggiornamento Campi Player
            for (String player : uniquePlayers) {
                userUpdateService.updateFields(player);
            }
        }
        return newTournament; // Salva il torneo aggiornato
    }

    @Override
    @PutMapping("update/{id}")
    public ResponseEntity<DocumentTournament> update(@PathVariable String id, @RequestBody DocumentTournament entity) {
        ResponseEntity<DocumentTournament> response = super.update(id, entity);
        if (response.getStatusCode().is2xxSuccessful() && entity.getRawMatches() != null) {
            Set<String> uniquePlayers = new HashSet<>();
            for (DocumentMatch match : entity.getRawMatches()) {
                uniquePlayers.add(match.getWhite()); // Aggiunge il giocatore con il bianco
                uniquePlayers.add(match.getBlack()); // Aggiunge il giocatore con il nero
            }

            // Aggiornamento Campi Player
            for (String player : uniquePlayers) {
                userUpdateService.updateFields(player);
            }
        }
        return response;
    }

    @Override
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {

        // Recupera il torneo prima di eliminarlo
        Optional<DocumentTournament> optionalTournament = repository.findById(id);

        if (optionalTournament.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tournament with ID " + id + " not found"));
        }

        DocumentTournament tournament = optionalTournament.get();
        Set<String> uniquePlayers = new HashSet<>();
        for (DocumentMatch match : tournament.getRawMatches()) {
            uniquePlayers.add(match.getWhite()); // Aggiunge il giocatore con il bianco
            uniquePlayers.add(match.getBlack()); // Aggiunge il giocatore con il nero
        }

        // Esegue la cancellazione
        ResponseEntity<Map<String, String>> response = super.delete(id);

        if (response.getStatusCode().is2xxSuccessful() && tournament.getRawMatches() != null) {
            // Aggiornamento Campi Player
            for (String player : uniquePlayers) {
                userUpdateService.updateFields(player);
            }
        }

        return response;
    }

    @DeleteMapping("/{id}/deleteRawMatch/{match}")
    public ResponseEntity<DocumentTournament> deleteRawMatchFromTournament(@PathVariable String id, @PathVariable String match) {
        Optional<DocumentTournament> optionalTournament = repository.findById(id);

        if (optionalTournament.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DocumentTournament tournament = optionalTournament.get();

        // Verifica che rawMatches non sia null
        if (tournament.getRawMatches() == null || tournament.getRawMatches().isEmpty()) {
            return ResponseEntity.badRequest().body(tournament);
        }

        // Parsiamo il parametro match per ottenere White e Black
        String[] players = match.split("-");
        if (players.length != 2) {
            return ResponseEntity.badRequest().build(); // Formato non valido
        }

        String whitePlayer = players[0];
        String blackPlayer = players[1];

        // Cerca il match da eliminare
        DocumentMatch matchToRemove = null;
        for (DocumentMatch m : tournament.getRawMatches()) {
            if (m.getWhite().equals(whitePlayer) && m.getBlack().equals(blackPlayer)) {
                matchToRemove = m;
                break;
            }
        }

        // Se il match non viene trovato, ritorna un errore
        if (matchToRemove == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(tournament);
        }

        // Rimuove il match trovato
        tournament.getRawMatches().remove(matchToRemove);
        repository.save(tournament); // Salva il torneo aggiornato

        // Aggiorna le statistiche dei giocatori coinvolti
        userUpdateService.updateFields(whitePlayer);
        userUpdateService.updateFields(blackPlayer);

        return ResponseEntity.ok(tournament);
    }


}



