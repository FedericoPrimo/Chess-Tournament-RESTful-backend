package it.unipi.enPassant.service.redisService;

import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.Map;
import java.util.Set;

@Service
public class ManagePlayerService {

    private static final String DISQUALIFIED_PLAYERS_KEY = "disqualified_players";
    private static final String REGISTERED_PLAYERS_KEY = "registered_players";

    private final JedisCluster jedisCluster;

    public ManagePlayerService(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    /* DISQUALIFIED PLAYER SECTION */
    public void addDisqualifiedPlayer(String playerId) {
        jedisCluster.sadd(DISQUALIFIED_PLAYERS_KEY, playerId);
    }

    public void removeDisqualifiedPlayer(String playerId) {
        jedisCluster.srem(DISQUALIFIED_PLAYERS_KEY, playerId);
    }

    public boolean isPlayerDisqualified(String playerId) {
        return jedisCluster.sismember(DISQUALIFIED_PLAYERS_KEY, playerId);
    }

    public Set<String> getAllDisqualifiedPlayers() {
        return jedisCluster.smembers(DISQUALIFIED_PLAYERS_KEY);
    }

    /* ENROLL CATEGORY SECTION */
    public void registerPlayer(String playerId, String category) {
        jedisCluster.hset(REGISTERED_PLAYERS_KEY, playerId, category);
    }

    public void removeRegisteredPlayer(String playerId) {
        jedisCluster.hdel(REGISTERED_PLAYERS_KEY, playerId);
    }

    public boolean isPlayerRegistered(String playerId) {
        return jedisCluster.hexists(REGISTERED_PLAYERS_KEY, playerId);
    }

    public Map<String, String> getAllRegisteredPlayers() {
        return jedisCluster.hgetAll(REGISTERED_PLAYERS_KEY);
    }
}
