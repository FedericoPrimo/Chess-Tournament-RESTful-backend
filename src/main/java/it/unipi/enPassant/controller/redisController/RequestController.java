package it.unipi.enPassant.controller.redisController;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.unipi.enPassant.model.requests.redisModel.Request;
import it.unipi.enPassant.service.redisService.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/requests")
@Tag(name = "Review Request", description = "Review Request operations")
public class RequestController {

    @Autowired
    private RequestService requestService;

    // Add a new request to the queue
    @PostMapping("/insert")
    public ResponseEntity<Long> addRequest(@RequestBody Request request) {
        return ResponseEntity.ok(requestService.addRequest(request));
    }

    // Read and remove the next request from the queue
    @GetMapping("/next")
    public ResponseEntity<?> consumeNextRequest() {
        return ResponseEntity.ok(requestService.consumeNextRequest());
    }

    // Get the number of requests currently in the queue
    @GetMapping("/size")
    public ResponseEntity<Long> getQueueSize() {
        return ResponseEntity.ok(requestService.getQueueSize());
    }

    // Completely reset the queue
    @DeleteMapping("/reset")
    public ResponseEntity<String> resetQueue() {
        requestService.resetQueue();
        return ResponseEntity.ok("Queue has been reset.");
    }
}
