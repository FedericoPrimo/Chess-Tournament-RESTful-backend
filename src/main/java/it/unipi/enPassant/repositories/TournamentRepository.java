package it.unipi.enPassant.repositories;
import it.unipi.enPassant.model.requests.mongoModel.tournament.DocumentTournament;
import it.unipi.enPassant.model.requests.mongoModel.tournament.MatchListModel;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentRepository extends MongoRepository<DocumentTournament, String>{

    List<DocumentTournament> findAll();

    @Aggregation(pipeline = {
            "{ $match: { 'Edition': ?0, 'Category': ?1, 'Location': ?2 } }",
            "{ $unwind: '$RawMatches' }",
            "{ $project: { '_id': 0, 'White': '$RawMatches.White', 'Black': '$RawMatches.Black' } }"
    })
    List<MatchListModel> findTournamentMatches(int Edition, String Category, String Location);

    //test query  db.tournaments.aggregate([{ $match: { "Edition": 2004, "Category": "Blitz", "Location": "Amsterdam" } }, { $unwind: "$RawMatches" }, { $match: { "RawMatches.White": "alekhine_alexander", "RawMatches.Black": "lasker_emanuel" } }, { $project: { "_id": 0, "RawMatches": 1 } }])
    @Aggregation(pipeline = {
            "{ $match: { 'Edition': ?0, 'Category': ?1, 'Location': ?2 } }",
            "{ $unwind: '$RawMatches' }",
            "{ $match: { 'RawMatches.White': ?3, 'RawMatches.Black': ?4 } }"
    })
    DocumentTournament findMatchofTournament(int edition, String category, String location, String Black, String White);
}
