//-----------------------------------------------------------------------QUERY 1-----------------------------------------------------------------------//
db.tournaments.aggregate([{ $match: { "Edition": 2004, "Category": "Blitz", "Location": "Amsterdam" } }, { $unwind: "$RawMatches" }, { $match: { "RawMatches.White": "alekhine_alexander", "RawMatches.Black": "lasker_emanuel" } }, { $project: { "_id": 0, "RawMatches": 1 } }]).explain("executionStats")

//? index: { "Edition": 1, "Category": 1, "Location": 1 } //
// executionStats: {
//   executionSuccess: true,
//   nReturned: 1,
//!   executionTimeMillis: 0,
//!   totalKeysExamined: 1,
//!   totalDocsExamined: 1,

//index: { "Edition": 1, "Category": 1} //
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,


//index: { "Edition": 1, "Category": 1}, {"Location": 1} //
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

//index: { "Edition": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//     executionTimeMillis: 0,
//     totalKeysExamined: 3,
//     totalDocsExamined: 3,

//index: { "Location": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//     executionTimeMillis: 0,
//     totalKeysExamined: 3,
//     totalDocsExamined: 3,

//index: {"Category": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//     executionTimeMillis: 1,
//     totalKeysExamined: 17,
//     totalDocsExamined: 17,

//no index
// executionStats: {
//   executionSuccess: true,
//   nReturned: 1,
//   executionTimeMillis: 0,
//   totalKeysExamined: 0,
//   totalDocsExamined: 51,

//index: { "Edition": 1, "Category": 1, "Location": 1, "RawMatches.White": 1, "RawMatches.Black": 1 } //
// executionStats: {
//   executionSuccess: true,
//   nReturned: 1,
//   executionTimeMillis: 2,
//   totalKeysExamined: 43,
//   totalDocsExamined: 1,


//-----------------------------------------------------------------------QUERY 2-----------------------------------------------------------------------//
db.tournaments.aggregate([{ $unwind: "$RawMatches" }, { $match: { $or: [{ "RawMatches.White": "frattacci_jonathan" }, { "RawMatches.Black": "frattacci_jonathan" }] } }, { $project: { "playerId": "frattacci_jonathan", "color": { $cond: [{ $eq: ["$RawMatches.White", "frattacci_jonathan"] }, "White", "Black"] }, "opponentId": { $cond: [{ $eq: ["$RawMatches.White", "frattacci_jonathan"] }, "$RawMatches.Black", "$RawMatches.White"] }, "moves": { $size: "$RawMatches.Moves" }, "winner": { $cond: [{ $eq: ["$RawMatches.Winner", "frattacci_jonathan"] }, "win", { $cond: [{ $eq: ["$RawMatches.Winner", "draw"] }, "draw", "loss"] }] }, "opening": "$RawMatches.ECO", "ELO": { $cond: [{ $eq: ["$RawMatches.White", "frattacci_jonathan"] }, "$RawMatches.WhiteElo", "$RawMatches.BlackElo"] }, "tournamentEdition": "$Edition", "tournamentCategory": "$Category" } }, { $group: { _id: "frattacci_jonathan", Matches: { $push: "$$ROOT" } } }]).explain("executionStats")

//? no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//!     executionTimeMillis: 9,
//!     totalKeysExamined: 0,
//!     totalDocsExamined: 51,
//     executionStages: {
//       isCached: false,
//       stage: 'PROJECTION_SIMPLE',

//index: { "RawMatches.White": 1, "RawMatches.Black": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//!     executionTimeMillis: 13,
//!     totalKeysExamined: 0,
//!     totalDocsExamined: 51,

//index: { "Edition": 1, "Category": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//!     executionTimeMillis: 14,
//!     totalKeysExamined: 0,
//!     totalDocsExamined: 51,

//index: {"Edition": 1, "Category": 1, "Location": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//!     executionTimeMillis: 14,
//!     totalKeysExamined: 0,
//!     totalDocsExamined: 51,

//index: { "Edition": 1, "Category": 1}, {"Location": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//     executionTimeMillis: 18,
//     totalKeysExamined: 0,
//     totalDocsExamined: 51,



//-----------------------------------------------------------------------CONCLUSION-----------------------------------------------------------------------//
//The best index for the first query is { "Edition": 1, "Category": 1, "Location": 1 } and for the second query is "no index".
//TODO: Should i use the index or not?






