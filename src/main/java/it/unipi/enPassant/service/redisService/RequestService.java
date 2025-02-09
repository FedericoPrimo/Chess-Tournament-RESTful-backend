package it.unipi.enPassant.service.redisService;

import it.unipi.enPassant.model.requests.redisModel.Request;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RequestService {

    private static final String MIN_PROGRESSIVE_KEY = "Request:min_progressive";
    private static final String MAX_PROGRESSIVE_KEY = "Request:max_progressive";
    private static final String REQUEST_PREFIX = "Request:progressive_number:";

    private final JedisPool jedisPool;

    public RequestService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    // Initialize progressive counters if they do not exist
    private void initializeProgressives() {
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.get(MIN_PROGRESSIVE_KEY) == null) {
                jedis.set(MIN_PROGRESSIVE_KEY, "1");
            }
            if (jedis.get(MAX_PROGRESSIVE_KEY) == null) {
                jedis.set(MAX_PROGRESSIVE_KEY, "0");
            }
        }
    }

    // Add a new request (to the queue)
    public Long addRequest(Request request) {
        initializeProgressives();

        try (Jedis jedis = jedisPool.getResource()) {
            Long maxProgressive = jedis.incr(MAX_PROGRESSIVE_KEY);
            String key = REQUEST_PREFIX + maxProgressive;

            jedis.set(key, request.toString());  // Convert the object to a string (JSON recommended)
            System.out.println(key);
            return maxProgressive;
        }
    }

    // Read and remove the oldest request (from the front of the queue)
    public Request consumeNextRequest() {
        initializeProgressives();

        try (Jedis jedis = jedisPool.getResource()) {
            Long minProgressive = jedis.incr(MIN_PROGRESSIVE_KEY);
            System.out.println("DEBUG: minProgressive = " + minProgressive);

            String key = REQUEST_PREFIX + (minProgressive - 1);
            System.out.println("DEBUG: Retrieving request with key: " + key);

            String requestData = jedis.get(key);
            if (requestData != null) {
                System.out.println("DEBUG: Request found: " + requestData);
                jedis.del(key); // Delete the request from the queue
                System.out.println("DEBUG: Request deleted. New minProgressive: " + jedis.get(MIN_PROGRESSIVE_KEY));

                return new Request("REQUEST_FOUND", requestData);
            } else {
                System.out.println("DEBUG: Queue empty, all requests have been processed.");

                jedis.set(MIN_PROGRESSIVE_KEY, "1");
                jedis.set(MAX_PROGRESSIVE_KEY, "0");
                System.out.println("DEBUG: Progressives reset.");

                return new Request("NO_REQUEST", "All requests have been reviewed.");
            }
        }
    }

    // Check the number of requests still in the queue
    public Long getQueueSize() {
        initializeProgressives();

        try (Jedis jedis = jedisPool.getResource()) {
            Long minProgressive = jedis.get(MIN_PROGRESSIVE_KEY) != null ?
                    Long.parseLong(jedis.get(MIN_PROGRESSIVE_KEY)) : 1L;

            Long maxProgressive = jedis.get(MAX_PROGRESSIVE_KEY) != null ?
                    Long.parseLong(jedis.get(MAX_PROGRESSIVE_KEY)) : 0L;

            return Math.max(0, maxProgressive - minProgressive + 1);
        }
    }

    // Reset the queue
    public void resetQueue() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(MIN_PROGRESSIVE_KEY);
            jedis.del(MAX_PROGRESSIVE_KEY);
        }
    }
}
