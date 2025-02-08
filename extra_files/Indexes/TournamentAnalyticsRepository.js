//-----------------------------------------------------------------------QUERY 1-----------------------------------------------------------------------//
db.tournaments.aggregate([{ $unwind: "$RawMatches" }, { $match: { "RawMatches.Winner": { $exists: true, $ne: "draw" }, "Edition": 2004, "Category": "Blitz", "RawMatches.Moves": { $exists: true, $not: { $size: 0 } } } }, { $project: { edition: "$Edition", category: "$Category", winner: "$RawMatches.Winner", moveCount: { $cond: { if: { $isArray: "$RawMatches.Moves" }, then: { $size: "$RawMatches.Moves" }, else: 0 } } } }, { $group: { _id: { edition: "$edition", category: "$category", winner: "$winner" }, avgMoves: { $avg: "$moveCount" } } }, { $project: { userId: "$_id.winner", category: "$_id.category", edition: "$_id.edition", howMany: "$avgMoves" } }, { $sort: { "edition": 1, "category": 1, "userId": 1 } }]).explain("executionStats")

//? index: { "Edition": 1, "Category": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

//index: { "Edition": 1, "Category": 1, "Location": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 2,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

//index: { "Edition": 1, "Category": 1}, {"Location": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//     executionTimeMillis: 2,
//     totalKeysExamined: 1,
//     totalDocsExamined: 1,

//index: { "RawMatches.Winner": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//     executionTimeMillis: 1,
//     totalKeysExamined: 0,
//     totalDocsExamined: 51,

//no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//     executionTimeMillis: 4,
//     totalKeysExamined: 0,
//     totalDocsExamined: 51,


//-----------------------------------------------------------------------QUERY 2-----------------------------------------------------------------------//
db.tournaments.aggregate([{ $unwind: "$RawMatches" }, { $match: { "RawMatches.Winner": { $exists: true, $ne: "draw" }, "Edition": 2004, "Category": "Blitz" } }, { $project: { edition: "$Edition", winner: "$RawMatches.Winner", category: "$Category" } }, { $group: { _id: { edition: "$edition", category: "$category", winner: "$winner" }, wonGames: { $sum: 1 } } }, { $project: { userId: "$_id.winner", category: "$_id.category", edition: "$_id.edition", howMany: "$wonGames" } }, { $sort: { "edition": 1, "category": 1, "userId": 1 } }]).explain("executionStats")

//? index: {"Edition": 1, "Category": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 0,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

//index: {"Edition": 1, "Category": 1, "Location": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

//no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//     executionTimeMillis: 2,
//     totalKeysExamined: 0,
//     totalDocsExamined: 51,

//index: { "RawMatches.Winner": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//     executionTimeMillis: 2,
//     totalKeysExamined: 0,
//     totalDocsExamined: 51,

//-----------------------------------------------------------------------QUERY 3-----------------------------------------------------------------------//
db.tournaments.aggregate([{ $unwind: "$RawMatches" }, { $match: { "RawMatches.Result": { $in: ["1-0", "0-1", "1/2-1/2"] } } }, { $group: { _id: "$RawMatches.ECO", totalMatches: { $sum: 1 }, whiteWins: { $sum: { $cond: [{ $eq: ["$RawMatches.Result", "1-0"] }, 1, 0] } }, draws: { $sum: { $cond: [{ $eq: ["$RawMatches.Result", "1/2-1/2"] }, 1, 0] } }, blackWins: { $sum: { $cond: [{ $eq: ["$RawMatches.Result", "0-1"] }, 1, 0] } } } }, { $project: { eco: "$_id", numberOfMatches: "$totalMatches", whiteWinPercentage: { $cond: { if: { $gt: ["$totalMatches", 0] }, then: { $multiply: [{ $divide: ["$whiteWins", "$totalMatches"] }, 100] }, else: 0 } }, drawPercentage: { $cond: { if: { $gt: ["$totalMatches", 0] }, then: { $multiply: [{ $divide: ["$draws", "$totalMatches"] }, 100] }, else: 0 } }, blackWinPercentage: { $cond: { if: { $gt: ["$totalMatches", 0] }, then: { $multiply: [{ $divide: ["$blackWins", "$totalMatches"] }, 100] }, else: 0 } } } }, { $sort: { "eco": 1 } }]).explain("executionStats")

//? no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//!     executionTimeMillis: 12,
//!     totalKeysExamined: 0,
//!     totalDocsExamined: 51,

//index: {"Edition": 1, "Category": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//!     executionTimeMillis: 16,
//!     totalKeysExamined: 0,
//!     totalDocsExamined: 51,

//index: {"Edition": 1, "Category": 1, "Location": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//!     executionTimeMillis: 16,
//!     totalKeysExamined: 0,
//!     totalDocsExamined: 51,

//index: {"RawMatches.Result": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//!     executionTimeMillis: 16,
//!     totalKeysExamined: 0,
//!     totalDocsExamined: 51,

