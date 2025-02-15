package it.unipi.enPassant.service.redisService;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.Map;
import java.util.Set;

@Service
public class ManagePlayerService {

    private static final String DISQUALIFIED_PLAYERS_KEY = "disqualified_players";
    private static final String REGISTERED_PLAYERS_KEY = "registered_players";

    private final JedisCluster jedisCluster;
    private final MongoTemplate mongoTemplate;

    public ManagePlayerService(JedisCluster jedisCluster, MongoTemplate mongoTemplate) {
        this.jedisCluster = jedisCluster;
        this.mongoTemplate = mongoTemplate;
    }

    /* DISQUALIFIED PLAYER SECTION */
    public boolean addDisqualifiedPlayer(String playerId) {
        boolean check = jedisCluster.sismember(DISQUALIFIED_PLAYERS_KEY, playerId);
        if (check) {
            return false;
        }
        jedisCluster.sadd(DISQUALIFIED_PLAYERS_KEY, playerId);
        return true;
    }

    public boolean removeDisqualifiedPlayer(String playerId) {
        boolean check = jedisCluster.sismember(DISQUALIFIED_PLAYERS_KEY, playerId);
        if (!check) {
            return false;
        }
        jedisCluster.srem(DISQUALIFIED_PLAYERS_KEY, playerId);
        return true;
    }

    public boolean isPlayerDisqualified(String playerId) {
        return jedisCluster.sismember(DISQUALIFIED_PLAYERS_KEY, playerId);
    }

    public Set<String> getAllDisqualifiedPlayers() {
        return jedisCluster.smembers(DISQUALIFIED_PLAYERS_KEY);
    }

    /* ENROLL CATEGORY SECTION */
    public boolean registerPlayer(String playerId, String category) {
        boolean status = jedisCluster.hexists(REGISTERED_PLAYERS_KEY, playerId);
        if(status)
            return false;

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(playerId).and("state").is(1));

        boolean isDisqualified = mongoTemplate.exists(query, "user");

        if (isDisqualified) {
            return false;
        }
        jedisCluster.hset(REGISTERED_PLAYERS_KEY, playerId, category);
        return true;
    }

    public boolean removeRegisteredPlayer(String playerId) {
        boolean status = jedisCluster.hexists(REGISTERED_PLAYERS_KEY, playerId);
        if(!status)
            return false;
        jedisCluster.hdel(REGISTERED_PLAYERS_KEY, playerId);
        return true;
    }

    public boolean isPlayerRegistered(String playerId) {
        return jedisCluster.hexists(REGISTERED_PLAYERS_KEY, playerId);
    }

    public Map<String, String> getAllRegisteredPlayers() {
        return jedisCluster.hgetAll(REGISTERED_PLAYERS_KEY);
    }
}


