package it.unipi.enPassant.service.redisService;

import it.unipi.enPassant.model.requests.redisModel.LiveMatch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;
import redis.clients.jedis.util.JedisClusterCRC16;

import java.awt.desktop.SystemSleepEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LiveMatchService {
    private static final String LIVE_MATCHES_KEY = "Live:matches";
    private static final int REPLICATION_FACTOR = 2; // Number of copies for consistency
    private final JedisCluster jedisCluster;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LiveMatchService(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    public boolean addLiveMatch(String matchId, String category, String startingTime) {
        try {
            LiveMatch liveMatch = new LiveMatch(category, startingTime);
            String matchJson = objectMapper.writeValueAsString(liveMatch);
            writeWithConsistency(LIVE_MATCHES_KEY + matchId, matchJson);
            jedisCluster.sadd(LIVE_MATCHES_KEY, matchId);
            initializeProgressiveMoveKey(matchId);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    public List<String> getLiveMatches() {
        return new ArrayList<>(jedisCluster.smembers(LIVE_MATCHES_KEY));
    }

    public LiveMatch getMatchDetails(String matchId) {
        try {
            String matchJson = readRedis(LIVE_MATCHES_KEY + matchId);
            return matchJson != null ? objectMapper.readValue(matchJson, LiveMatch.class) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing LiveMatch", e);
        }
    }

    public boolean insertMatchResult(String matchId, String winner, String ECO) {
        try {
            String matchJson = readRedis(LIVE_MATCHES_KEY + matchId);
            if (matchJson != null) {
                LiveMatch liveMatch = objectMapper.readValue(matchJson, LiveMatch.class);
                liveMatch.setWinner(winner);
                liveMatch.setEndTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                liveMatch.setECO(ECO);
                writeWithConsistency(LIVE_MATCHES_KEY + matchId, objectMapper.writeValueAsString(liveMatch));
            }
            else {
                return false;
            }
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    public boolean removeLiveMatch(String matchId) {
        String matchJson = readRedis(LIVE_MATCHES_KEY + matchId);
        if(matchJson == null) {
            return false;
        }


        jedisCluster.del(LIVE_MATCHES_KEY + matchId);
        jedisCluster.del(LIVE_MATCHES_KEY + matchId + ":progressive");
        jedisCluster.del(LIVE_MATCHES_KEY + matchId + ":moveList");
        return true;
    }


    public Boolean addMoves(String moves, String user, String matchId) {
        String progressiveKey = LIVE_MATCHES_KEY + matchId + ":progressive";
        String moveListKey = LIVE_MATCHES_KEY + matchId + ":moveList";
        initializeProgressiveMoveKey(matchId);

        Long currentProgressive = readRedis(progressiveKey) != null ?
                Long.parseLong(readRedis(progressiveKey)) : 0L;

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
        String moveListKey = LIVE_MATCHES_KEY + matchId + ":moveList";
        List<String> moves = jedisCluster.lrange(moveListKey, 0, -1);
        return moves != null ? moves : new ArrayList<>();
    }

    private void initializeProgressiveMoveKey(String matchId) {
        String progressiveKey = LIVE_MATCHES_KEY + matchId + ":progressive";
        if (readRedis(progressiveKey) == null) {
            writeWithConsistency(progressiveKey, "1");
        }
    }
    

    private void writeWithConsistency(String key, String value) {
        jedisCluster.set(key, value);
        long replicasAck = jedisCluster.waitReplicas(key, REPLICATION_FACTOR, 10000);
        if (replicasAck < REPLICATION_FACTOR) {
            throw new RuntimeException("Write not replicated on at least " + REPLICATION_FACTOR + " replicas");
        }

    }

    private String readRedis(String key) {
        return jedisCluster.get(key);
    }
}
