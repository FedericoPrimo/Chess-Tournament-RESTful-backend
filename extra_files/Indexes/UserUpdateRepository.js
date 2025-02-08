//-----------------------------------------------------------------------QUERY 1-----------------------------------------------------------------------//
db.user.aggregate([{ $match: { "_id": "frattacci_jonathan", "Type": "1" } }, { $set: { "Matches": "<MATCHES_VALUE>" } }, { $merge: { into: "user", on: "_id", whenMatched: "merge", whenNotMatched: "fail" } }]).explain("executionStats")

//no index

//? index: { "Type": 1 }

//-----------------------------------------------------------------------QUERY 2-----------------------------------------------------------------------//
db.user.aggregate([{ $match: { "_id": "frattacci_jonathan", "Type": "1" } }, { $set: { "NumberOfPlayedMatches": { $size: { $ifNull: ["$Matches", []] } }, "NumberOfVictories": { $size: { $filter: { input: "$Matches", as: "match", cond: { $eq: ["$$match.Outcome", "win"] } } } }, "NumberOfDefeats": { $size: { $filter: { input: "$Matches", as: "match", cond: { $eq: ["$$match.Outcome", "loss"] } } } }, "NumberOfDraws": { $size: { $filter: { input: "$Matches", as: "match", cond: { $eq: ["$$match.Outcome", "draw"] } } } }, "avgMovesNumber": { $avg: { $map: { input: "$Matches", as: "match", in: { $ifNull: ["$$match.NumberOfMoves", 0] } } } } } }, { $merge: { into: "user", on: "_id", whenMatched: "merge", whenNotMatched: "fail" } }]).explain("executionStats")

//? no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 0,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

// index: { "Type": 1 }
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 0,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

//-----------------------------------------------------------------------QUERY 3-----------------------------------------------------------------------//
db.user.aggregate([{ $match: { "Type": "1", "_id": "frattacci_jonathan" } }, { $unwind: { path: "$Matches", preserveNullAndEmptyArrays: true } }, { $group: { _id: "$_id", NumberOfVictories: { $sum: { $cond: [{ $eq: ["$Matches.Outcome", "win"] }, 1, 0] } }, NumberOfDefeats: { $sum: { $cond: [{ $eq: ["$Matches.Outcome", "loss"] }, 1, 0] } }, NumberOfDraws: { $sum: { $cond: [{ $eq: ["$Matches.Outcome", "draw"] }, 1, 0] } } } }, { $set: { ELO: { $max: [0, { $subtract: [{ $add: [{ $multiply: ["$NumberOfVictories", 80] }, { $multiply: ["$NumberOfDraws", 10] }] }, { $multiply: ["$NumberOfDefeats", 50] }] }] } } }, { $merge: { into: "user", on: "_id", whenMatched: "merge", whenNotMatched: "fail" } }]).explain("executionStats")

//? index: { "Type": 1 }
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 0,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

// no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,