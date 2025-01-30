package it.unipi.enPassant.repositories;

import it.unipi.enPassant.model.requests.DocumentTournament;
import it.unipi.enPassant.model.requests.MatchListModel;
import it.unipi.enPassant.model.requests.MatchModel;
import it.unipi.enPassant.model.requests.TournamentModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends MongoRepository<DocumentTournament, String>{
    @Query(value = "{}", fields = "{ 'category': 1, 'edition': 1, 'location': 1 }")
    List<TournamentModel> findAllProjected();

    @Query(value = "{ 'edition': ?0, 'category': ?1, 'location': ?2 }",
            fields = "{ 'rawMatches.White': 1, 'rawMatches.Black': 1 }")
    List<MatchListModel> findTournamentMatches(int edition, String category, String location);

    @Query(value = "{ 'edition': ?0, 'category': ?1, 'location': ?2, 'rawMatches.White': ?3, 'rawMatches.Black': ?4 }",
            fields = "{ 'rawMatches': 1}")
    MatchModel findMatchofTournament(int edition, String category, String location, String White, String Black);
    /*
    chatGPT suggerirebbe di fare la query cosi
     @Query(value = "{ 'edition': ?0, 'category': ?1, 'location': ?2, 'rawMatches':
                        { $elemMatch: { 'white': ?3, 'black': ?4 } } }",
           fields = "{ 'rawMatches.$': 1, '_id': 0 }")
     */
}
