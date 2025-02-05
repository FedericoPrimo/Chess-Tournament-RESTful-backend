package it.unipi.enPassant.controller;
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
        // 1. Reset request Queue
        // -> gestito nel punto 5:)
        // 2. Get disqualified players and update MongoDB
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

        // 5. Flush DB
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.flushDB(); // Cancella il database attuale di Redis
            System.out.println("Redis database flushed.");
        } catch (Exception e) {
            System.err.println("Error while flushing Redis: " + e.getMessage());
        }
    }

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

    public List<String> getBlitzPlayers() {
        return blitzPlayers;
    }

    public List<String> getOpenPlayers() {
        return openPlayers;
    }

    public List<String> getRapidPlayers() {
        return rapidPlayers;
    }

    private void manageTournamentRegistration(String category, List<String> players) {
        int currentYear = LocalDate.now().getYear();

        // 1. Controlla se il torneo dell'anno corrente esiste
        if (!tournamentExists(currentYear, category)) {
            createTournament(currentYear, category);
            addPlayersToTournament(currentYear, category, players);
            return;
        }

        // 2. Controlla se le iscrizioni sono ancora aperte
        if (isRegistrationOpen(currentYear, category)) {
            addPlayersToTournament(currentYear, category, players);
            return;
        }

        // 3. Se le iscrizioni sono chiuse, gestisci il torneo per l'anno successivo
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
            return true; // Se il torneo non esiste o la data è vuota, consideriamo le iscrizioni aperte
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

    public void updateTournamentsWithLiveMatches() {
        System.out.println("🔎 Recupero dei match live da Redis...");
        List<String> liveMatches = liveMatchService.getLiveMatches();

        if (liveMatches.isEmpty()) {
            System.out.println("⚠ Nessun match live trovato.");
            return;
        }

        List<DocumentMatch> newRawMatches = new ArrayList<>();
        int currentYear = Year.now().getValue();

        for (String matchId : liveMatches) {
            // Recupero i dettagli del match
            Map<String, String> matchDetails = liveMatchService.getMatchDetails(matchId);
            if (matchDetails.isEmpty()) continue;
            System.out.println(matchId);
            String category = matchDetails.get("category");
            String startingTime = matchDetails.get("startingTime");

            // Recupero la lista delle mosse
            List<String> movesList = liveMatchService.retrieveMovesList(matchId);

            // Creazione dell'oggetto DocumentMatch
            DocumentMatch match = new DocumentMatch();

            // Parsing del matchId per ottenere user1 (bianco) e user2 (nero)
            String[] users = matchId.split("-");

            if (users.length == 2) {
                match.setWhite(users[0]); // White player
                match.setBlack(users[1]); // Black player
            } else {
                System.out.println("⚠ Errore nel parsing del matchId: " + matchId);
                return; // Evita di aggiungere un match corrotto
            }

            match.setCategory(category);
            System.out.println(category);
            match.setTimestamp(startingTime);
            System.out.println(startingTime);
            match.setMoves(movesList);
            System.out.println(movesList);

            newRawMatches.add(match);

            // **Ricerca del torneo corrispondente**
            Query query = new Query();
            query.addCriteria(Criteria.where("Edition").is(currentYear).and("Category").is(category));

            DocumentTournament tournament = mongoTemplate.findOne(query, DocumentTournament.class);

            if (tournament != null) {
                // Inizializza rawMatches se necessario
                if (tournament.getRawMatches() == null) {
                    tournament.setRawMatches(new ArrayList<>());
                }

                // Aggiunge il match alla lista dei rawMatches
                tournament.getRawMatches().add(match);

                // Salva l'aggiornamento
                repository.save(tournament);
                System.out.println("✅ Aggiunto match live " + matchId + " al torneo " + category);
            } else {
                System.out.println("⚠ Nessun torneo trovato per categoria: " + category);
            }
        }
    }
}