package it.unipi.enPassant.service.redisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LiveMatchService {
    private static final String LIVE_MATCHES_KEY = "Live:matches";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private void initializeProgressiveMoveKey(String matchId) {
        String progressiveKey = "Live:" + matchId + ":progressive";
        if (redisTemplate.opsForValue().get(progressiveKey) == null) {
            redisTemplate.opsForValue().set(progressiveKey, 1L);
        }
    }

    /*si può implementare un inserimento multiplo nel controller*/
    public void addLiveMatch(String matchId, String category, String startingTime) {
        redisTemplate.opsForSet().add(LIVE_MATCHES_KEY, matchId);
        redisTemplate.opsForValue().set("Live:" + matchId + ":category", category);
        redisTemplate.opsForValue().set("Live:" + matchId + ":startingTime", startingTime);
        initializeProgressiveMoveKey(matchId);
    }

    public List<String> getLiveMatches() {
        return new ArrayList<>(redisTemplate.opsForSet().members(LIVE_MATCHES_KEY)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList()));
    }

    public Map<String, String> getMatchDetails(String matchId) {
        Map<String, String> details = new HashMap<>();
        details.put("category", (String) redisTemplate.opsForValue().get("Live:" + matchId + ":category"));
        details.put("startingTime", (String) redisTemplate.opsForValue().get("Live:" + matchId + ":startingTime"));
        return details;
    }

    public void removeLiveMatch(String matchId) {
        redisTemplate.opsForSet().remove(LIVE_MATCHES_KEY, matchId);
        redisTemplate.delete("Live:" + matchId + ":category");
        redisTemplate.delete("Live:" + matchId + ":startingTime");
        redisTemplate.delete("Live:" + matchId + ":progressive");
        redisTemplate.delete("Live:" + matchId + ":moveList");
    }

    public Boolean addMoves(String moves, String user, String matchId) {
        String progressiveKey = "Live:" + matchId + ":progressive";
        String moveListKey = "Live:" + matchId + ":moveList";
        initializeProgressiveMoveKey(matchId);

        Long currentProgressive = redisTemplate.opsForValue().get(progressiveKey) != null ?
                Long.parseLong(redisTemplate.opsForValue().get(progressiveKey).toString()) : 0L;
        // Estrarre user1 e user2 dal matchId
        String[] users = matchId.split("-");
        if (users.length != 2) {
            return false; // Formato matchId non valido
        }
        String user1 = users[0];
        String user2 = users[1];

        // Controllo se l'utente è nel match
        if (!user.equals(user1) && !user.equals(user2)) {
            return false; // Utente non appartiene al match
        }

        // Controllo se è il turno dell'utente
        boolean isUser1Turn = (currentProgressive % 2 == 1);
        if ((user.equals(user1) && !isUser1Turn) || (user.equals(user2) && isUser1Turn)) {
            return false; // Non è il turno dell'utente
        }

        moves = currentProgressive + "." + moves;
        // Incrementa il progressivo e salva la mossa
        redisTemplate.opsForValue().set(progressiveKey, currentProgressive + 1);
        redisTemplate.opsForList().rightPush(moveListKey, moves);
        return true;
    }

    public List<String> retrieveMovesList(String matchId) {
        String moveListKey = "Live:" + matchId + ":moveList";
        List<Object> moves = redisTemplate.opsForList().range(moveListKey, 0, -1);
        return moves != null ? moves.stream().map(Object::toString).collect(Collectors.toList()) : new ArrayList<>();
    }
}
