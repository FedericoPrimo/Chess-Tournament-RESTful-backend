package it.unipi.enPassant.service.redisService;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class LiveMatchService {
    private static final String LIVE_MATCHES_KEY = "Live:matches";
    private final JedisPool jedisPool;

    public LiveMatchService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    // Initialize progressive move key if it does not exist
    private void initializeProgressiveMoveKey(String matchId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String progressiveKey = "Live:" + matchId + ":progressive";
            if (jedis.get(progressiveKey) == null) {
                jedis.set(progressiveKey, "1");
            }
        }
    }

    // Add a live match to the Redis set and store its details
    public void addLiveMatch(String matchId, String category, String startingTime) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd(LIVE_MATCHES_KEY, matchId);
            jedis.set("Live:" + matchId + ":category", category);
            jedis.set("Live:" + matchId + ":startingTime", startingTime);
            initializeProgressiveMoveKey(matchId);
        }
    }

    // Retrieve the list of live matches
    public List<String> getLiveMatches() {
        try (Jedis jedis = jedisPool.getResource()) {
            return new ArrayList<>(jedis.smembers(LIVE_MATCHES_KEY));
        }
    }

    // Retrieve match details from Redis
    public Map<String, String> getMatchDetails(String matchId) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> details = new HashMap<>();
            details.put("category", jedis.get("Live:" + matchId + ":category"));
            details.put("startingTime", jedis.get("Live:" + matchId + ":startingTime"));
            details.put("winner", jedis.get("Live:" + matchId + ":winner"));
            details.put("endTime", jedis.get("Live:" + matchId + ":endTime"));
            details.put("ECO", jedis.get("Live:" + matchId + ":ECO"));
            return details;
        }
    }

    // Remove a live match and its associated data from Redis
    public void removeLiveMatch(String matchId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.srem(LIVE_MATCHES_KEY, matchId);
            jedis.del("Live:" + matchId + ":category",
                    "Live:" + matchId + ":startingTime",
                    "Live:" + matchId + ":progressive",
                    "Live:" + matchId + ":moveList");
        }
    }

    // Add a move to the match while ensuring turn order
    public Boolean addMoves(String moves, String user, String matchId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String progressiveKey = "Live:" + matchId + ":progressive";
            String moveListKey = "Live:" + matchId + ":moveList";
            initializeProgressiveMoveKey(matchId);

            Long currentProgressive = jedis.get(progressiveKey) != null ?
                    Long.parseLong(jedis.get(progressiveKey)) : 0L;

            // Extract user1 and user2 from matchId
            String[] users = matchId.split("-");
            if (users.length != 2) {
                return false;
            }
            String user1 = users[0];
            String user2 = users[1];

            // Check if the user is part of the match
            if (!user.equals(user1) && !user.equals(user2)) {
                return false;
            }

            // Check if it is the user's turn
            boolean isUser1Turn = (currentProgressive % 2 == 1);
            if ((user.equals(user1) && !isUser1Turn) || (user.equals(user2) && isUser1Turn)) {
                return false;
            }

            moves = currentProgressive + "." + moves;
            // Increment the progressive counter and save the move
            jedis.set(progressiveKey, String.valueOf(currentProgressive + 1));
            jedis.rpush(moveListKey, moves);
            return true;
        }
    }

    // Retrieve the list of moves for a specific match
    public List<String> retrieveMovesList(String matchId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String moveListKey = "Live:" + matchId + ":moveList";
            List<String> moves = jedis.lrange(moveListKey, 0, -1);
            return moves != null ? moves : new ArrayList<>();
        }
    }

    // Insert the result of a match into Redis
    public void insertMatchResult(String matchId, String winner, String ECO) {
        try (Jedis jedis = jedisPool.getResource()) {
            String endTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            jedis.set("Live:" + matchId + ":winner", winner);
            jedis.set("Live:" + matchId + ":endTime", endTime);
            jedis.set("Live:" + matchId + ":ECO", ECO);
        }
    }
}
