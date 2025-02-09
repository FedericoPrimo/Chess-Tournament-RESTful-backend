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

    // Add a new request to the queue
    @PostMapping("/insert")
    public Long addRequest(@RequestBody Request request) {
        return requestService.addRequest(request);
    }

    // Read and remove the next request from the queue
    @GetMapping("/next")
    public Request consumeNextRequest() {
        return requestService.consumeNextRequest();
    }

    // Get the number of requests currently in the queue
    @GetMapping("/size")
    public Long getQueueSize() {
        return requestService.getQueueSize();
    }

    // Completely reset the queue
    @DeleteMapping("/reset")
    public void resetQueue() {
        requestService.resetQueue();
    }
}
