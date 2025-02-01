package it.unipi.enPassant.repositories;
import it.unipi.enPassant.model.requests.DocumentUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CRUDrepositoryUser extends MongoRepository<DocumentUser, String> {
}
