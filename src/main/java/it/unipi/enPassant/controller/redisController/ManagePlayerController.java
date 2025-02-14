package it.unipi.enPassant.controller.redisController;
import it.unipi.enPassant.service.redisService.ManagePlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Response;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/managePlayer")
public class ManagePlayerController {

    @Autowired
    private ManagePlayerService managePlayerService;

    /*DISQUALIFIED PLAYER SECTION*/
    @PostMapping("/disqualify/{playerId}")
    public String addDisqualifiedPlayer(@PathVariable String playerId) {
        managePlayerService.addDisqualifiedPlayer(playerId);
        return "Player " + playerId + " added to disqualified list.";
    }

    @DeleteMapping("/disqualified/{playerId}")
    public String removeDisqualifiedPlayer(@PathVariable String playerId) {
        managePlayerService.removeDisqualifiedPlayer(playerId);
        return "Player " + playerId + " removed from disqualified list.";
    }

    @GetMapping("/disqualified/{playerId}")
    public String isPlayerDisqualified(@PathVariable String playerId) {
       if(managePlayerService.isPlayerDisqualified(playerId))
           return "Player " + playerId + " is disqualified.";
       else
           return "Player " + playerId + " is not disqualified.";
    }

    @GetMapping("/disqualifiedList")
    public Set<String> getAllDisqualifiedPlayers() {
        return managePlayerService.getAllDisqualifiedPlayers();
    }

    /*ENROLL CATEGORY SECTION*/
    @PostMapping("/register/{playerId}/{category}")
    public String registerPlayer(@PathVariable String playerId, @PathVariable String category) {
        managePlayerService.registerPlayer(playerId, category);
        return "Player " + playerId + " registered in category " + category + ".";
    }

    @DeleteMapping("/register/{playerId}")
    public ResponseEntity<String> removeRegisteredPlayer(@PathVariable String playerId) {
        boolean status = managePlayerService.removeRegisteredPlayer(playerId);
        if(status)
            return ResponseEntity.ok("Player " + playerId + " removed from registered list.");
        else
            return ResponseEntity.badRequest().body("Player " + playerId + " is not registered.");
    }

    @GetMapping("/register/{playerId}")
    public String isPlayerRegistered(@PathVariable String playerId) {
        if(managePlayerService.isPlayerRegistered(playerId))
            return "Player " + playerId + " is enrolled.";
        else
            return "Player " + playerId + " is not enrolled.";
    }

    @GetMapping("/registerList")
    public Map<String, String> getAllRegisteredPlayers() {
        return managePlayerService.getAllRegisteredPlayers();
    }
}

