package it.unipi.enPassant.controller.mongoController.mongoCRUD;
import it.unipi.enPassant.model.requests.mongoModel.tournament.DocumentTournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public abstract class CRUDcontroller<T, ID> {

    protected final MongoRepository<T, ID> repository;;

    public CRUDcontroller(MongoRepository<T, ID> repository) {
        this.repository = repository;
    }

    @GetMapping("/read/{pageNo}/{pageSize}")
    public List<T> getAll(@PathVariable int pageNo, @PathVariable int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<T> page = repository.findAll(pageable);
        return page.getContent();
    }

    @PostMapping("/create")
    public T create(@RequestBody T entity) {
        return repository.save(entity);
    }

    @GetMapping("findById/{id}")
    public ResponseEntity<T> getById(@PathVariable ID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("update/{id}")
    public ResponseEntity<T> update(@PathVariable ID id, @RequestBody T entity) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repository.save(entity));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable ID id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Entity with ID " + id + " not found"));
        }

        repository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Entity with ID " + id + " deleted successfully"));
    }
}