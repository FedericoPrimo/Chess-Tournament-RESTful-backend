package it.unipi.enPassant.service.redisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class ManagePlayerService {

    private static final String DISQUALIFIED_PLAYERS_KEY = "disqualified_players";
    private static final String REGISTERED_PLAYERS_KEY = "registered_players";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /*DISQUALIFIED PLAYER SECTION*/
    public void addDisqualifiedPlayer(String playerId) {
        redisTemplate.opsForSet().add(DISQUALIFIED_PLAYERS_KEY, playerId);
    }

    public void removeDisqualifiedPlayer(String playerId) {
        redisTemplate.opsForSet().remove(DISQUALIFIED_PLAYERS_KEY, playerId);
    }

    public boolean isPlayerDisqualified(String playerId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(DISQUALIFIED_PLAYERS_KEY, playerId));
    }

    public Set<Object> getAllDisqualifiedPlayers() {
        return redisTemplate.opsForSet().members(DISQUALIFIED_PLAYERS_KEY);
    }

    /*ENROLL CATEGORY SECTION*/
    public void registerPlayer(String playerId, String category) {
        redisTemplate.opsForHash().put(REGISTERED_PLAYERS_KEY, playerId, category);
    }

    public void removeRegisteredPlayer(String playerId) {
        redisTemplate.opsForHash().delete(REGISTERED_PLAYERS_KEY, playerId);
    }

    public boolean isPlayerRegistered(String playerId) {
        return redisTemplate.opsForHash().hasKey(REGISTERED_PLAYERS_KEY, playerId);
    }

    public Map<Object, Object> getAllRegisteredPlayers() {
        return redisTemplate.opsForHash().entries(REGISTERED_PLAYERS_KEY);
    }
}
