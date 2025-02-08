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

    // This is one of the main function called by the getMapping
    // the player that are enrolled are putted into the tournament document.
    // In this way when the submission to the tournament are ended the manager can create the maindraw.
    //this function use the ausiliar function tournamentExists, isRegistrationOpen createTournament and
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

    // This is another core part of the getMapping. The aims are to insert the live match already ends in the DocumentDB
    // in this way they could be stored , we can flush the KeyValue DB without losing information.
    // Some System.out were add in order to improve the readability of the code in the execution phase.
    //this function use the ausiliar function calculateDuration(), retrieveUserInformation() and updateUserInformation()
    // in order to keep the code as much modular as possible
    private void updateTournamentsWithLiveMatches() {
        System.out.println("Retrieve Live Matches from Redis");
        List<String> liveMatches = liveMatchService.getLiveMatches();

        if (liveMatches.isEmpty()) {
            System.out.println("No Live Matches found!");
            return;
        }

        List<DocumentMatch> newRawMatches = new ArrayList<>();
        int currentYear = Year.now().getValue();
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        for (String matchId : liveMatches) {
            // Here we manage every Live Match separetly
            Map<String, String> matchDetails = liveMatchService.getMatchDetails(matchId);
            if (matchDetails.isEmpty()) continue;
            System.out.println(matchId);
            String category = matchDetails.get("category");
            String startingTime = matchDetails.get("startingTime");
            String endTime = matchDetails.get("endTime");
            String winner = matchDetails.get("winner");
            String ECO = matchDetails.get("ECO");

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
            match.setDate(currentYear);
            System.out.println(currentYear);
            match.setWinner(winner);
            System.out.println(winner);
            match.setEco(ECO);
            System.out.println(ECO);

            int whiteelo = retrieveELOUserInformation(users[0]);
            match.setWhiteElo(whiteelo);
            System.out.println(whiteelo);

            int blackelo = retrieveELOUserInformation(users[1]);
            match.setBlackElo(blackelo);
            System.out.println(blackelo);

            String result = computeResult(users[0],users[1],winner);
            match.setResult(result);
            System.out.println(result);

            if (startingTime != null && endTime != null) {
                match.setDuration(calculateDuration(startingTime, endTime));
            }

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
            updateUserInformation(match);
        }

    }

    private double calculateDuration(String start, String end) { //* modificato per restituire un double
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss"); //* aggiunto per formattare l'orario
        LocalTime startTime = LocalTime.parse(start, formatter);
        LocalTime endTime = LocalTime.parse(end, formatter);

        return java.time.Duration.between(startTime, endTime).toMinutes(); //* calcola la durata in minuti come double
    }

    private String computeResult(String white, String black, String winner) {
        if(Objects.equals(white, winner)) return "1-0";
        else if (Objects.equals(black, winner)) return "0-1";
        else return "0.5-0.5";
    }

    private int retrieveELOUserInformation(String user) {
        Query query = new Query(Criteria.where("_id").is(user));
        Document userDoc = mongoTemplate.findOne(query, Document.class, "user");

        if (userDoc != null && userDoc.containsKey("ELO")) {
            return userDoc.getInteger("ELO");
        }

        return 0;
    }

    // This is the last core function here we garantee data integrity updating the user document everytime we insert
    // a tournament match. This is made by two steps first we create the embedded document and in a second instance
    // we update all the statistical fields. This function use the ausiliar function updateUserMatchRecord() in order
    // to keep the code as much modular as possible.
    // (There are some print that are usefull to view the various execution status of the function)
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
            update.inc("ELO", 50);
        } else if (winner.equals("draw")) {
            update.inc("NumberOfDraw", 1);
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

}