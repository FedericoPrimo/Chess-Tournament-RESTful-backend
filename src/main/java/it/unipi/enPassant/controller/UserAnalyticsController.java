package it.unipi.enPassant.controller;

import it.unipi.enPassant.model.requests.UserAnalyticsModel;
import it.unipi.enPassant.service.UserAnalyticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/user-analytics")
public class UserAnalyticsController {
    private final UserAnalyticsService userAnalyticsService;
    List<String> validOutcomes = Arrays.asList("win", "draw", "loss");

    public UserAnalyticsController(UserAnalyticsService userAnalyticsService) {
        this.userAnalyticsService = userAnalyticsService;
    }

    //API endpoints//

    //1: Get the list of all the players and their respective most frequently used opening
    @GetMapping("/all-most-frequent-openings")
    public ResponseEntity<List<UserAnalyticsModel>> allMostFrequentOpening() {
        List<UserAnalyticsModel> results = userAnalyticsService.getAllPlayersMFO();

        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //1VAR: Get a specific player's most frequently used opening
    @GetMapping("/player-most-frequent-openings/{username}")
    public ResponseEntity<UserAnalyticsModel> playerMostFrequentOpening(@PathVariable String username) {
        UserAnalyticsModel results = userAnalyticsService.getPlayersMFO(username);
        if (results==null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }


    //2: Get the list of all the players and their respective most frequently used opening in matches they won/loss/draw
    @GetMapping("/all-most-frequent-openings/{outcome}")
    public ResponseEntity<?> allMostFrequentOpening_Winning(@PathVariable String outcome) {
        if (!validOutcomes.contains(outcome.toLowerCase())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Outcome must be one of: win, draw, loss");
        }

        List<UserAnalyticsModel> results = userAnalyticsService.getAllPlayersWinningMFO(outcome);

        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //2VAR: Get a specific player's most frequently used opening in matches they won
    @GetMapping("/player-most-frequent-openings/{outcome}/{username}")
    public ResponseEntity<?> playerMostFrequentOpening_Winning(@PathVariable String username, @PathVariable String outcome) {

        if (!validOutcomes.contains(outcome.toLowerCase())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Outcome must be one of: win, draw, loss");
        }

        UserAnalyticsModel results = userAnalyticsService.getPlayersWinningMFO(username, outcome);

        if (results == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(results);
    }


    //3: Get the list of all the players and their respective number of defeated opponents
    @GetMapping("/number-of-defeated-opponents")
    public ResponseEntity<List<UserAnalyticsModel>> allDefeatedOpponents() {
        List<UserAnalyticsModel> results = userAnalyticsService.getNumberOfDefeatedOpponents();
        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //3 VAR: Get a specific player's number of defeated opponents
    @GetMapping("/player-number-of-defeated-opponents/{username}")
    public ResponseEntity<UserAnalyticsModel> playerDefeatedOpponents(@PathVariable String username) {
        UserAnalyticsModel results = userAnalyticsService.getPlayerNumberOfDefeatedOpponents(username);

        if (results == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }


    //4: Get the list of all the players and their respective number of won matches
    @GetMapping("/number-of-matches/{outcome}")
    public ResponseEntity<?> allWonMatches(@PathVariable String outcome) {

        if (!validOutcomes.contains(outcome.toLowerCase())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Outcome must be one of: win, draw, loss");
        }

        List<UserAnalyticsModel> results = userAnalyticsService.getNumberOfWonMatches(outcome);
        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //4 VAR: Get the list of all the players and their respective number of won matches
    @GetMapping("/player-number-of-matches/{outcome}/{username}")
    public ResponseEntity<?> playerWonMatches(@PathVariable String outcome, @PathVariable String username) {

        if (!validOutcomes.contains(outcome.toLowerCase())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Outcome must be one of: win, draw, loss");
        }

        UserAnalyticsModel results = userAnalyticsService.getPlayersNumberOfWonMatches(outcome, username);
        if (results == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }


    //5: Get the list of disqualified players
    @GetMapping("/disqualified-players")
    public ResponseEntity<List<String>> disqualifiedPlayers() {
        List<String> results = userAnalyticsService.getDisqualifiedPlayers();
        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }
}
