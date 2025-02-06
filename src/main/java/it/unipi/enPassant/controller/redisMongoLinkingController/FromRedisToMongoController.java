package it.unipi.enPassant.controller.redisMongoLinkingController;
import it.unipi.enPassant.model.requests.mongoModel.tournament.DocumentMatch;
import it.unipi.enPassant.model.requests.mongoModel.tournament.DocumentTournament;
import it.unipi.enPassant.repositories.TournamentRepository;
import it.unipi.enPassant.service.redisService.LiveMatchService;
import it.unipi.enPassant.service.redisService.ManagePlayerService;
import it.unipi.enPassant.service.redisService.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/appUpdate")
public class FromRedisToMongoController {

    // 1.initialize the class
    @Autowired
    private RequestService requestService;

    @Autowired
    private ManagePlayerService managePlayerService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private LiveMatchService liveMatchService;

    @Autowired
    private TournamentRepository repository;

    private List<String> blitzPlayers;
    private List<String> openPlayers;
    private List<String> rapidPlayers;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping
    public void appUpdate() {
        // 2. Get disqualified players and update MongoDB player status
        Set<Object> disqualified = managePlayerService.getAllDisqualifiedPlayers();
        for (Object user : disqualified) {
            if (user instanceof String userId) {
                System.out.println(userId);
                Query query = new Query(Criteria.where("_id").is(userId));
                Update update = new Update().set("state", 1);
                mongoTemplate.updateFirst(query, update, "user");
            }
        }

        // 3. Get enrolled players
        Map<Object, Object> enrolled = managePlayerService.getAllRegisteredPlayers();
        categorizePlayers(enrolled);
        System.out.println(blitzPlayers);
        if (!blitzPlayers.isEmpty()) {
            manageTournamentRegistration("Blitz", blitzPlayers);
        }
        if (!openPlayers.isEmpty()) {
            manageTournamentRegistration("Open", openPlayers);
        }
        if (!rapidPlayers.isEmpty()) {
            manageTournamentRegistration("Rapid", rapidPlayers);
        }

        // 4. Get live match
        updateTournamentsWithLiveMatches();

        // 5. Flush Key Value DB the information that contains now are  no more rilevant
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.flushDB(); // Cancella il database attuale di Redis
            System.out.println("Redis database flushed.");
        } catch (Exception e) {
            System.err.println("Error while flushing Redis: " + e.getMessage());
        }
    }

    // This function retrive from Key Value the list of the player that are enroll in some categories and divide them into three lists
    private void categorizePlayers(Map<Object, Object> enrolled) {
        blitzPlayers = new ArrayList<>();
        openPlayers = new ArrayList<>();
        rapidPlayers = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : enrolled.entrySet()) {
            String playerId = String.valueOf(entry.getKey());
            String category = String.valueOf(entry.getValue());

            switch (category.toLowerCase()) {
                case "blitz" -> blitzPlayers.add(playerId);
                case "open" -> openPlayers.add(playerId);
                case "rapid" -> rapidPlayers.add(playerId);
            }
        }
    }

    // Some getter
    public List<String> getBlitzPlayers() {
        return blitzPlayers;
    }

    public List<String> getOpenPlayers() {
        return openPlayers;
    }

    public List<String> getRapidPlayers() {
        return rapidPlayers;
    }

    // This is one of the main function called by the getMapping
    // the player that are enrolled are putted into the tournament document.
    // In this way when the submission to the tournament are ended the manager can create the maindraw.
    //thi function use the ausiliar function tournamentExists, isRegistrationOpen createTournament and
    // addPlayersToTournament in order to keep the code as much modular as possible
    private void manageTournamentRegistration(String category, List<String> players) {
        int currentYear = LocalDate.now().getYear();
        // check the existence of the tournaments
        if (!tournamentExists(currentYear, category)) {
            createTournament(currentYear, category);
            addPlayersToTournament(currentYear, category, players);
            return;
        }
        // check the entry closing date
        if (isRegistrationOpen(currentYear, category)) {
            addPlayersToTournament(currentYear, category, players);
            return;
        }

        // if submission were closed the submission is good for the next year
        int nextYear = currentYear + 1;
        if (!tournamentExists(nextYear, category)) {
            createTournament(nextYear, category);
        }
        if (isRegistrationOpen(nextYear, category)) {
            addPlayersToTournament(nextYear, category, players);
        }
    }

    private boolean tournamentExists(int year, String category) {
        Query query = new Query(Criteria.where("Edition").is(year).and("Category").is(category));
        return mongoTemplate.exists(query, "tournaments");
    }

    private boolean isRegistrationOpen(int year, String category) {
        Query query = new Query(Criteria.where("Edition").is(year).and("Category").is(category));
        DocumentTournament tournament = mongoTemplate.findOne(query, DocumentTournament.class);

        if (tournament == null || tournament.getEntry_Closing_Date() == null || tournament.getEntry_Closing_Date().isEmpty()) {
            return true;
        }

        return LocalDate.now().isBefore(LocalDate.parse(tournament.getEntry_Closing_Date().substring(0, 10), FORMATTER));
    }

    private void createTournament(int year, String category) {
        DocumentTournament tournament = new DocumentTournament();
        tournament.setEdition(year);
        tournament.setCategory(category);
        mongoTemplate.save(tournament);
    }

    private void addPlayersToTournament(int year, String category, List<String> players) {
        Query query = new Query(Criteria.where("Edition").is(year).and("Category").is(category));
        Update update = new Update().addToSet("Participants").each(players.toArray());
        mongoTemplate.updateFirst(query, update, "tournaments");
    }

    // This is the other core part of the getMapping. The aims is to insert the live match already ends in the DocumentDB
    // in this way they could be stored and we can flush the KeyValue DB without losing information.
    // Some System.out were addd in order to improve the readability of the code in the execution phase.
    public void updateTournamentsWithLiveMatches() {
        System.out.println("Retrieve Live Matches from Redis");
        List<String> liveMatches = liveMatchService.getLiveMatches();

        if (liveMatches.isEmpty()) {
            System.out.println("No Live Matches found!");
            return;
        }

        List<DocumentMatch> newRawMatches = new ArrayList<>();
        int currentYear = Year.now().getValue();

        for (String matchId : liveMatches) {
            // Here we manage every Live Match separetly
            Map<String, String> matchDetails = liveMatchService.getMatchDetails(matchId);
            if (matchDetails.isEmpty()) continue;
            System.out.println(matchId);
            String category = matchDetails.get("category");
            String startingTime = matchDetails.get("startingTime");


            List<String> movesList = liveMatchService.retrieveMovesList(matchId);


            DocumentMatch match = new DocumentMatch();

            // Retrieve White and Black by matchId that by convection is user1-user2
            String[] users = matchId.split("-");

            if (users.length == 2) {
                match.setWhite(users[0]); // White player
                match.setBlack(users[1]); // Black player
            } else {
                System.out.println("MatchId is a wrong format: " + matchId);
                continue;
            }
            // Some print in order to help in case of debug or something goes wrong (Hopefully, these prints are only aesthetics  )
            match.setCategory(category);
            System.out.println(category);
            match.setTimestamp(startingTime);
            System.out.println(startingTime);
            match.setMoves(movesList);
            System.out.println(movesList);

            newRawMatches.add(match);

            // Retrive the Tournament in which make the insert of the match
            Query query = new Query();
            query.addCriteria(Criteria.where("Edition").is(currentYear).and("Category").is(category));

            DocumentTournament tournament = mongoTemplate.findOne(query, DocumentTournament.class);

            if (tournament != null) {

                if (tournament.getRawMatches() == null) {
                    tournament.setRawMatches(new ArrayList<>());
                }

                tournament.getRawMatches().add(match);

                repository.save(tournament);
                System.out.println("Live match " + matchId + " added succesfully in  " + category);
            } else {
                System.out.println("There is no Tournament for this category: " + category);
            }
        }
    }
}