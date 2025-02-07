package it.unipi.enPassant.controller.redisController;
import it.unipi.enPassant.service.redisService.LiveMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/LiveMatch")
public class LiveMatchController {

    @Autowired
    private LiveMatchService liveMatchService;

    @PostMapping("/addLiveMatch/{matchId}/{category}/{startingTime}")
    public ResponseEntity<String> addLiveMatch(
            @PathVariable String matchId,
            @PathVariable String category,
            @PathVariable String startingTime) {
        liveMatchService.addLiveMatch(matchId, category, startingTime);
        return ResponseEntity.ok("Match submitted successfully.");
    }

    @GetMapping("/getLiveMatches")
    public ResponseEntity<List<String>> getLiveMatches() {
        List<String> matches = liveMatchService.getLiveMatches();
        return ResponseEntity.ok(matches);
    }

    @PostMapping("/insertMoves/{userId}/{matchId}")
    public ResponseEntity<String> addMove(
            @RequestBody String move,
            @PathVariable String userId,
            @PathVariable String matchId) {

        boolean success = liveMatchService.addMoves(move, userId, matchId);
        if (success) {
            return ResponseEntity.ok("Move " + move + " submitted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Move " + move + " not submitted successfully. " +
                    "Are you sure that the match and username are correct? Maybe it's not your turn.");
        }
    }

    @GetMapping("/retrieveMoveList/{matchId}")
    public ResponseEntity<List<String>> retrieveMoveList(@PathVariable String matchId) {
        List<String> moves = liveMatchService.retrieveMovesList(matchId);
        return ResponseEntity.ok(moves);
    }

    @GetMapping("/matchDetails/{matchId}")
    public ResponseEntity<Map<String, String>> getMatchDetails(@PathVariable String matchId) {
        Map<String, String> details = liveMatchService.getMatchDetails(matchId);
        return ResponseEntity.ok(details);
    }

    @DeleteMapping("/removeLiveMatch/{matchId}")
    public ResponseEntity<String> removeLiveMatch(@PathVariable String matchId) {
        liveMatchService.removeLiveMatch(matchId);
        return ResponseEntity.ok("Match " + matchId + " removed successfully.");
    }

    @PostMapping("/addLiveMatches")
    public ResponseEntity<String> addLiveMatches(@RequestBody List<Map<String, String>> matches) {
        for (Map<String, String> match : matches) {
            String matchId = match.get("matchId");
            String category = match.get("category");
            String startingTime = match.get("startingTime");

            if (matchId != null && category != null && startingTime != null) {
                liveMatchService.addLiveMatch(matchId, category, startingTime);
            } else {
                return ResponseEntity.badRequest().body("Invalid match data format.");
            }
        }
        return ResponseEntity.ok("Matches submitted successfully.");
    }

    @PostMapping("/insertMatchResult/{matchId}/{winner}/{ECO}")
    public ResponseEntity<String> insertMatchResult(@PathVariable String matchId, @PathVariable String winner, @PathVariable String ECO) {
        liveMatchService.insertMatchResult(matchId, winner,ECO);
        return ResponseEntity.ok("Match Result of  " + matchId + " inserted successfully.");
    }
}

