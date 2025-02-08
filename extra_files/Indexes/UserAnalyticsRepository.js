//-----------------------------------------------------------------------QUERY 1-----------------------------------------------------------------------//
db.user.aggregate([{ $match: { "Type": "1" } }, { $unwind: "$Matches" }, { $group: { _id: { userId: "$_id", opening: "$Matches.Opening" }, count: { $sum: 1 } } }, { $sort: { "_id.userId": 1, "count": -1 } }, { $group: { _id: "$_id.userId", mostFrequentOpening: { $first: "$_id.opening" }, howMany: { $first: "$count" } } }, { $project: { userId: "$_id", mostFrequentOpening: 1, howMany: 1 } }, { $sort: { "userId": 1 } }]).explain("executionStats")    

//? index: { "Type": 1 }
// executionStats: {
//     executionSuccess: true,
//     nReturned: 17,
//!     executionTimeMillis: 14,
//!     totalKeysExamined: 17,
//!     totalDocsExamined: 17,

//no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 17,
//     executionTimeMillis: 18,
//     totalKeysExamined: 0,
//     totalDocsExamined: 71,

//-----------------------------------------------------------------------QUERY 1VAR-----------------------------------------------------------------------//
db.user.aggregate([{ $match: { "_id": "frattacci_jonathan", "Type": "1" } }, { $unwind: "$Matches" }, { $project: { userId: "$_id", opening: "$Matches.Opening" } }, { $group: { _id: { userId: "$userId", opening: "$opening" }, count: { $sum: 1 } } }, { $sort: { count: -1 } }, { $group: { _id: "$_id.userId", mostFrequentOpening: { $first: "$_id.opening" }, howMany: { $first: "$count" } } }, { $project: { userId: "$_id", mostFrequentOpening: 1, howMany: 1 } }, { $sort: { "userId": 1 } }]).explain("executionStats")

//? index: { "Type": 1 }
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 0,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

//index: { "_id": 1, "Type": 1 }
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

// no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//     executionTimeMillis: 2,
//     totalKeysExamined: 1,
//     totalDocsExamined: 1,

//-----------------------------------------------------------------------QUERY 2-----------------------------------------------------------------------//
db.user.aggregate([{ $match: { "Type": "1" } }, { $unwind: "$Matches" }, { $match: { "Matches.Winner": "win" } }, { $project: { userId: "$_id", opening: "$Matches.Opening" } }, { $group: { _id: { userId: "$userId", opening: "$opening" }, count: { $sum: 1 } } }, { $sort: { count: -1 } }, { $group: { _id: "$_id.userId", mostFrequentOpening: { $first: "$_id.opening" }, howMany: { $first: "$count" } } }, { $project: { userId: "$_id", mostFrequentOpening: 1, howMany: 1 } }, { $sort: { "userId": 1 } }]).explain("executionStats")

//? index: { "Type": 1 }
// executionStats: {
//     executionSuccess: true,
//     nReturned: 17,
//!     executionTimeMillis: 12,
//!     totalKeysExamined: 17,
//!     totalDocsExamined: 17,

//no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 17,
//     executionTimeMillis: 11,
//     totalKeysExamined: 0,
//     totalDocsExamined: 71,

//-----------------------------------------------------------------------QUERY 2VAR-----------------------------------------------------------------------//
db.user.aggregate([{ $match: { "Type": "1" } }, { $unwind: "$Matches" }, { $match: { "_id": "frattacci_jonathan", "Matches.Winner": "win" } }, { $project: { userId: "$_id", opening: "$Matches.Opening" } }, { $group: { _id: { userId: "$userId", opening: "$opening" }, count: { $sum: 1 } } }, { $sort: { count: -1 } }, { $group: { _id: "$_id.userId", mostFrequentOpening: { $first: "$_id.opening" }, howMany: { $first: "$count" } } }, { $project: { userId: "$_id", mostFrequentOpening: 1, howMany: 1 } }, { $sort: { "userId": 1 } }]).explain("executionStats")

//? index: { "Type": 1 }
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
//     totalKeysExamined: 1,
//     totalDocsExamined: 1,

//-----------------------------------------------------------------------QUERY 3-----------------------------------------------------------------------//
db.user.aggregate([{ $match: { "Type": "1" } }, { $unwind: "$Matches" }, { $match: { "Matches.Winner": "win" } }, { $group: { _id: { userId: "$_id", opponentId: "$Matches.OpponentId" } } }, { $group: { _id: "$_id.userId", howMany: { $sum: 1 } } }, { $project: { userId: "$_id", howMany: 1 } }, { $sort: { "userId": 1 } }]).explain("executionStats")

//? index: { "Type": 1 }
// executionStats: {
//     executionSuccess: true,
//     nReturned: 17,
//!     executionTimeMillis: 13,
//!     totalKeysExamined: 17,
//!     totalDocsExamined: 17,

//no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 17,
//     executionTimeMillis: 11,
//     totalKeysExamined: 0,
//     totalDocsExamined: 71,

//-----------------------------------------------------------------------QUERY 3VAR-----------------------------------------------------------------------//
db.user.aggregate([{ $match: { "Type": "1" } }, { $unwind: "$Matches" }, { $match: { "_id": "frattacci_jonathan", "Matches.Winner": "win" } }, { $project: { userId: "$_id", opponentId: "$Matches.OpponentId" } }, { $group: { _id: { userId: "$userId", opponentId: "$opponentId" }, totalOpponents: { $sum: 1 } } }, { $group: { _id: "$_id.userId", howMany: { $sum: 1 } } }, { $project: { userId: "$_id", howMany: 1 } }, { $sort: { "userId": 1 } }]).explain("executionStats")

//? no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

// index: { "Type": 1 }
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

//-----------------------------------------------------------------------QUERY 4-----------------------------------------------------------------------//
db.user.aggregate([{ $match: { "Type": "1" } }, { $unwind: "$Matches" }, { $match: { "Matches.Winner": "win" } }, { $group: { _id: "$_id", howMany: { $sum: 1 } } }, { $project: { userId: "$_id", howMany: 1 } }, { $sort: { "userId": 1 } }]).explain("executionStats")

//? index: { "Type": 1 }
// executionStats: {
//     executionSuccess: true,
//     nReturned: 17,
//!     executionTimeMillis: 10,
//!     totalKeysExamined: 17,
//!     totalDocsExamined: 17,

//no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 17,
//     executionTimeMillis: 11,
//     totalKeysExamined: 0,
//     totalDocsExamined: 71,

//-----------------------------------------------------------------------QUERY 4VAR-----------------------------------------------------------------------//
db.user.aggregate([{ $match: { "Type": "1" } }, { $unwind: "$Matches" }, { $match: { "_id": "frattacci_jonathan", "Matches.Winner": "win" } }, { $group: { _id: "$_id", howMany: { $sum: 1 } } }, { $project: { userId: "$_id", howMany: 1 } }, { $sort: { "userId": 1 } }]).explain("executionStats")

//? no index
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

// index: { "Type": 1 }
// executionStats: {
//     executionSuccess: true,
//     nReturned: 1,
//!     executionTimeMillis: 1,
//!     totalKeysExamined: 1,
//!     totalDocsExamined: 1,

//-----------------------------------------------------------------------QUERY 5-----------------------------------------------------------------------//
db.user.aggregate([{ $match: { "status": { $exists: true } } }, { $project: { userId: "$_id" } }]).explain("executionStats")

//? index: { "status": 1 } (sparse)