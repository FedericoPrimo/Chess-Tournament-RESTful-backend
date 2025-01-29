package it.unipi.enPassant.controller;

import it.unipi.enPassant.model.requests.MatchListModel;
import it.unipi.enPassant.model.requests.MatchModel;
import it.unipi.enPassant.model.requests.TournamentModel;
import it.unipi.enPassant.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tournament")
public class TournamentController{
    // Services used for interacting with the databases
    private final DataService dataService;

    // Constructor for the TournamentController
    @Autowired
    public TournamentController(DataService dataService) {
        this.dataService = dataService;
    }

    /* this GET allows us to retrive the list of all tournaments.
    It returns a list of jason with edition,category and location*/
    @GetMapping("/tournamentList")
    public ResponseEntity<List<TournamentModel>> tournamentList() {
        List<TournamentModel> tournament = dataService.tournamentGetList();

        if (tournament.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(tournament);
    }

    /* this GET allows us to retrive the list of all live matches.
    It returns a list of jason with white player and black player*/
    @GetMapping("/liveMatchList")
    public ResponseEntity<List<MatchListModel>> getliveMatchList() {
        List<MatchListModel> liveMatch = dataService.liveMatchGetList();

        if (liveMatch.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(liveMatch);
    }

    /* this GET allows us to retrive the list of all matches of a specific tournament.
    It returns a list of jason with white player and black player*/
    @GetMapping("/tournamentMatchList/{category}/{edition}/{location}")
    public ResponseEntity<List<MatchListModel>> getTournamentMatchList(
            @PathVariable String category, @PathVariable int edition, @PathVariable String location){
        List<MatchListModel> tournamentMatch = dataService.tournamentMatchGetList(category,edition,location);

        if (tournamentMatch.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(tournamentMatch);
    }

    /*this GET allows us to retrive the list of all information of a finished match.*/
    @GetMapping("/tournamentMatchList/match/{black}/{white}")
    public ResponseEntity<MatchModel> getTournamentMatch(
            @PathVariable String black, @PathVariable String white){
        MatchModel tournamentMatch = dataService.tournamentMatchGet(black, white);

        if (tournamentMatch==null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(tournamentMatch);
    }

    /*this GET allows us to retrive the list of all information of live match.*/
    @GetMapping("/liveMatchList/match/{black}/{white}")
    public ResponseEntity<MatchModel> getLiveMatch(
            @PathVariable String black, @PathVariable String white){
        MatchModel tournamentMatch = dataService.liveMatchGet(black, white);

        if (tournamentMatch==null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(tournamentMatch);
    }

    /*Here we have to put all mongoDB analytics queries endpoint*/
}



