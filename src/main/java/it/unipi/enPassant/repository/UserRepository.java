package it.unipi.enPassant.repository;

import it.unipi.enPassant.model.requests.LoginModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<LoginModel, String> {
  Optional<LoginModel> findByUsername(String username);
}
