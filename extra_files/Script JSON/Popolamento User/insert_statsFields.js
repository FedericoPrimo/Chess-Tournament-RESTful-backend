//number of matches
db.user.aggregate([
    { $match: { Type: '1' } },
    { $project: { _id: 1, NumberOfPlayedMatches: { $size: "$Matches" } } },
    { $merge: { 
            into: "user", 
            on: "_id", 
            whenMatched: "merge", 
            whenNotMatched: "fail"
        } 
    }
]);

//number of won matches
db.user.aggregate([
    { $unwind: "$Matches" },
    { $match: { "Matches.Winner": "win" } },
    { $group: { _id: "$_id", NumberOfVictories: { $sum: 1 }} }, 
    { $merge: { 
            into: "user", 
            on: "_id", 
            whenMatched: "merge", 
            whenNotMatched: "fail"
        } 
    }
]);

//number of lost matches
db.user.aggregate([
    { $unwind: "$Matches" },
    { $match: { "Matches.Winner": "loss" } },
    { $group: { _id: "$_id", NumberOfDefeats: { $sum: 1 }} },
    { $merge: { 
        into: "user", 
        on: "_id", 
        whenMatched: "merge", 
        whenNotMatched: "fail"
        } 
    }
]);

//number of draws
db.user.aggregate([
    { $unwind: "$Matches" },
    { $match: { "Matches.Winner": "draw" } },
    { $group: { _id: "$_id", NumberOfDraws: { $sum: 1 }} },
    { $merge: { 
        into: "user", 
        on: "_id", 
        whenMatched: "merge", 
        whenNotMatched: "fail"
        } 
    }
]);

//avg number of moves
db.user.aggregate([
    { $unwind: "$Matches" },
    { $group: { _id: "$_id", avgMovesNumber: { $avg: "$Matches.NumberOfMoves" } } },
    { $merge: { 
        into: "user", 
        on: "_id", 
        whenMatched: "merge", 
        whenNotMatched: "fail"
        } 
    }
]);
