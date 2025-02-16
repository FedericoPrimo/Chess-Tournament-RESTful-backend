package it.unipi.enPassant.repositories;

import it.unipi.enPassant.model.requests.mongoModel.tournament.DocumentTournament;
import it.unipi.enPassant.model.requests.mongoModel.tournament.TournamentsAnalyticsModel;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentsAnalyticsRepository extends MongoRepository<DocumentTournament, String> {

    //1: Get the list of players who won matches in a particular category and the average number of moves they needed to win
    @Aggregation(pipeline = {
            "{ $unwind: '$RawMatches' }",
            "{ $match: { 'RawMatches.Winner': { $exists: true, $ne: 'draw' }, 'Edition': ?0, 'Category': ?1, 'RawMatches.Moves': { $exists: true, $not: { $size: 0 } } } }",
            "{ $project: { edition: '$Edition', category: '$Category', winner: '$RawMatches.Winner', moveCount: { $cond: { if: { $isArray: '$RawMatches.Moves' }, then: { $size: '$RawMatches.Moves' }, else: 0 } } } }",
            "{ $group: { _id: { edition: '$edition', category: '$category', winner: '$winner' }, avgMoves: { $avg: '$moveCount' } } }",
            "{ $project:  {userId:  '$_id.winner', category:  '$_id.category', edition: '$_id.edition', howMany: '$avgMoves'}}",
            "{ $sort: { 'edition': 1, 'category': 1, 'userId': 1 } }"
    })
    List<TournamentsAnalyticsModel> calculateAvgMovesPerWinner(int edition, String category);

    //2: Get the list of players who participated in a given edition and category and the number of matches they won
    @Aggregation(pipeline = {
            "{ $unwind: '$RawMatches' }",
            "{ $match: { 'RawMatches.Winner': { $exists: true, $ne: 'draw' }, 'Edition': ?0, 'Category': ?1 } }",
            "{ $project: { edition: '$Edition', winner: '$RawMatches.Winner', category: '$Category'} }",
            "{ $group: { _id: { edition: '$edition', category: '$category', winner: '$winner' }, wonGames: { $sum: 1 } } }",
            "{ $project:  {userId:  '$_id.winner', category:  '$_id.category', edition: '$_id.edition', howMany: '$wonGames'}}",
            "{ $sort: { 'edition': 1, 'category': 1, 'userId': 1 } }"
    })
    List<TournamentsAnalyticsModel> countGamesWonByPlayerPerEdition(int edition, String category);

    //3 Gets the list of all openings and for each of them calculates the percentages of victories, losses and draws they led to
    @Aggregation(pipeline = {
            "{ $unwind: '$RawMatches' }",
            "{ $match: { 'RawMatches.Result': { $in: ['1-0', '0-1', '1/2-1/2'] } } }",
            "{ $group: {_id: '$RawMatches.ECO',"
                    + " totalMatches: { $sum: 1 },"
                    + " whiteWins: { $sum: { $cond: [{ $eq: ['$RawMatches.Result', '1-0'] }, 1, 0] } },"
                    + " draws: { $sum: { $cond: [{ $eq: ['$RawMatches.Result', '1/2-1/2'] }, 1, 0] } },"
                    + " blackWins: { $sum: { $cond: [{ $eq: ['$RawMatches.Result', '0-1'] }, 1, 0] } }"
                    + "} }",
            "{ $project: {"
                    + " eco: '$_id',"
                    + " numberOfMatches: '$totalMatches',"
                    + " whiteWinPercentage: {"
                    + "    $cond: { if: { $gt: ['$totalMatches', 0] },"
                    + "             then: { $multiply: [{ $divide: ['$whiteWins', '$totalMatches'] }, 100] },"
                    + "             else: 0 }"
                    + " },"
                    + " drawPercentage: {"
                    + "    $cond: { if: { $gt: ['$totalMatches', 0] },"
                    + "             then: { $multiply: [{ $divide: ['$draws', '$totalMatches'] }, 100] },"
                    + "             else: 0 }"
                    + " },"
                    + " blackWinPercentage: {"
                    + "    $cond: { if: { $gt: ['$totalMatches', 0] },"
                    + "             then: { $multiply: [{ $divide: ['$blackWins', '$totalMatches'] }, 100] },"
                    + "             else: 0 }"
                    + " }"
                    + "} }",
            "{ $sort: { 'eco': 1 } }"
    })
    List<TournamentsAnalyticsModel> calculateAllOpeningsRates();

    //3 VAR: Given a certain opening calculates the percentages of victory, loss and draw it led to
    @Aggregation(pipeline = {
            "{ $unwind: '$RawMatches' }",
            "{ $match: { 'RawMatches.Result': { $in: ['1-0', '0-1', '1/2-1/2'] }, 'RawMatches.ECO': ?0 } }",
            "{ $group: {"
                    + " _id: '$RawMatches.ECO',"
                    + " totalMatches: { $sum: 1 },"
                    + " whiteWins: { $sum: { $cond: [{ $eq: ['$RawMatches.Result', '1-0'] }, 1, 0] } },"
                    + " draws: { $sum: { $cond: [{ $eq: ['$RawMatches.Result', '1/2-1/2'] }, 1, 0] } },"
                    + " blackWins: { $sum: { $cond: [{ $eq: ['$RawMatches.Result', '0-1'] }, 1, 0] } }"
                    + "} }",
            "{ $project: {"
                    + " eco: '$_id',"
                    + " numberOfMatches: '$totalMatches',"
                    + " whiteWinPercentage: {"
                    + "    $cond: { if: { $gt: ['$totalMatches', 0] },"
                    + "             then: { $multiply: [{ $divide: ['$whiteWins', '$totalMatches'] }, 100] },"
                    + "             else: 0 }"
                    + " },"
                    + " drawPercentage: {"
                    + "    $cond: { if: { $gt: ['$totalMatches', 0] },"
                    + "             then: { $multiply: [{ $divide: ['$draws', '$totalMatches'] }, 100] },"
                    + "             else: 0 }"
                    + " },"
                    + " blackWinPercentage: {"
                    + "    $cond: { if: { $gt: ['$totalMatches', 0] },"
                    + "             then: { $multiply: [{ $divide: ['$blackWins', '$totalMatches'] }, 100] },"
                    + "             else: 0 }"
                    + " }"
                    + "} }",
            "{ $sort: { 'eco': 1 } }"
    })
    TournamentsAnalyticsModel calculateRatesByOpening(String opening);

    //4: Given an edition, it returns for every tournament category the average number of moves matches lasted
    @Aggregation(pipeline = {
            "{ $unwind: '$RawMatches' }",
            "{ $match: { 'Edition': ?0 } }",
            "{ $project: {"
                    + " edition: '$Edition',"
                    + " category: '$Category' ,"
                    + " moveCount: { $size: { $ifNull: ['$RawMatches.Moves', []] } }"
                    + "} }",
            "{ $group: {"
                    + " _id: { edition: '$edition', category: '$category' },"
                    + " averageMoves: { $avg: '$moveCount' }"
                    + "} }",
            "{ $sort: { '_id.edition': 1, '_id.category': 1 } }",
            "{ $project: {"
                    + " edition: '$_id.edition',"
                    + " category: '$_id.category',"
                    + " howMany: '$averageMoves',"
                    + " _id: 0"
                    + "} }"
    })
    List<TournamentsAnalyticsModel> calculateAverageMovesPerTournament(int edition);


    //5: Given a certain edition it returns, for each category, the most frequently used opening and how many times it was used
    @Aggregation(pipeline = {
            "{ $unwind: '$RawMatches' }",
            "{ $match: { 'RawMatches.ECO': { $exists: true, $ne: '' }, 'Edition': ?0 } }",
            "{ $project: {"
                    + " edition: '$Edition',"
                    + " category: '$Category',"
                    + " opening: '$RawMatches.ECO'"
                    + "} }",
            "{ $group: {"
                    + " _id: { edition: '$edition', category: '$category', opening: '$opening' },"
                    + " count: { $sum: 1 }"
                    + "} }",
            "{ $sort: { 'count': -1 } }",
            "{ $group: {"
                    + " _id: { edition: '$_id.edition', category: '$_id.category' },"
                    + " mostFrequentOpening: { $first: '$_id.opening' },"
                    + " maxCount: { $first: '$count' }"
                    + "} }",
            "{ $sort: { '_id.edition': 1, '_id.category': 1 } }",
            "{ $project: {"
                    + " _id: 0,"
                    + " edition: '$_id.edition',"
                    + " category: '$_id.category',"
                    + " eco: '$mostFrequentOpening',"
                    + " howMany: '$maxCount'"
                    + "} }"
    })
    List<TournamentsAnalyticsModel> findMostFrequentOpeningPerTournament(int edition);

    //8: Given a certain edition, for each category it returns the average match duration
    @Aggregation(pipeline = {
            "{ $unwind: '$RawMatches' }",
            "{ $match: {'Edition': ?0}}",
            "{ $project: {"
                    + " edition: '$Edition',"
                    + " category: '$Category',"
                    + " duration: '$RawMatches.Duration'"
                    + "} }",
            "{ $group: {"
                    + " _id: { edition: '$edition', category: '$category' },"
                    + " averageMatchDuration: { $avg: '$duration' }"
                    + "} }",
            "{ $sort: { '_id.edition': 1, '_id.category': 1 } }",
            "{ $project: {"
                    + " _id: 0,"
                    + " edition: '$_id.edition',"
                    + " category: '$_id.category',"
                    + " averageMatchDuration: '$averageMatchDuration'"
                    + "} }"
    })
    List<TournamentsAnalyticsModel> findAverageMatchDuration(int edition);


}
