// 1: Trova il vincitore per ogni edizione e categoria
var winners = db.tournaments.aggregate([
  { $unwind: "$RawMatches" },
  {
    $project: {
      edition: "$Edition",
      category: "$Category",
      winner: {
        $cond: [
          { $eq: ["$RawMatches.Result", "1-0"] }, "$RawMatches.White",
          { $cond: [
            { $eq: ["$RawMatches.Result", "0-1"] }, "$RawMatches.Black",
            null
          ]}
        ]
      }
    }
  },
  { $match: { winner: { $ne: null } } },
  {
    $group: {
      _id: { edition: "$edition", category: "$category", winner: "$winner" },
      wins: { $sum: 1 }
    }
  },
  { $sort: { "_id.edition": 1, "_id.category": 1, wins: -1 } },
  {
    $group: {
      _id: { edition: "$_id.edition", category: "$_id.category" },
      top_winner: { $first: "$_id.winner" }
    }
  }
]).toArray();  // Converte in array per l'iterazione

// Step 2: Itera sui risultati ed aggiorna il campo "Winner" del torneo
winners.forEach(function(winnerData) {
  db.tournaments.updateMany(
    { Edition: winnerData._id.edition, Category: winnerData._id.category },
    { $set: { Winner: winnerData.top_winner } }
  );
});
