package it.unipi.enPassant.service.redisService;

import it.unipi.enPassant.model.requests.redisModel.LiveMatch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class LiveMatchService {
    private static final String LIVE_MATCHES_KEY = "Live:matches";
    private static final int REPLICATION_FACTOR = 2; // Number of copies for consistency
    private final JedisCluster jedisCluster;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LiveMatchService(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    public void addLiveMatch(String matchId, String category, String startingTime) {
        try {
            LiveMatch liveMatch = new LiveMatch(category, startingTime);
            String matchJson = objectMapper.writeValueAsString(liveMatch);
            writeWithConsistency("Live:" + matchId, matchJson);
            jedisCluster.sadd(LIVE_MATCHES_KEY, matchId);
            initializeProgressiveMoveKey(matchId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing LiveMatch", e);
        }
    }

    public List<String> getLiveMatches() {
        return new ArrayList<>(jedisCluster.smembers(LIVE_MATCHES_KEY));
    }

    public LiveMatch getMatchDetails(String matchId) {
        try {
            String matchJson = readWithConsistency("Live:" + matchId);
            return matchJson != null ? objectMapper.readValue(matchJson, LiveMatch.class) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing LiveMatch", e);
        }
    }

    public void insertMatchResult(String matchId, String winner, String ECO) {
        try {
            String matchJson = readWithConsistency("Live:" + matchId);
            if (matchJson != null) {
                LiveMatch liveMatch = objectMapper.readValue(matchJson, LiveMatch.class);
                liveMatch.setWinner(winner);
                liveMatch.setEndTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                liveMatch.setECO(ECO);
                writeWithConsistency("Live:" + matchId, objectMapper.writeValueAsString(liveMatch));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error updating LiveMatch", e);
        }
    }

    public boolean removeLiveMatch(String matchId) {
        String matchJson = readWithConsistency("Live:" + matchId);
        if(matchJson == null) {
            return false;
        }

        for (int i = 0; i < REPLICATION_FACTOR; i++) {
            jedisCluster.del("Live:" + matchId + ":replica" + i);
            jedisCluster.del("Live:" + matchId + ":progressive");
            jedisCluster.del("Live:" + matchId + ":moveList");
        }
        jedisCluster.srem(LIVE_MATCHES_KEY, matchId);
        return true;
    }


    public Boolean addMoves(String moves, String user, String matchId) {
        String progressiveKey = "Live:" + matchId + ":progressive";
        String moveListKey = "Live:" + matchId + ":moveList";
        initializeProgressiveMoveKey(matchId);

        Long currentProgressive = readWithConsistency(progressiveKey) != null ?
                Long.parseLong(readWithConsistency(progressiveKey)) : 0L;

        String[] users = matchId.split("-");
        if (users.length != 2) {
            return false;
        }
        String user1 = users[0];
        String user2 = users[1];

        if (!user.equals(user1) && !user.equals(user2)) {
            return false;
        }

        boolean isUser1Turn = (currentProgressive % 2 == 1);
        if ((user.equals(user1) && !isUser1Turn) || (user.equals(user2) && isUser1Turn)) {
            return false;
        }

        moves = currentProgressive + "." + moves;
        writeWithConsistency(progressiveKey, String.valueOf(currentProgressive + 1));
        jedisCluster.rpush(moveListKey, moves);
        return true;
    }

    public List<String> retrieveMovesList(String matchId) {
        String moveListKey = "Live:" + matchId + ":moveList";
        List<String> moves = jedisCluster.lrange(moveListKey, 0, -1);
        return moves != null ? moves : new ArrayList<>();
    }

    private void initializeProgressiveMoveKey(String matchId) {
        String progressiveKey = "Live:" + matchId + ":progressive";
        if (readWithConsistency(progressiveKey) == null) {
            writeWithConsistency(progressiveKey, "1");
        }
    }

    private void writeWithConsistency(String key, String value) {
        for (int i = 0; i < REPLICATION_FACTOR; i++) {
            jedisCluster.set(key + ":replica" + i, value);
        }
    }

    private String readWithConsistency(String key) {
        Map<String, Integer> valuesCount = new HashMap<>();
        for (int i = 0; i < REPLICATION_FACTOR; i++) {
            String value = jedisCluster.get(key + ":replica" + i);
            if (value != null) {
                valuesCount.put(value, valuesCount.getOrDefault(value, 0) + 1);
            }
        }
        return valuesCount.entrySet().stream()
                .filter(entry -> entry.getValue() >= REPLICATION_FACTOR / 2 + 1)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
