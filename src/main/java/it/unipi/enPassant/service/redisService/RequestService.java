package it.unipi.enPassant.service.redisService;

import it.unipi.enPassant.model.requests.redisModel.Request;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

@Service
public class RequestService {

    private static final String MIN_PROGRESSIVE_KEY = "Request:min_progressive";
    private static final String MAX_PROGRESSIVE_KEY = "Request:max_progressive";
    private static final String REQUEST_PREFIX = "Request:progressive_number:";

    private final JedisCluster jedisCluster;

    public RequestService(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    // Initialize progressive counters if they do not exist
    private void initializeProgressives() {
        if (jedisCluster.get(MIN_PROGRESSIVE_KEY) == null) {
            jedisCluster.set(MIN_PROGRESSIVE_KEY, "1");
        }
        if (jedisCluster.get(MAX_PROGRESSIVE_KEY) == null) {
            jedisCluster.set(MAX_PROGRESSIVE_KEY, "0");
        }
    }

    // Add a new request (to the queue)
    public Long addRequest(Request request) {
        initializeProgressives();

        Long maxProgressive = jedisCluster.incr(MAX_PROGRESSIVE_KEY);
        String key = REQUEST_PREFIX + maxProgressive;

        jedisCluster.set(key, request.toString());  // Convert the object to a string (JSON recommended)
        System.out.println(key);
        return maxProgressive;
    }

    // Read and remove the oldest request (from the front of the queue)
    public Request consumeNextRequest() {
        initializeProgressives();

        Long minProgressive = jedisCluster.incr(MIN_PROGRESSIVE_KEY);
        System.out.println("DEBUG: minProgressive = " + minProgressive);

        String key = REQUEST_PREFIX + (minProgressive - 1);
        System.out.println("DEBUG: Retrieving request with key: " + key);

        String requestData = jedisCluster.get(key);
        if (requestData != null) {
            System.out.println("DEBUG: Request found: " + requestData);
            jedisCluster.del(key); // Delete the request from the queue
            System.out.println("DEBUG: Request deleted. New minProgressive: " + jedisCluster.get(MIN_PROGRESSIVE_KEY));

            return new Request("REQUEST_FOUND", requestData);
        } else {
            System.out.println("DEBUG: Queue empty, all requests have been processed.");

            jedisCluster.set(MIN_PROGRESSIVE_KEY, "1");
            jedisCluster.set(MAX_PROGRESSIVE_KEY, "0");
            System.out.println("DEBUG: Progressives reset.");

            return new Request("NO_REQUEST", "All requests have been reviewed.");
        }
    }

    // Check the number of requests still in the queue
    public Long getQueueSize() {
        initializeProgressives();

        Long minProgressive = jedisCluster.get(MIN_PROGRESSIVE_KEY) != null ?
                Long.parseLong(jedisCluster.get(MIN_PROGRESSIVE_KEY)) : 1L;

        Long maxProgressive = jedisCluster.get(MAX_PROGRESSIVE_KEY) != null ?
                Long.parseLong(jedisCluster.get(MAX_PROGRESSIVE_KEY)) : 0L;

        return Math.max(0, maxProgressive - minProgressive + 1);
    }

    // Reset the queue
    public void resetQueue() {
        jedisCluster.del(MIN_PROGRESSIVE_KEY);
        jedisCluster.del(MAX_PROGRESSIVE_KEY);
    }
}
