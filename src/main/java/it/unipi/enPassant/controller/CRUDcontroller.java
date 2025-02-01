package it.unipi.enPassant.controller;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class CRUDcontroller<T, ID> {

    protected final MongoRepository<T, ID> repository;;

    public CRUDcontroller(MongoRepository<T, ID> repository) {
        this.repository = repository;
    }

    @GetMapping("/read")
    public List<T> getAll() {
        return repository.findAll();
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
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}