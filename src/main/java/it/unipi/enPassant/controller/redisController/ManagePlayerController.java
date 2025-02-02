package it.unipi.enPassant.controller;
import it.unipi.enPassant.service.ManagePlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/managePlayer")
public class ManagePlayerController {

    @Autowired
    private ManagePlayerService managePlayerService;

    /*DISQUALIFIED PLAYER SECTION*/
    @PostMapping("/disqualified/{playerId}")
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
           return "Player" + playerId + " is disqualified.";
       else
           return "Player" + playerId + " is not disqualified.";
    }

    @GetMapping("/disqualifiedList")
    public Set<Object> getAllDisqualifiedPlayers() {
        return managePlayerService.getAllDisqualifiedPlayers();
    }

    /*ENROLL CATEGORY SECTION*/
    @PostMapping("/register/{playerId}/{category}")
    public String registerPlayer(@PathVariable String playerId, @PathVariable String category) {
        managePlayerService.registerPlayer(playerId, category);
        return "Player " + playerId + " registered in category " + category + ".";
    }

    @DeleteMapping("/register/{playerId}")
    public String removeRegisteredPlayer(@PathVariable String playerId) {
        managePlayerService.removeRegisteredPlayer(playerId);
        return "Player " + playerId + " removed from registered list.";
    }

    @GetMapping("/register/{playerId}")
    public String isPlayerRegistered(@PathVariable String playerId) {
        if(managePlayerService.isPlayerRegistered(playerId))
            return "Player" + playerId + " is enrolled.";
        else
            return "Player" + playerId + " is not enrolled.";
    }

    @GetMapping("/registerList")
    public Map<Object, Object> getAllRegisteredPlayers() {
        return managePlayerService.getAllRegisteredPlayers();
    }
}

