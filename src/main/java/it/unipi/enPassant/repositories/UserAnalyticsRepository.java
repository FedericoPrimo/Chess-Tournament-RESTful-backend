package it.unipi.enPassant.repositories;

import it.unipi.enPassant.model.requests.DocumentUser;
import it.unipi.enPassant.model.requests.UserAnalyticNumberModel;
import it.unipi.enPassant.model.requests.UserAnalyticOpeningModel;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAnalyticsRepository extends MongoRepository<DocumentUser, String> {
    //1: List player - mostFrequentOpening
    @Aggregation(pipeline = {
            "{ $match: { 'Type': '1' } }",
            "{ $unwind: '$Matches' }",
            "{ $group: { _id: { userId: '$_id', opening: '$Matches.Opening' }, count: { $sum: 1 } } }",
            "{ $sort: { '_id.userId': 1, 'count': -1 } }",
            "{ $group: { _id: '$_id.userId', mostFrequentOpening: { $first: '$_id.opening' }, howMany: { $first: '$count' } } }",
            "{ $project: { userId: '$_id', mostFrequentOpening: 1, howMany: 1 } }",
            "{ $sort: { 'userId': 1 } }"
    })
    List<UserAnalyticOpeningModel> findAllMostFrequentOpenings();

    //1 VAR: Finds the mostFrequentOpening for a specific player
    @Aggregation(pipeline = {
            "{ $match: {'_id': ?0 ,'Type': '1'}}",
            "{ $unwind: '$Matches'}",
            "{ $project: { userId: '$_id', opening: '$Matches.Opening' } }",
            "{ $group: { _id: { userId: '$userId', opening: '$opening' }, count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }",
            "{ $group: { _id: '$_id.userId', mostFrequentOpening: { $first: '$_id.opening' }, howMany: { $first: '$count' } } }",
            "{ $project: { userId: '$_id', mostFrequentOpening: 1, howMany: 1 } }",
            "{ $sort: { 'userId': 1 } }"
    })
    UserAnalyticOpeningModel findPlayerMostFrequentOpening(String username);

    //2: List player - mostFrequentOpening in matches they won
    @Aggregation(pipeline = {
            "{ $match: { 'Type': '1' } }",
            "{ $unwind: '$Matches' }",
            "{ $match: { 'Matches.Winner': ?0 } }",
            "{ $project: { userId: '$_id', opening: '$Matches.Opening'} }",
            "{ $group: { _id: { userId: '$userId', opening: '$opening' }, count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }",
            "{ $group: { _id: '$_id.userId', mostFrequentOpening: { $first: '$_id.opening' }, howMany: { $first: '$count' } } }",
            "{ $project: { userId: '$_id', mostFrequentOpening: 1, howMany: 1 } }",
            "{ $sort: { 'userId': 1 } }"
    })
    List<UserAnalyticOpeningModel> findAllMostFrequentWinningOpening(String outcome);

    //2 VAR: Finds the mostFrequentOpening for a specific player in matches they won/lost/draw
    @Aggregation(pipeline = {
            "{ $match: { 'Type': '1' } }",
            "{ $unwind: '$Matches' }",
            "{ $match: { '_id': ?0, 'Matches.Winner': ?1 } }",
            "{ $project: { userId: '$_id', opening: '$Matches.Opening' } }",
            "{ $group: { _id: { userId: '$userId', opening: '$opening' }, count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }",
            "{ $group: { _id: '$_id.userId', mostFrequentOpening: { $first: '$_id.opening' }, howMany: { $first: '$count' } } }",
            "{ $project: { userId: '$_id', mostFrequentOpening: 1, howMany: 1 } }",
            "{ $sort: { 'userId': 1 } }"
    })
    UserAnalyticOpeningModel findPlayerMostFrequentWinningOpening(String username, String outcome);


    //3: List player-number of defeated opponets
    @Aggregation(pipeline = {
            "{ $match: { 'Type': '1' } }",
            "{ $unwind: '$Matches' }",
            "{ $match: { 'Matches.Winner': 'win' } }",
            "{ $group: { _id: { userId: '$_id', opponentId: '$Matches.OpponentId' } } }",
            "{ $group: { _id: '$_id.userId', howMany: { $sum: 1 } } }",
            "{ $project: { userId: '$_id', howMany: 1 } }",
            "{ $sort: { 'userId': 1 } }"
    })
    List<UserAnalyticNumberModel> numberOfDefeatedOpponents();

    //3 VAR: Finds the number of opponents defeated by a certain player
    @Aggregation(pipeline = {
            "{ $match: { 'Type': '1' } }",
            "{ $unwind: '$Matches' }",
            "{ $match: { '_id': ?0, 'Matches.Winner': 'win' } }",
            "{ $project: { userId: '$_id', opponentId: '$Matches.OpponentId'}}",
            "{ $group: { _id: { userId: '$userId', opponentId: '$opponentId'}, totalOpponents: { $sum: 1 } } }",
            "{ $group: { _id: '$_id.userId', howMany: { $sum: 1 } } }",
            "{ $project: { userId: '$_id', howMany: 1 } }",
            "{ $sort: { 'userId': 1 } }"
    })
    UserAnalyticNumberModel playerNumberOfDefeatedOpponents(String username);


    //4: List player-number of won matches
    @Aggregation(pipeline = {
            "{ $match: { 'Type': '1' } }",
            "{ $unwind: '$Matches' }",
            "{ $match: { 'Matches.Winner': ?0 } }",
            "{ $group: { _id: '$_id', howMany: { $sum: 1 } } }",
            "{ $project: { userId: '$_id', howMany: 1 } }",
            "{ $sort: { 'userId': 1 } }"
    })
    List<UserAnalyticNumberModel> numberOfWonMatches(String outcome);

    //4 VAR: Find a certain player's number of won/lost/draw matches
    @Aggregation(pipeline = {
            "{ $match: { 'Type': '1' } }",
            "{ $unwind: '$Matches' }",
            "{ $match: { '_id': ?1, 'Matches.Winner': ?0 } }",
            "{ $group: { _id: '$_id', howMany: { $sum: 1 } } }",
            "{ $project: { userId: '$_id', howMany: 1 } }",
            "{ $sort: { 'userId': 1 } }"
    })
    UserAnalyticNumberModel playerNumberOfWonMatches(String outcome, String username);


    //5: List of disqualified players
    @Aggregation(pipeline = {
            "{ $match: { status: { $exists: true } } }",
            "{ $project: { userId: '$_id' } }"
    })
    List<String> findDisqualified();
}
