package it.unipi.enPassant.controller.redisController;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.enPassant.config.JwtFilter;
import it.unipi.enPassant.controller.mongoController.mongoCRUD.CRUDcontrollerUser;
import it.unipi.enPassant.model.requests.mongoModel.user.DocumentUser;
import it.unipi.enPassant.service.JWTService;
import it.unipi.enPassant.service.redisService.LiveMatchService;
import it.unipi.enPassant.model.requests.redisModel.LiveMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/LiveMatch")
@Tag(name = "Live Match", description = "Live Match operations")
public class LiveMatchController {

    @Autowired
    private CRUDcontrollerUser crudControllerUser;

    @Autowired
    private LiveMatchService liveMatchService;

    @PostMapping("/addLiveMatch/{matchId}/{category}/{startingTime}")
    public ResponseEntity<String> addLiveMatch(
            @PathVariable String matchId,
            @PathVariable String category,
            @PathVariable String startingTime) {
        String[] users = matchId.split("-");
        if(crudControllerUser.getById(users[0]).getStatusCode().equals(HttpStatus.NOT_FOUND))
            return ResponseEntity.badRequest().body("User: " + users[0] + " does not exist.");
        if(crudControllerUser.getById(users[1]).getStatusCode().equals(HttpStatus.NOT_FOUND))
            return ResponseEntity.badRequest().body("User: " + users[1] + " does not exist.");

        boolean status = liveMatchService.addLiveMatch(matchId, category, startingTime);
        if(!status)
            return ResponseEntity.badRequest().body("Match " + matchId + " already exists.");
        else
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
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedUser = ((UserDetails) obj).getUsername();
        if(!loggedUser.equals(userId)) {
            return ResponseEntity.badRequest().body("You can only insert your own moves");
        }

        boolean success = liveMatchService.addMoves(move, userId, matchId);
        if (success) {
            return ResponseEntity.ok("Move " + move + " submitted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Move " + move + " not submitted successfully. " +
                    "Are you sure that the match and username are correct? Maybe it's not your turn.");
        }
    }

    @GetMapping("/retrieveMoveList/{matchId}")
    public ResponseEntity<?> retrieveMoveList(@PathVariable String matchId) {
        List<String> moves = liveMatchService.retrieveMovesList(matchId);
        if(moves.isEmpty())
            return ResponseEntity.badRequest().body("No moves found for match " + matchId + ".\n" +
                    "Check if the match id is correct. Or maybe the match has not started yet.");
        else{
            return ResponseEntity.ok(moves);
        }
    }

    @GetMapping("/matchDetails/{matchId}")
    public ResponseEntity<?> getMatchDetails(@PathVariable String matchId) {
        LiveMatch details = liveMatchService.getMatchDetails(matchId);
        if(details == null)
            return ResponseEntity.badRequest().body("Match details " + matchId + " not found.");
        else{
            return ResponseEntity.ok(details);
        }
    }

    @DeleteMapping("/removeLiveMatch/{matchId}")
    public ResponseEntity<String> removeLiveMatch(@PathVariable String matchId) {
        boolean status = liveMatchService.removeLiveMatch(matchId);
        if(status)
            return ResponseEntity.ok("Match " + matchId + " removed successfully.");
        else
            return ResponseEntity.badRequest().body("Match " + matchId + " not found.");
    }

    @PostMapping("/addLiveMatches")
    public ResponseEntity<String> addLiveMatches(@RequestBody List<Map<String, String>> matches) {
        for (Map<String, String> match : matches) {
            String matchId = match.get("matchId");
            String category = match.get("category");
            String startingTime = match.get("startingTime");

            String[] users = matchId.split("-");
            if(crudControllerUser.getById(users[0]).getStatusCode().equals(HttpStatus.NOT_FOUND))
                return ResponseEntity.badRequest().body("User: " + users[0] + " does not exist.");
            if(crudControllerUser.getById(users[1]).getStatusCode().equals(HttpStatus.NOT_FOUND))
                return ResponseEntity.badRequest().body("User: " + users[1] + " does not exist.");

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
        boolean status = liveMatchService.insertMatchResult(matchId, winner, ECO);
        if(!status)
            return ResponseEntity.badRequest().body("Match Result of " + matchId + " not inserted successfully. Check the parameters.");
        else
            return ResponseEntity.ok("Match Result of " + matchId + " inserted successfully.");
    }
}
