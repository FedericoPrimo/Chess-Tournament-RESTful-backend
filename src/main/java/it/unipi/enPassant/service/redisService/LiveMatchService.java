package it.unipi.enPassant.service.redisService;

import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class LiveMatchService {
    private static final String LIVE_MATCHES_KEY = "Live:matches";
    private final JedisCluster jedisCluster;

    public LiveMatchService(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    // Initialize progressive move key if it does not exist
    private void initializeProgressiveMoveKey(String matchId) {
        String progressiveKey = "Live:" + matchId + ":progressive";
        if (jedisCluster.get(progressiveKey) == null) {
            jedisCluster.set(progressiveKey, "1");
        }
    }

    // Add a live match to the Redis set and store its details
    public void addLiveMatch(String matchId, String category, String startingTime) {
        jedisCluster.sadd(LIVE_MATCHES_KEY, matchId);
        jedisCluster.set("Live:" + matchId + ":category", category);
        jedisCluster.set("Live:" + matchId + ":startingTime", startingTime);
        initializeProgressiveMoveKey(matchId);
    }

    // Retrieve the list of live matches
    public List<String> getLiveMatches() {
        return new ArrayList<>(jedisCluster.smembers(LIVE_MATCHES_KEY));
    }

    // Retrieve match details from Redis
    public Map<String, String> getMatchDetails(String matchId) {
        Map<String, String> details = new HashMap<>();
        details.put("category", jedisCluster.get("Live:" + matchId + ":category"));
        details.put("startingTime", jedisCluster.get("Live:" + matchId + ":startingTime"));
        details.put("winner", jedisCluster.get("Live:" + matchId + ":winner"));
        details.put("endTime", jedisCluster.get("Live:" + matchId + ":endTime"));
        details.put("ECO", jedisCluster.get("Live:" + matchId + ":ECO"));
        return details;
    }

    // Remove a live match and its associated data from Redis
    public void removeLiveMatch(String matchId) {
        jedisCluster.srem(LIVE_MATCHES_KEY, matchId);
        jedisCluster.del("Live:" + matchId + ":category",
                "Live:" + matchId + ":startingTime",
                "Live:" + matchId + ":progressive",
                "Live:" + matchId + ":moveList");
    }

    // Add a move to the match while ensuring turn order
    public Boolean addMoves(String moves, String user, String matchId) {
        String progressiveKey = "Live:" + matchId + ":progressive";
        String moveListKey = "Live:" + matchId + ":moveList";
        initializeProgressiveMoveKey(matchId);

        Long currentProgressive = jedisCluster.get(progressiveKey) != null ?
                Long.parseLong(jedisCluster.get(progressiveKey)) : 0L;

        // Extract user1 and user2 from matchId
        String[] users = matchId.split("-");
        if (users.length != 2) {
            return false; // Invalid matchId format
        }
        String user1 = users[0];
        String user2 = users[1];

        // Check if the user is part of the match
        if (!user.equals(user1) && !user.equals(user2)) {
            return false; // User is not part of the match
        }

        // Check if it is the user's turn
        boolean isUser1Turn = (currentProgressive % 2 == 1);
        if ((user.equals(user1) && !isUser1Turn) || (user.equals(user2) && isUser1Turn)) {
            return false; // Not the user's turn
        }

        moves = currentProgressive + "." + moves;
        // Increment the progressive counter and save the move
        jedisCluster.set(progressiveKey, String.valueOf(currentProgressive + 1));
        jedisCluster.rpush(moveListKey, moves);
        return true;
    }

    // Retrieve the list of moves for a specific match
    public List<String> retrieveMovesList(String matchId) {
        String moveListKey = "Live:" + matchId + ":moveList";
        List<String> moves = jedisCluster.lrange(moveListKey, 0, -1);
        return moves != null ? moves : new ArrayList<>();
    }

    // Insert the result of a match into Redis
    public void insertMatchResult(String matchId, String winner, String ECO) {
        String endTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        jedisCluster.set("Live:" + matchId + ":winner", winner);
        jedisCluster.set("Live:" + matchId + ":endTime", endTime);
        jedisCluster.set("Live:" + matchId + ":ECO", ECO);
    }
}
