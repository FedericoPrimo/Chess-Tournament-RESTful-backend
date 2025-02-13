package it.unipi.enPassant.controller.redisMongoLinkingController;
import it.unipi.enPassant.model.requests.mongoModel.tournament.DocumentMatch;
import it.unipi.enPassant.model.requests.mongoModel.tournament.DocumentTournament;
import it.unipi.enPassant.model.requests.redisModel.LiveMatch;
import it.unipi.enPassant.model.requests.redisModel.Request;
import it.unipi.enPassant.repositories.TournamentRepository;
import it.unipi.enPassant.service.redisService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.bson.Document;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @Autowired
    private RedisReplicationChecker redisReplicationChecker;

    @Autowired
    private ClusterFlush clusterFlush;

    private List<String> blitzPlayers;
    private List<String> openPlayers;
    private List<String> rapidPlayers;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping
    public ResponseEntity<List<Request>> appUpdate() {
        // 0. waiting for the synchronization of redis cluster
        if (!redisReplicationChecker.waitForReplicationSync()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Collections.emptyList());
        }
        // 1. Get disqualified players and update MongoDB player status
        Set<String> disqualified = managePlayerService.getAllDisqualifiedPlayers();
        for (Object user : disqualified) {
            if (user instanceof String userId) {
                System.out.println(userId);
                Query query = new Query(Criteria.where("_id").is(userId));
                Update update = new Update().set("status", 1);
                mongoTemplate.updateFirst(query, update, "user");
            }
        }

        // 2. Get enrolled players
        Map<String, String> enrolled = managePlayerService.getAllRegisteredPlayers();
        categorizePlayers(enrolled);
        System.out.println("Enrolled players:");
        for (Map.Entry<String, String> entry : enrolled.entrySet()) {
            System.out.println("Player ID: " + entry.getKey() + ", Category: " + entry.getValue());
        }System.out.println("Enrolled players:");
        for (Map.Entry<String, String> entry : enrolled.entrySet()) {
            System.out.println("Player ID: " + entry.getKey() + ", Category: " + entry.getValue());
        }
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

        // 3. Get live match and update
        updateTournamentsWithLiveMatches();

        //4. Retrieve pending request
        List<Request> pendingRequests = getAllPendingRequests();

        // 5. Flush Key Value DB the information that contains now are  no more relevant
        clusterFlush.flushClusterDB();

        return ResponseEntity.ok(pendingRequests);
    }

    // This function retrieve from Key Value the list of the player that are enroll in some categories and divide them into three lists
    private void categorizePlayers(Map<String, String> enrolled) {
        blitzPlayers = new ArrayList<>();
        openPlayers = new ArrayList<>();
        rapidPlayers = new ArrayList<>();

        for (Map.Entry<String, String> entry : enrolled.entrySet()) {
            String playerId = entry.getKey();
            String category = entry.getValue().toLowerCase();

            // Query per verificare se il giocatore esiste ed è di tipo "player"
            Query query = new Query(Criteria.where("_id").is(playerId).and("Type").is("1"));

            if (!mongoTemplate.exists(query, "user")) {
                System.out.println("Player ID " + playerId + " does not exist or is not of type 'player'. Skipping...");
                continue; // Salta il giocatore se non esiste o non è un "player"
            }

            // Inserisci il giocatore nella categoria corretta
            switch (category) {
                case "blitz" -> blitzPlayers.add(playerId);
                case "open" -> openPlayers.add(playerId);
                case "rapid" -> rapidPlayers.add(playerId);
                default -> System.out.println("Unknown category for player ID " + playerId + ": " + category);
            }
        }
    }


    // This is one of the main function called by the getMapping
    // the player that are enrolled are putted into the tournament document.
    // In this way when the submission to the tournament are ended the manager can create the main draw.
    //this function use the auxiliary function tournamentExists, isRegistrationOpen createTournament and
    // addPlayersToTournament in order to keep the code as much modular as possible
    private void manageTournamentRegistration(String category, List<String> players) {
        int currentYear = LocalDate.now().getYear();
        System.out.println("Managing tournament registration for year: " + currentYear + ", category: " + category);
        System.out.println("Players to register: " + players);

        // Check the existence of the tournament for the current year
        if (!tournamentExists(currentYear, category)) {
            System.out.println("Tournament for " + currentYear + " in category " + category + " does not exist. Creating...");
            createTournament(currentYear, category);
            addPlayersToTournament(currentYear, category, players);
            System.out.println("Players added to new tournament.");
            return;
        } else {
            System.out.println("Tournament for " + currentYear + " in category " + category + " already exists.");
        }

        // Check if registration is still open
        if (isRegistrationOpen(currentYear, category)) {
            System.out.println("Registration is open for " + currentYear + ". Adding players...");
            addPlayersToTournament(currentYear, category, players);
            return;
        } else {
            System.out.println("Registration is closed for " + currentYear + ".");
        }

        // If submission is closed, try for next year
        int nextYear = currentYear + 1;
        System.out.println("Checking for next year: " + nextYear);

        if (!tournamentExists(nextYear, category)) {
            System.out.println("Tournament for " + nextYear + " in category " + category + " does not exist. Creating...");
            createTournament(nextYear, category);
        } else {
            System.out.println("Tournament for " + nextYear + " in category " + category + " already exists.");
        }

        if (isRegistrationOpen(nextYear, category)) {
            System.out.println("Registration is open for " + nextYear + ". Adding players...");
            addPlayersToTournament(nextYear, category, players);
        } else {
            System.out.println("Registration is closed for " + nextYear + ".");
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

    // This is another core part of the getMapping. The aims are to insert the live match already ends in the DocumentDB
    // in this way they could be stored , we can flush the KeyValue DB without losing information.
    // Some System.out were add in order to improve the readability of the code in the execution phase.
    //this function use the auxiliary function calculateDuration(), retrieveUserInformation() and updateUserInformation()
    // in order to keep the code as much modular as possible
    private void updateTournamentsWithLiveMatches() {
        System.out.println("Retrieving Live Matches from Redis");
        List<String> liveMatchIds = liveMatchService.getLiveMatches();

        if (liveMatchIds.isEmpty()) {
            System.out.println("No Live Matches found!");
            return;
        }

        List<DocumentMatch> newRawMatches = new ArrayList<>();
        int currentYear = Year.now().getValue();

        for (String matchId : liveMatchIds) {
            LiveMatch liveMatch = liveMatchService.getMatchDetails(matchId);
            if (liveMatch == null) continue;

            System.out.println("Processing match: " + matchId);

            String category = liveMatch.getCategory();
            String startingTime = liveMatch.getStartingTime();
            String endTime = liveMatch.getEndTime();
            String winner = liveMatch.getWinner();
            String ECO = liveMatch.getECO();

            if (winner == null || winner.trim().isEmpty() ||
                    ECO == null || ECO.trim().isEmpty() ||
                    endTime == null || endTime.trim().isEmpty()) {
                continue;
            }

            List<String> movesList = liveMatchService.retrieveMovesList(matchId);
            DocumentMatch match = new DocumentMatch();

            String[] users = matchId.split("-");
            if (users.length == 2) {
                match.setWhite(users[0]);
                match.setBlack(users[1]);
            } else {
                System.out.println("Invalid matchId format: " + matchId);
                continue;
            }

            match.setCategory(category);
            match.setTimestamp(startingTime);
            match.setMoves(movesList);
            match.setDate(currentYear);
            match.setWinner(winner);
            match.setEco(ECO);

            String result = computeResult(users[0], users[1], winner);
            match.setResult(result);

            if (startingTime != null && endTime != null) {
                match.setDuration(calculateDuration(startingTime, endTime));
            }

            newRawMatches.add(match);

            Query query = new Query(Criteria.where("Edition").is(currentYear).and("Category").is(category));
            DocumentTournament tournament = mongoTemplate.findOne(query, DocumentTournament.class);

            if (tournament != null) {
                if (tournament.getRawMatches() == null) {
                    tournament.setRawMatches(new ArrayList<>());
                }
                tournament.getRawMatches().add(match);
                repository.save(tournament);
                System.out.println("Live match " + matchId + " successfully added to " + category);
            } else {
                System.out.println("No tournament found for category: " + category);
            }

            updateUserInformation(match);
        }
    }

    private double calculateDuration(String start, String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime startTime = LocalTime.parse(start, formatter);
        LocalTime endTime = LocalTime.parse(end, formatter);

        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    private String computeResult(String white, String black, String winner) {
        if(Objects.equals(white, winner)) return "1-0";
        else if (Objects.equals(black, winner)) return "0-1";
        else return "0.5-0.5";
    }

    // This is the last core function here we guarantee data integrity updating the user document everytime we insert
    // a tournament match. This is made by two steps first we create the embedded document and in a second instance
    // we update all the statistical fields. This function use the auxiliary function updateUserMatchRecord() in order
    // to keep the code as much modular as possible.
    // (There are some print that are usefully to view the various execution status of the function)
    private void updateUserInformation(DocumentMatch match) {
        String whitePlayerId = match.getWhite();
        String blackPlayerId = match.getBlack();
        String winner = match.getWinner();
        String opening = match.getEco();
        int numberOfMoves = match.getMoves().size();
        int tournamentEdition = match.getDate();
        String tournamentCategory = match.getCategory();

        System.out.println("updateUserInformation called with:");
        System.out.println("White Player: " + whitePlayerId);
        System.out.println("Black Player: " + blackPlayerId);
        System.out.println("Winner: " + winner);
        System.out.println("Opening: " + opening);
        System.out.println("Number of Moves: " + numberOfMoves);
        System.out.println("Tournament Edition: " + tournamentEdition);
        System.out.println("Tournament Category: " + tournamentCategory);

        updateUserMatchRecord(whitePlayerId, "White", numberOfMoves, winner, opening, blackPlayerId, tournamentEdition, tournamentCategory);
        updateUserMatchRecord(blackPlayerId, "Black", numberOfMoves, winner, opening, whitePlayerId, tournamentEdition, tournamentCategory);
    }

    private void updateUserMatchRecord(String userId, String color, int numberOfMoves, String winner, String opening, String opponentId, int tournamentEdition, String tournamentCategory) {
        System.out.println("updateUserMatchRecord called with:");
        System.out.println("User ID: " + userId);
        System.out.println("Color: " + color);
        System.out.println("Number of Moves: " + numberOfMoves);
        System.out.println("Winner: " + winner);
        System.out.println("Opening: " + opening);
        System.out.println("Opponent ID: " + opponentId);
        System.out.println("Tournament Edition: " + tournamentEdition);
        System.out.println("Tournament Category: " + tournamentCategory);

        Query query = new Query(Criteria.where("_id").is(userId));

        Document matchRecord = new Document();
        matchRecord.put("Color", color);
        matchRecord.put("NumberOfMoves", numberOfMoves);
        matchRecord.put("Winner", winner);
        matchRecord.put("Opening", opening);
        matchRecord.put("OpponentId", opponentId);
        matchRecord.put("TournamentEdition", tournamentEdition);
        matchRecord.put("TournamentCategory", tournamentCategory);

        Update update = new Update().push("Matches", matchRecord);

        // Update user statistics
        update.inc("NumberOfPlayedMatches", 1);
        if (winner.equals(userId)) {
            update.inc("NumberOfVictories", 1);
            update.inc("ELO", 80);
        } else if (winner.equals("draw")) {
            update.inc("NumberOfDraw", 1);
            update.inc("ELO", 10);
        } else {
            update.inc("NumberOfDefeats", 1);
            update.inc("ELO", -50);
        }


        Query queryavg = new Query(Criteria.where("_id").is(userId));
        Document userDoc = mongoTemplate.findOne(queryavg, Document.class, "user");

        int totalMoves = userDoc != null && userDoc.containsKey("Matches")
                ? ((List<Document>) userDoc.get("Matches")).stream()
                .mapToInt(m -> m.getInteger("NumberOfMoves", 0))
                .sum()
                : 0;

        int totalMatches = userDoc != null && userDoc.containsKey("NumberOfPlayedMatches")
                ? userDoc.getInteger("NumberOfPlayedMatches", 0)
                : 0;

        double avgMoves = (totalMatches > 0) ? (double) totalMoves / totalMatches : 0;
        Update updateavg = new Update().set("avgMovesNumber", avgMoves);
        mongoTemplate.updateFirst(queryavg, updateavg, "user");


        mongoTemplate.updateFirst(query, update, "user");
    }

    private List<Request> getAllPendingRequests() {
        List<Request> requests = new ArrayList<>();

        while (requestService.getQueueSize() > 0) {
            Request request = requestService.consumeNextRequest();
            if (request.getNomeUtente() != null && !request.getNomeUtente().isEmpty()) {
                requests.add(request);
            }
        }
        return requests;
    }
}