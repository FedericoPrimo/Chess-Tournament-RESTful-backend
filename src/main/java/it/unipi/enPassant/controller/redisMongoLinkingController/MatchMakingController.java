package it.unipi.enPassant.controller.redisMongoLinkingController;
import it.unipi.enPassant.service.redisService.LiveMatchService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.bson.Document;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/tournament/matchMaking")
public class MatchMakingController {

    private final MongoTemplate mongoTemplate;
    private final LiveMatchService tournamentService;

    public MatchMakingController(MongoTemplate mongoTemplate, LiveMatchService tournamentService) {
        this.mongoTemplate = mongoTemplate;
        this.tournamentService = tournamentService;
    }

    @PostMapping("/{year}/{category}/generateMatches/{numDays}")
    public ResponseEntity<String> generateRoundRobinMatches(
            @PathVariable int year,
            @PathVariable String category,
            @PathVariable int numDays) {

        Query query = new Query(Criteria.where("Edition").is(year).and("Category").is(category));
        query.fields().include("Participants");
        Document tournamentDoc = mongoTemplate.findOne(query, Document.class, "tournaments");

        if (tournamentDoc == null || !tournamentDoc.containsKey("Participants")) {
            return ResponseEntity.badRequest().body("Tournament not found or participants list not available");
        }

        List<String> participants = (List<String>) tournamentDoc.get("Participants");
        if (participants.size() < 2) {
            return ResponseEntity.badRequest().body("There must be at least 2 participants in order to generate a main draw");
        }

        List<String> matchIds = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            for (int j = i + 1; j < participants.size(); j++) {
                matchIds.add(participants.get(i) + "-" + participants.get(j)); // Andata
                matchIds.add(participants.get(j) + "-" + participants.get(i)); // Ritorno
            }
        }


        List<List<String>> matchDays = new ArrayList<>();
        for (int i = 0; i < numDays; i++) {
            matchDays.add(new ArrayList<>());
        }
        for (int i = 0; i < matchIds.size(); i++) {
            matchDays.get(i % numDays).add(matchIds.get(i));
        }

        Update update = new Update()
                .set("MatchDays", matchDays)
                .unset("Participants");

        mongoTemplate.updateFirst(query, update, "tournaments");

        return ResponseEntity.ok("Main draw created successfully");
    }


    @PostMapping("/{year}/{category}/scheduleNextDay")
    public ResponseEntity<String> scheduleNextMatchDay(@PathVariable int year, @PathVariable String category) {

        Query query = new Query(Criteria.where("Edition").is(year).and("Category").is(category));
        query.fields().include("MatchDays"); // Recupera solo il campo "MatchDays"
        Document tournamentDoc = mongoTemplate.findOne(query, Document.class, "tournaments");

        if (tournamentDoc == null || !tournamentDoc.containsKey("MatchDays")) {
            return ResponseEntity.badRequest().body("No matchdays available");
        }

        List<List<String>> matchDays = (List<List<String>>) tournamentDoc.get("MatchDays");
        if (matchDays.isEmpty()) {
            return ResponseEntity.badRequest().body("No matchdays left to be scheduled");
        }

        List<String> nextDayMatches = matchDays.get(0);
        if (nextDayMatches.isEmpty()) {
            return ResponseEntity.badRequest().body("The selected matchday is empty");
        }


        LocalTime startTime = LocalTime.of(8, 0);
        int intervalMinutes = (12 * 60) / nextDayMatches.size(); // Distribuzione equa nell'arco 08:00-20:00

        for (int i = 0; i < nextDayMatches.size(); i++) {
            String matchId = nextDayMatches.get(i);
            String formattedTime = startTime.plusMinutes(i * intervalMinutes).toString();

            tournamentService.addLiveMatch(matchId, category, formattedTime);
        }

        matchDays.remove(0);
        Update update = new Update().set("MatchDays", matchDays);
        mongoTemplate.updateFirst(query, update, "tournaments");

        return ResponseEntity.ok("Matchday sucessfully scheduled");
    }
}
