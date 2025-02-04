package it.unipi.enPassant.controller.mongoController.mongoAnalytics;

import it.unipi.enPassant.model.requests.mongoModel.tournament.TournamentsAnalyticsModel;
import it.unipi.enPassant.service.mongoService.TournamentsAnalyticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/tournaments-analytics")
public class TournamentsAnalyticsController {
    private final TournamentsAnalyticsService tournamentsAnalyticsService;
    List<String> validOutcomes = Arrays.asList("Blitz", "Rapid", "Open");

    public TournamentsAnalyticsController(TournamentsAnalyticsService tournamentsAnalyticsService) {
        this.tournamentsAnalyticsService = tournamentsAnalyticsService;
    }

    //1: Get the list of players who won matches in a particular category and the average number of moves they needed to win
    @GetMapping("/winner-avg-moves/{edition}/{category}")
    public ResponseEntity<?> winnerAvgMoves(@PathVariable int edition, @PathVariable String category) {
        if (!validOutcomes.contains(category)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Category must be one of: Blitz, Rapid, Open");
        }

        List<TournamentsAnalyticsModel> results = tournamentsAnalyticsService.AVGmovesPerWinner(edition, category);

        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //2: Get the list of players who participated in an edition and the number of matches they won in each category they enrolled in
    @GetMapping("/all-players-won-matches/{edition}/{category}")
    public ResponseEntity<?> allPlayersWonMatches(@PathVariable int edition, @PathVariable String category) {
        if (!validOutcomes.contains(category)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Category must be one of: Blitz, Rapid, Open");
        }

        List<TournamentsAnalyticsModel> results = tournamentsAnalyticsService.wonMatchesPerPlayer(edition, category);

        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //3 Gets the list of all openings and for each of them calculates the percentages of victories, losses and draws they led to
    @GetMapping("/opening-outcome-percentages")
    public ResponseEntity<List<TournamentsAnalyticsModel>> openingOutcomePercentages() {
        List<TournamentsAnalyticsModel> results = tournamentsAnalyticsService.allOpeningsPercentages();

        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //3 VAR: Given a certain opening calculates the percentages of victory, loss and draw it led to
    @GetMapping("/opening-outcome-percentages/{opening}")
    public ResponseEntity<TournamentsAnalyticsModel> openingOutcomePercentages(@PathVariable String opening) {
        TournamentsAnalyticsModel results = tournamentsAnalyticsService.openingsPercentages(opening);

        if (results == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //4: Given an edition, it returns for every tournament category the average number of moves matches lasted
    @GetMapping("/tournament-avg-moves/{edition}")
    public ResponseEntity<List<TournamentsAnalyticsModel>> openingOutcomePercentages(@PathVariable int edition) {
        List<TournamentsAnalyticsModel> results = tournamentsAnalyticsService.AVGmovesPerTournament(edition);

        if (results == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //5: Given a certain edition it returns, for each category, the most frequently used opening and how many times it was used
    @GetMapping("/tournament-most-frequent-opening/{edition}")
    public ResponseEntity<List<TournamentsAnalyticsModel>> TournamentMostFrequentOpening(@PathVariable int edition) {
        List<TournamentsAnalyticsModel> results = tournamentsAnalyticsService.tournamentMFO(edition);

        if (results == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //8: Given a certain edition, for each category it returns the average match duration
    @GetMapping("/tournament-avg-match-duration/{edition}")
    public ResponseEntity<List<TournamentsAnalyticsModel>> TournamentMatchAVGDuration(@PathVariable int edition) {
        List<TournamentsAnalyticsModel> results = tournamentsAnalyticsService.tournamentMatchDuration(edition);

        if (results == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }
}

