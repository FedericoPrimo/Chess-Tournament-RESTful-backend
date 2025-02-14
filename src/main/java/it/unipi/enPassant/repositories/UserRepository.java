package it.unipi.enPassant.repositories;
import it.unipi.enPassant.model.requests.mongoModel.user.DocumentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRepository extends MongoRepository<DocumentUser, String>{

    //List<DocumentUser> findAll();
    Page<DocumentUser> findAll(Pageable pageable);

    @Aggregation(pipeline = {
            "{ $match: { '_id': ?0 } }"
    })
    DocumentUser findByUsername(String username);
}
