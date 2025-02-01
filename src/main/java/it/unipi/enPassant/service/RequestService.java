package it.unipi.enPassant.service;
import it.unipi.enPassant.model.requests.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

    private static final String MIN_PROGRESSIVE_KEY = "Request:min_progressive";
    private static final String MAX_PROGRESSIVE_KEY = "Request:max_progressive";
    private static final String REQUEST_PREFIX = "Request:progressive_number:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Inizializza i progressivi se non esistono
    private void initializeProgressives() {
        if (redisTemplate.opsForValue().get(MIN_PROGRESSIVE_KEY) == null) {
            redisTemplate.opsForValue().set(MIN_PROGRESSIVE_KEY, 1L);
        }
        if (redisTemplate.opsForValue().get(MAX_PROGRESSIVE_KEY) == null) {
            redisTemplate.opsForValue().set(MAX_PROGRESSIVE_KEY, 0L);
        }
    }

    // Aggiungere una nuova richiesta (in coda)
    public Long addRequest(Request request) {
        initializeProgressives();

        Long maxProgressive = redisTemplate.opsForValue().increment(MAX_PROGRESSIVE_KEY);
        String key = REQUEST_PREFIX + maxProgressive;

        redisTemplate.opsForValue().set(key, request);
        return maxProgressive;
    }

    // Leggere ed eliminare la richiesta più vecchia (in testa)
    public Request consumeNextRequest() {
        initializeProgressives();

        Long minProgressive = (Long) redisTemplate.opsForValue().get(MIN_PROGRESSIVE_KEY);
        Long maxProgressive = (Long) redisTemplate.opsForValue().get(MAX_PROGRESSIVE_KEY);

        if (minProgressive > maxProgressive) {
            return null; // La coda è vuota
        }

        String key = REQUEST_PREFIX + minProgressive;
        Request request = (Request) redisTemplate.opsForValue().get(key);

        if (request != null) {
            redisTemplate.delete(key);
            redisTemplate.opsForValue().increment(MIN_PROGRESSIVE_KEY); // Avanza la testa della coda
        }

        return request;
    }

    // Controllare il numero di richieste ancora in coda
    public Long getQueueSize() {
        initializeProgressives();

        Long minProgressive = (Long) redisTemplate.opsForValue().get(MIN_PROGRESSIVE_KEY);
        Long maxProgressive = (Long) redisTemplate.opsForValue().get(MAX_PROGRESSIVE_KEY);

        return maxProgressive - minProgressive + 1;
    }

    // 🔹 Resettare la coda
    public void resetQueue() {
        redisTemplate.delete(MIN_PROGRESSIVE_KEY);
        redisTemplate.delete(MAX_PROGRESSIVE_KEY);
    }
}
