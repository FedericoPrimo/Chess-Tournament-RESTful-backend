package it.unipi.enPassant.controller.redisController;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.enPassant.controller.mongoController.mongoCRUD.CRUDcontrollerUser;
import it.unipi.enPassant.service.redisService.ManagePlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Response;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/managePlayer")
@Tag(name = "Manage Player", description = "Manage Player operations")
public class ManagePlayerController {
    List<String> validOutcomes = Arrays.asList("Blitz", "Rapid", "Open");

    @Autowired
    private CRUDcontrollerUser crudControllerUser;

    @Autowired
    private ManagePlayerService managePlayerService;

    /*DISQUALIFIED PLAYER SECTION*/
    @PostMapping("/disqualify/{playerId}")
    public ResponseEntity<String> addDisqualifiedPlayer(@PathVariable String playerId) {
        if(crudControllerUser.getById(playerId).getStatusCode().equals(HttpStatus.NOT_FOUND))
            return ResponseEntity.badRequest().body("User: " + playerId + " does not exist.");

        boolean check = managePlayerService.addDisqualifiedPlayer(playerId);
        if(!check)
            return ResponseEntity.badRequest().body("Player " + playerId + " already disqualified.");
        else
            return ResponseEntity.ok("Player " + playerId + " added to disqualified list.");
    }

    @DeleteMapping("/disqualify/{playerId}")
    public ResponseEntity<String> removeDisqualifiedPlayer(@PathVariable String playerId) {
        boolean check = managePlayerService.removeDisqualifiedPlayer(playerId);
        if(!check)
            return ResponseEntity.badRequest().body("Player " + playerId + " is not disqualified.");
        else
            return ResponseEntity.ok("Player " + playerId + " removed from disqualified list.");
    }

    @GetMapping("/disqualified/{playerId}")
    public ResponseEntity<String> isPlayerDisqualified(@PathVariable String playerId) {
       if(managePlayerService.isPlayerDisqualified(playerId))
           return ResponseEntity.ok("Player " + playerId + " is disqualified.");
       else
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player " + playerId + " is not disqualified.");
    }

    @GetMapping("/disqualifiedList")
    public Set<String> getAllDisqualifiedPlayers() {
        return managePlayerService.getAllDisqualifiedPlayers();
    }

    /*ENROLL CATEGORY SECTION*/
    @PostMapping("/register/{playerId}/{category}")
    public ResponseEntity<String> registerPlayer(@PathVariable String playerId, @PathVariable String category) {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedUser = ((UserDetails) obj).getUsername();
        if(!loggedUser.equals(playerId)){
            return ResponseEntity.badRequest().body("You can only enroll yourself!");
        }
        if(!validOutcomes.contains(category))
            return ResponseEntity.badRequest().body("Category must be one of: Blitz, Rapid, Open");

        boolean status = managePlayerService.registerPlayer(playerId, category);
        if(!status)
            return ResponseEntity.badRequest().body("Player " + playerId + " disqualified or already registered in category " + category + ".");
        else
            return ResponseEntity.ok("Player " + playerId + " registered in category " + category + ".");
    }

    @DeleteMapping("/register/{playerId}")
    public ResponseEntity<String> removeRegisteredPlayer(@PathVariable String playerId) {
        boolean status = managePlayerService.removeRegisteredPlayer(playerId);
        if(status)
            return ResponseEntity.ok("Player " + playerId + " removed from registered list.");
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player " + playerId + " is not registered.");
    }

    @GetMapping("/register/{playerId}")
    public ResponseEntity<String> isPlayerRegistered(@PathVariable String playerId) {
        if(managePlayerService.isPlayerRegistered(playerId))
            return ResponseEntity.ok("Player " + playerId + " is enrolled");
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player " + playerId + " is not enrolled.");
    }

    @GetMapping("/registerList")
    public Map<String, String> getAllRegisteredPlayers() {
        return managePlayerService.getAllRegisteredPlayers();
    }
}

