package it.unipi.enPassant.service.redisService;

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

    public LiveMatchService(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    private void initializeProgressiveMoveKey(String matchId) {
        String progressiveKey = "Live:" + matchId + ":progressive";
        if (readWithConsistency(progressiveKey) == null) {
            writeWithConsistency(progressiveKey, "1");
        }
    }

    public void addLiveMatch(String matchId, String category, String startingTime) {
        jedisCluster.sadd(LIVE_MATCHES_KEY, matchId);
        writeWithConsistency("Live:" + matchId + ":category", category);
        writeWithConsistency("Live:" + matchId + ":startingTime", startingTime);
        initializeProgressiveMoveKey(matchId);
    }

    public List<String> getLiveMatches() {
        return new ArrayList<>(jedisCluster.smembers(LIVE_MATCHES_KEY));
    }

    public Map<String, String> getMatchDetails(String matchId) {
        Map<String, String> details = new HashMap<>();
        details.put("category", readWithConsistency("Live:" + matchId + ":category"));
        details.put("startingTime", readWithConsistency("Live:" + matchId + ":startingTime"));
        details.put("winner", readWithConsistency("Live:" + matchId + ":winner"));
        details.put("endTime", readWithConsistency("Live:" + matchId + ":endTime"));
        details.put("ECO", readWithConsistency("Live:" + matchId + ":ECO"));
        return details;
    }

    public void removeLiveMatch(String matchId) {
        jedisCluster.srem(LIVE_MATCHES_KEY, matchId);
        jedisCluster.del("Live:" + matchId + ":category",
                "Live:" + matchId + ":startingTime",
                "Live:" + matchId + ":progressive",
                "Live:" + matchId + ":moveList");
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

    public void insertMatchResult(String matchId, String winner, String ECO) {
        String endTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        writeWithConsistency("Live:" + matchId + ":winner", winner);
        writeWithConsistency("Live:" + matchId + ":endTime", endTime);
        writeWithConsistency("Live:" + matchId + ":ECO", ECO);
    }

    /**
     * Writes a value to multiple replicas to ensure strong consistency.
     * The value is stored in multiple keys with a replication suffix.
     *
     * @param key   The base key to write.
     * @param value The value to be stored.
     */
    private void writeWithConsistency(String key, String value) {
        for (int i = 0; i < REPLICATION_FACTOR; i++) {
            jedisCluster.set(key + ":replica" + i, value);
        }
    }

    /**
     * Reads a value from multiple replicas and returns the most consistent one.
     * The function verifies if at least a majority of the replicas contain the same value
     * before considering the read operation successful.
     *
     * @param key The base key to read.
     * @return The most agreed-upon value or null if consensus is not reached.
     */
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
