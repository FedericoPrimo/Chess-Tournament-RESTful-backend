package it.unipi.enPassant.repositories;
import it.unipi.enPassant.model.requests.DocumentTournament;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CRUDrepositoryTournament extends MongoRepository<DocumentTournament, String> {
}
