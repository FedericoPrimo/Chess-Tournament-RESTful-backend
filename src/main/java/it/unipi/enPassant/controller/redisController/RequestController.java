package it.unipi.enPassant.controller.redisController;

import it.unipi.enPassant.model.requests.redisModel.Request;
import it.unipi.enPassant.service.redisService.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    // Aggiungere una nuova richiesta in coda
    @PostMapping("/insert")
    public Long addRequest(@RequestBody Request request) {
        return requestService.addRequest(request);
    }

    // Leggere ed eliminare la prossima richiesta nella coda
    @GetMapping("/next")
    public Request consumeNextRequest() {
        return requestService.consumeNextRequest();
    }

    // Ottenere il numero di richieste attualmente in coda
    @GetMapping("/size")
    public Long getQueueSize() {
        return requestService.getQueueSize();
    }

    // Resettare completamente la coda
    @DeleteMapping("/reset")
    public void resetQueue() {
        requestService.resetQueue();
    }
}