//-----------------------------------------------------------------------QUERY 3VAR-----------------------------------------------------------------------//
db.tournaments.aggregate([{ $unwind: "$RawMatches" }, { $match: { "RawMatches.Result": { $in: ["1-0", "0-1", "1/2-1/2"] }, "RawMatches.ECO": "C88" } }, { $group: { _id: "$RawMatches.ECO", totalMatches: { $sum: 1 }, whiteWins: { $sum: { $cond: [{ $eq: ["$RawMatches.Result", "1-0"] }, 1, 0] } }, draws: { $sum: { $cond: [{ $eq: ["$RawMatches.Result", "1/2-1/2"] }, 1, 0] } }, blackWins: { $sum: { $cond: [{ $eq: ["$RawMatches.Result", "0-1"] }, 1, 0] } } } }, { $project: { eco: "$_id", numberOfMatches: "$totalMatches", whiteWinPercentage: { $cond: { if: { $gt: ["$totalMatches", 0] }, then: { $multiply: [{ $divide: ["$whiteWins", "$totalMatches"] }, 100] }, else: 0 } }, drawPercentage: { $cond: { if: { $gt: ["$totalMatches", 0] }, then: { $multiply: [{ $divide: ["$draws", "$totalMatches"] }, 100] }, else: 0 } }, blackWinPercentage: { $cond: { if: { $gt: ["$totalMatches", 0] }, then: { $multiply: [{ $divide: ["$blackWins", "$totalMatches"] }, 100] }, else: 0 } } } }, { $sort: { "eco": 1 } }]).explain("executionStats")

//? no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//!     executionTimeMillis: 7,
//!     totalKeysExamined: 0,
//!     totalDocsExamined: 51,

//index: {"Edition": 1, "Category": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//!     executionTimeMillis: 8,
//!     totalKeysExamined: 0,
//!     totalDocsExamined: 51,

//index: {"Edition": 1, "Category": 1, "Location": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//     executionTimeMillis: 10,
//     totalKeysExamined: 0,
//     totalDocsExamined: 51,

//index: {"RawMatches.ECO": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 51,
//     executionTimeMillis: 11,
//     totalKeysExamined: 0,
//     totalDocsExamined: 51,

//-----------------------------------------------------------------------QUERY 4-----------------------------------------------------------------------//
db.tournaments.aggregate([{ $unwind: "$RawMatches" }, { $match: { "Edition": 2004 } }, { $project: { edition: "$Edition", category: "$Category", moveCount: { $size: { $ifNull: ["$RawMatches.Moves", []] } } } }, { $group: { _id: { edition: "$edition", category: "$category" }, averageMoves: { $avg: "$moveCount" } } }, { $sort: { "_id.edition": 1, "_id.category": 1 } }, { $project: { edition: "$_id.edition", category: "$_id.category", howMany: "$averageMoves", _id: 0 } }]).explain("executionStats")

//? index: {"Edition": 1, "Category": 1, "Location": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 3,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 3,
//!     totalDocsExamined: 3,

//index: {"Edition": 1, "Category": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 3,
//!     executionTimeMillis: 2,
//!     totalKeysExamined: 3,
//!     totalDocsExamined: 3,

// no index:
// executionStats: {
//     executionSuccess: true,
//     nReturned: 3,
//     executionTimeMillis: 2,
//     totalKeysExamined: 0,
//     totalDocsExamined: 51,

//-----------------------------------------------------------------------QUERY 5-----------------------------------------------------------------------//
db.tournaments.aggregate([{ $unwind: "$RawMatches" }, { $match: { "RawMatches.ECO": { $exists: true, $ne: "" }, "Edition": 2004 } }, { $project: { edition: "$Edition", category: "$Category", opening: "$RawMatches.ECO" } }, { $group: { _id: { edition: "$edition", category: "$category", opening: "$opening" }, count: { $sum: 1 } } }, { $sort: { "count": -1 } }, { $group: { _id: { edition: "$_id.edition", category: "$_id.category" }, mostFrequentOpening: { $first: "$_id.opening" }, maxCount: { $first: "$count" } } }, { $sort: { "_id.edition": 1, "_id.category": 1 } }, { $project: { _id: 0, edition: "$_id.edition", category: "$_id.category", eco: "$mostFrequentOpening", howMany: "$maxCount" } }]).explain("executionStats")

//? index: {"Edition": 1, "Category": 1, "Location": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 3,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 3,
//!     totalDocsExamined: 3,

//index: {"Edition": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 3,
//!     executionTimeMillis: 2,
//!     totalKeysExamined: 3,
//!     totalDocsExamined: 3,

//index: {"Edition": 1, "Category": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 3,
//!     executionTimeMillis: 3,
//!     totalKeysExamined: 3,
//!     totalDocsExamined: 3,

//no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 3,
//     executionTimeMillis: 3,
//     totalKeysExamined: 0,
//     totalDocsExamined: 51,

//-----------------------------------------------------------------------QUERY 8-----------------------------------------------------------------------//
db.tournaments.aggregate([{ $unwind: "$RawMatches" }, { $match: { "Edition": 2004 } }, { $project: { edition: "$Edition", category: "$Category", duration: "$RawMatches.Duration" } }, { $group: { _id: { edition: "$edition", category: "$category" }, averageMatchDuration: { $avg: "$duration" } } }, { $sort: { "_id.edition": 1, "_id.category": 1 } }, { $project: { _id: 0, edition: "$_id.edition", category: "$_id.category", averageMatchDuration: "$averageMatchDuration" } }]).explain("executionStats")

//? index: {"Edition": 1, "Category": 1, "Location": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 3,
//!     executionTimeMillis: 0,
//!     totalKeysExamined: 3,
//!     totalDocsExamined: 3,

//index: {"RawMatches.Duration": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 3,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 3,
//!     totalDocsExamined: 3,

//index: {"Edition": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 3,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 3,
//!     totalDocsExamined: 3,

//index: {"Edition": 1, "Category": 1}
// executionStats: {
//     executionSuccess: true,
//     nReturned: 3,
//!     executionTimeMillis: 2,
//!     totalKeysExamined: 3,
//!     totalDocsExamined: 3,

//no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 3,
//     executionTimeMillis: 2,
//     totalKeysExamined: 0,
//     totalDocsExamined: 51,

//-----------------------------------------------------------------------CONCLUSIONS-----------------------------------------------------------------------//
