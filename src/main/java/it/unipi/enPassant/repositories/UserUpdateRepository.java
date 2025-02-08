package it.unipi.enPassant.repositories;

import it.unipi.enPassant.model.requests.mongoModel.tournament.UserMatchUpdateModel;
import it.unipi.enPassant.model.requests.mongoModel.user.DocumentUser;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserUpdateRepository extends MongoRepository<DocumentUser, String> {
    //*UPDATE MATCH LIST*
    @Aggregation(pipeline = {
            "{ $match: { _id: ?0, Type: '1' } }",
            "{ $set: { Matches: ?1 } }",
            "{ $merge: { into: 'user', on: '_id', whenMatched: 'merge', whenNotMatched: 'fail' } }"
    })
    void updateUserMatches(String username, List<UserMatchUpdateModel> matches);

    //*UPDATE STATS*
    @Aggregation(pipeline = {
            "{ $match: { _id: ?0, Type: '1' } }",
            "{ $set: { " +
                    "'NumberOfPlayedMatches': { $size: { $ifNull: ['$Matches', []] } }, " +
                    "'NumberOfVictories': { $size: { $filter: { input: '$Matches', as: 'match', cond: { $eq: ['$$match.Outcome', 'win'] } } } }, " +
                    "'NumberOfDefeats': { $size: { $filter: { input: '$Matches', as: 'match', cond: { $eq: ['$$match.Outcome', 'loss'] } } } }, " +
                    "'NumberOfDraws': { $size: { $filter: { input: '$Matches', as: 'match', cond: { $eq: ['$$match.Outcome', 'draw'] } } } }, " +
                    "'avgMovesNumber': { $avg: { $map: { input: { $ifNull: ['$Matches', []] }, as: 'match', in: { $ifNull: ['$$match.NumberOfMoves', 0] } } } } " +
                    "} }",
            "{ $merge: { into: 'user', on: '_id', whenMatched: 'merge', whenNotMatched: 'fail' } }"
    })
    void updateUserStats(String username);


    //*UPDATE ELO*
    @Aggregation(pipeline = {
            "{ $match: { Type: '1', _id: ?0 } }",
            "{ $set: { ELO: { " +
                    "$max: [ 0, " +
                    "{ $subtract: [ " +
                    "{ $add: [ { $multiply: ['$NumberOfVictories', 80] }, { $multiply: ['$NumberOfDraws', 10] } ] }, " +
                    "{ $multiply: ['$NumberOfDefeats', 50] } " +
                    "] } ] } } }",
            "{ $merge: { into: 'user', on: '_id', whenMatched: 'merge', whenNotMatched: 'fail' } }"
    })
    void updateEloForUser(String username);

}
