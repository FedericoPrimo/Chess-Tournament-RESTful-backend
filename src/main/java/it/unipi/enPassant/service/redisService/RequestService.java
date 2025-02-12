package it.unipi.enPassant.service.redisService;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unipi.enPassant.model.requests.redisModel.Request;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

@Service
public class RequestService {

    private static final String MIN_PROGRESSIVE_KEY = "Request:min_progressive";
    private static final String MAX_PROGRESSIVE_KEY = "Request:max_progressive";
    private static final String REQUEST_PREFIX = "Request:progressive_number:";

    private final JedisCluster jedisCluster;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RequestService(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    private void initializeProgressives() {
        if (jedisCluster.get(MIN_PROGRESSIVE_KEY) == null) {
            jedisCluster.set(MIN_PROGRESSIVE_KEY, "1");
        }
        if (jedisCluster.get(MAX_PROGRESSIVE_KEY) == null) {
            jedisCluster.set(MAX_PROGRESSIVE_KEY, "0");
        }
    }

    public Long addRequest(Request request) {
        initializeProgressives();
        Long maxProgressive = jedisCluster.incr(MAX_PROGRESSIVE_KEY);
        String key = REQUEST_PREFIX + maxProgressive;

        try {
            String jsonRequest = objectMapper.writeValueAsString(request);
            jedisCluster.set(key, jsonRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return maxProgressive;
    }

    public Request consumeNextRequest() {
        initializeProgressives();
        Long minProgressive = jedisCluster.incr(MIN_PROGRESSIVE_KEY);
        String key = REQUEST_PREFIX + (minProgressive - 1);

        String requestData = jedisCluster.get(key);
        if (requestData != null) {
            try {
                jedisCluster.del(key);
                return objectMapper.readValue(requestData, Request.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Request("NO_REQUEST", "All requests have been reviewed.");
    }

    public Long getQueueSize() {
        initializeProgressives();
        Long minProgressive = jedisCluster.get(MIN_PROGRESSIVE_KEY) != null ?
                Long.parseLong(jedisCluster.get(MIN_PROGRESSIVE_KEY)) : 1L;
        Long maxProgressive = jedisCluster.get(MAX_PROGRESSIVE_KEY) != null ?
                Long.parseLong(jedisCluster.get(MAX_PROGRESSIVE_KEY)) : 0L;
        return Math.max(0, maxProgressive - minProgressive + 1);
    }

    public void resetQueue() {
        jedisCluster.del(MIN_PROGRESSIVE_KEY);
        jedisCluster.del(MAX_PROGRESSIVE_KEY);
    }
}
