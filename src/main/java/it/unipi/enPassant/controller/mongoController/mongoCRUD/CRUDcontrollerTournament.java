package it.unipi.enPassant.controller.mongoController.mongoCRUD;


import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.enPassant.model.requests.mongoModel.tournament.DocumentMatch;
import it.unipi.enPassant.model.requests.mongoModel.tournament.DocumentTournament;
import it.unipi.enPassant.repositories.CRUDrepositoryTournament;
import it.unipi.enPassant.service.mongoService.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/tournamentsCRUD")
@Tag(name = "Tournament CRUD", description = "CRUD operations for Tournament documents")
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

        // Initialize rawMatches if it is null (to avoid NullPointerException)
        if (tournament.getRawMatches() == null) {
            tournament.setRawMatches(new ArrayList<>());
        }

        // Collect unique players involved without duplicates
        Set<String> uniquePlayers = new HashSet<>();
        for (DocumentMatch match : newMatches) {
            uniquePlayers.add(match.getWhite()); // Adds the player playing as White
            uniquePlayers.add(match.getBlack()); // Adds the player playing as Black
        }

        // Add all new matches to the existing list
        tournament.getRawMatches().addAll(newMatches);

        repository.save(tournament); // Save the updated document

        // Update fields for involved players
        for (String player : uniquePlayers) {
            userUpdateService.updateFields(player);
        }

        return ResponseEntity.ok(tournament);
    }

    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DocumentTournament tournament) {
        if (repository.existsById(tournament.getId())) {
            return ResponseEntity.badRequest().body("Tournament already exists");
        }

        DocumentTournament newTournament = repository.save(tournament);

        // Check if the tournament has rawMatches and collect unique players
        if (newTournament.getRawMatches() != null) {
            Set<String> uniquePlayers = new HashSet<>();
            for (DocumentMatch match : newTournament.getRawMatches()) {
                uniquePlayers.add(match.getWhite()); // Adds the player playing as White
                uniquePlayers.add(match.getBlack()); // Adds the player playing as Black
            }

            // Update Player Fields
            for (String player : uniquePlayers) {
                userUpdateService.updateFields(player);
            }
        }
        return ResponseEntity.ok(newTournament); // Save the updated tournament
    }

    @Override
    @PutMapping("update/{id}")
    public ResponseEntity<DocumentTournament> update(@PathVariable String id, @RequestBody DocumentTournament entity) {
        ResponseEntity<DocumentTournament> response = super.update(id, entity);
        if (response.getStatusCode().is2xxSuccessful() && entity.getRawMatches() != null) {
            Set<String> uniquePlayers = new HashSet<>();
            for (DocumentMatch match : entity.getRawMatches()) {
                uniquePlayers.add(match.getWhite()); // Adds the player playing as White
                uniquePlayers.add(match.getBlack()); // Adds the player playing as Black
            }

            // Update Player Fields
            for (String player : uniquePlayers) {
                userUpdateService.updateFields(player);
            }
        }
        return response;
    }

    @Override
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {

        // Retrieve the tournament before deleting it
        Optional<DocumentTournament> optionalTournament = repository.findById(id);

        if (optionalTournament.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tournament with ID " + id + " not found"));
        }

        DocumentTournament tournament = optionalTournament.get();
        Set<String> uniquePlayers = new HashSet<>();
        for (DocumentMatch match : tournament.getRawMatches()) {
            uniquePlayers.add(match.getWhite()); // Adds the player playing as White
            uniquePlayers.add(match.getBlack()); // Adds the player playing as Black
        }

        // Perform deletion
        ResponseEntity<Map<String, String>> response = super.delete(id);

        if (response.getStatusCode().is2xxSuccessful() && tournament.getRawMatches() != null) {
            // Update Player Fields
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

        // Check that rawMatches is not null
        if (tournament.getRawMatches() == null || tournament.getRawMatches().isEmpty()) {
            return ResponseEntity.badRequest().body(tournament);
        }

        // Parse the match parameter to obtain White and Black
        String[] players = match.split("-");
        if (players.length != 2) {
            return ResponseEntity.badRequest().build(); // Invalid format
        }

        String whitePlayer = players[0];
        String blackPlayer = players[1];

        // Find the match to delete
        DocumentMatch matchToRemove = null;
        for (DocumentMatch m : tournament.getRawMatches()) {
            if (m.getWhite().equals(whitePlayer) && m.getBlack().equals(blackPlayer)) {
                matchToRemove = m;
                break;
            }
        }

        // If the match is not found, return an error
        if (matchToRemove == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(tournament);
        }

        // Remove the found match
        tournament.getRawMatches().remove(matchToRemove);
        repository.save(tournament); // Save the updated tournament

        // Update statistics for the involved players
        userUpdateService.updateFields(whitePlayer);
        userUpdateService.updateFields(blackPlayer);

        return ResponseEntity.ok(tournament);
    }
}
