package it.unipi.enPassant.repositories;
import it.unipi.enPassant.model.requests.DataUserModel;
import it.unipi.enPassant.model.requests.DocumentTournament;
import it.unipi.enPassant.model.requests.DocumentUser;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRepository extends MongoRepository<DocumentUser, String>{

    List<DocumentUser> findAll();

    @Aggregation(pipeline = {
            "{ $match: { '_id': ?0 } }"
    })
    DocumentUser findByUsername(String username);
}
