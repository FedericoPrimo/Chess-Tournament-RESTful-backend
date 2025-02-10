package it.unipi.enPassant.service.redisService;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ManagePlayerService {

    private static final String DISQUALIFIED_PLAYERS_KEY = "disqualified_players";
    private static final String REGISTERED_PLAYERS_KEY = "registered_players";

    private final JedisPool jedisPool;

    public ManagePlayerService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /* DISQUALIFIED PLAYER SECTION */
    public void addDisqualifiedPlayer(String playerId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd(DISQUALIFIED_PLAYERS_KEY, playerId);
        }
    }

    public void removeDisqualifiedPlayer(String playerId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.srem(DISQUALIFIED_PLAYERS_KEY, playerId);
        }
    }

    public boolean isPlayerDisqualified(String playerId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sismember(DISQUALIFIED_PLAYERS_KEY, playerId);
        }
    }

    public Set<String> getAllDisqualifiedPlayers() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(DISQUALIFIED_PLAYERS_KEY);
        }
    }

    /* ENROLL CATEGORY SECTION */
    public void registerPlayer(String playerId, String category) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(REGISTERED_PLAYERS_KEY, playerId, category);
        }
    }

    public void removeRegisteredPlayer(String playerId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hdel(REGISTERED_PLAYERS_KEY, playerId);
        }
    }

    public boolean isPlayerRegistered(String playerId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hexists(REGISTERED_PLAYERS_KEY, playerId);
        }
    }

    public Map<String, String> getAllRegisteredPlayers() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(REGISTERED_PLAYERS_KEY);
        }
    }
}


