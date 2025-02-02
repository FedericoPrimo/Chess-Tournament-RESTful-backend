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
        System.out.println(key);
        return maxProgressive;
    }

    // Leggere ed eliminare la richiesta più vecchia (in testa)
    public Request consumeNextRequest() {
        initializeProgressives(); // Assicura che i progressivi esistano

        // Recupera i progressivi come String
        Long minProgressive = redisTemplate.opsForValue().increment(MIN_PROGRESSIVE_KEY);

        System.out.println("DEBUG: minProgressive = " + minProgressive);

        String key = REQUEST_PREFIX + (minProgressive-1);
        System.out.println("DEBUG: Recupero la richiesta con chiave: " + key);

        Request request = (Request) redisTemplate.opsForValue().get(key);

        if (request != null) {
            System.out.println("DEBUG: Richiesta trovata: " + request);
            redisTemplate.delete(key); // Elimina la richiesta dalla coda
            System.out.println("DEBUG: Richiesta eliminata. Nuovo minProgressive: " +
                    redisTemplate.opsForValue().get(MIN_PROGRESSIVE_KEY));
        } else {
            System.out.println("DEBUG: Coda vuota, tutte le richieste sono state revisionate.");

            redisTemplate.opsForValue().set(MIN_PROGRESSIVE_KEY, 1L);
            redisTemplate.opsForValue().set(MAX_PROGRESSIVE_KEY, 0L);
            System.out.println("DEBUG: Progressivi resettati.");

            return new Request("NO_REQUEST", "All requests have been reviewed.");
        }

        return request;
    }


    // Controllare il numero di richieste ancora in coda
    public Long getQueueSize() {
        initializeProgressives();

        // Recupera i progressivi e li converte correttamente in Long
        Long minProgressive = redisTemplate.opsForValue().get(MIN_PROGRESSIVE_KEY) != null ?
                Long.parseLong(redisTemplate.opsForValue().get(MIN_PROGRESSIVE_KEY).toString()) : 1L;

        Long maxProgressive = redisTemplate.opsForValue().get(MAX_PROGRESSIVE_KEY) != null ?
                Long.parseLong(redisTemplate.opsForValue().get(MAX_PROGRESSIVE_KEY).toString()) : 0L;

        // Assicuriamoci che il valore restituito sia sempre >= 0
        return Math.max(0, maxProgressive - minProgressive + 1);
    }

    // 🔹 Resettare la coda
    public void resetQueue() {
        redisTemplate.delete(MIN_PROGRESSIVE_KEY);
        redisTemplate.delete(MAX_PROGRESSIVE_KEY);
    }
}
