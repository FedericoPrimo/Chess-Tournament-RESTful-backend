package it.unipi.enPassant.controller;

import it.unipi.enPassant.model.requests.TournamentsAnalytic3Model;
import it.unipi.enPassant.model.requests.TournamentsAnalyticAVGModel;
import it.unipi.enPassant.service.TournamentsAnalyticsService;
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

        List<TournamentsAnalyticAVGModel> results = tournamentsAnalyticsService.AVGmovesPerWinner(edition, category);

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

        List<TournamentsAnalyticAVGModel> results = tournamentsAnalyticsService.wonMatchesPerPlayer(edition, category);

        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //3 Gets the list of all openings and for each of them calculates the percentages of victories, losses and draws they led to
    @GetMapping("/opening-outcome-percentages")
    public ResponseEntity<List<TournamentsAnalytic3Model>> openingOutcomePercentages() {
        List<TournamentsAnalytic3Model> results = tournamentsAnalyticsService.allOpeningsPercentages();

        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //3 VAR: Given a certain opening calculates the percentages of victory, loss and draw it led to
    @GetMapping("/opening-outcome-percentages/{opening}")
    public ResponseEntity<TournamentsAnalytic3Model> openingOutcomePercentages(@PathVariable String opening) {
        TournamentsAnalytic3Model results = tournamentsAnalyticsService.openingsPercentages(opening);

        if (results == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //4: Given an edition, it returns for every tournament category the average number of moves matches lasted
    @GetMapping("/tournament-avg-moves/{edition}")
    public ResponseEntity<List<TournamentsAnalyticAVGModel>> openingOutcomePercentages(@PathVariable int edition) {
        List<TournamentsAnalyticAVGModel> results = tournamentsAnalyticsService.AVGmovesPerTournament(edition);

        if (results == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    //5
    @GetMapping("/tournament-most-frequent-opening/{edition}")
    public ResponseEntity<List<TournamentsAnalyticAVGModel>> TournamentMostFrequentOpening(@PathVariable int edition) {
        List<TournamentsAnalyticAVGModel> results = tournamentsAnalyticsService.tournamentMFO(edition);

        if (results == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/tournament-avg-match-duration/{edition}")
    public ResponseEntity<List<TournamentsAnalyticAVGModel>> TournamentMatchAVGDuration(@PathVariable int edition) {
        List<TournamentsAnalyticAVGModel> results = tournamentsAnalyticsService.tournamentMatchDuration(edition);

        if (results == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(results);
    }
}

