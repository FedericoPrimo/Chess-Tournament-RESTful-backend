/**
 * This aggregation pipeline processes the `tournaments` collection and extracts detailed match data for players who played as "Black". 
 * The pipeline performs the following steps:
 * 
 * 1. **Unwind Matches**: Breaks down the `RawMatches` array into individual documents, so each match can
 *    be processed independently.
 * 
 * 2. **Project Fields**: Creates a new document for each match with the following fields:
 *    - `_id`.
 *    - `playerId`: The ID of the player who played as "Black."
 *    - `color`: Fixed value "Black" to indicate the player's side.
 *    - `opponentId`: The ID of the player who played as "White."
 *    - `moves`: The number of moves in the match (calculated using `$size` on the `Moves` array).
 *    - `winner`: The result of the match for the Black player (`win`, `loss`, or `draw`), determined 
 *      by evaluating the `Winner` field.
 *    - `opening`: The ECO code representing the opening played in the match.
 *    - `ELO`: The Elo rating of the Black player.
 *    - `tournamentEdition`: The edition of the tournament in which the match was played.
 *    - `tournamentCategory`: The category of the tournament (e.g., type, level).
 * 
 * 3. **Output Results**: Saves the transformed match data to a new collection called `black`.
 */

db.tournaments.aggregate([{ 
    $unwind: "$RawMatches" 
}, 
{ 
    $project: { 
        _id: { 
            $function: { 
                body: function() { 
                    return new ObjectId(); 
                }, 
                args: [], 
                lang: "js" 
            } 
        }, 
        playerId: "$RawMatches.Black", 
        color: "Black", 
        opponentId: "$RawMatches.White", 
        moves: { $size: "$RawMatches.Moves" }, 
        winner: {$cond: [{$eq: ["$RawMatches.Winner", "$RawMatches.Black"] },  "win", { $cond: [{ $eq: ["$RawMatches.Winner", "draw"] }, "draw", "loss"] }] }, 
        opening: "$RawMatches.ECO", ELO: "$RawMatches.BlackElo", 
        tournamentEdition: "$Edition", 
        tournamentCategory: "$Category" } 
}, 
{ $out: "black" }]);

/**
 * This aggregation pipeline processes the `tournaments` collection and extracts detailed match data for players who played as "White". 
 * The pipeline performs the following steps:
 * 
 * 1. **Unwind Matches**: Breaks down the `RawMatches` array into individual documents, so each match can
 *    be processed independently.
 * 
 * 2. **Project Fields**: Creates a new document for each match with the following fields:
 *    - `_id`.
 *    - `playerId`: The ID of the player who played as "White."
 *    - `color`: Fixed value "White" to indicate the player's side.
 *    - `opponentId`: The ID of the player who played as "Black."
 *    - `moves`: The number of moves in the match (calculated using `$size` on the `Moves` array).
 *    - `winner`: The result of the match for the White player (`win`, `loss`, or `draw`), determined 
 *      by evaluating the `Winner` field.
 *    - `opening`: The ECO code representing the opening played in the match.
 *    - `ELO`: The Elo rating of the White player.
 *    - `tournamentEdition`: The edition of the tournament in which the match was played.
 *    - `tournamentCategory`: The category of the tournament (e.g., type, level).
 * 
 * 3. **Output Results**: Saves the transformed match data to a new collection called `white`.
 */
db.tournaments.aggregate([{ 
    $unwind: "$RawMatches"
}, 
{ 
    $project: { 
        _id: { 
            $function: { 
                body: function() { 
                    return new ObjectId(); 
                }, 
                args: [], 
                lang: "js" 
            } 
        }, 
        playerId: "$RawMatches.White", 
        color: "White", 
        opponentId: "$RawMatches.Black", 
        moves: { $size: "$RawMatches.Moves" }, 
        winner: { $cond: [{ $eq: ["$RawMatches.Winner", "$RawMatches.White"] }, "win", { $cond: [{ $eq: ["$RawMatches.Winner", "draw"] }, "draw", "loss"] }] }, 
        opening: "$RawMatches.ECO", 
        ELO: "$RawMatches.WhiteElo", 
        tournamentEdition: "$Edition", 
        tournamentCategory: "$Category" 
    } 
}, 
{ $out: "white" }]);

/**
 * This aggregation pipeline combines data from the `white` and `black` collections into a single collection called `combined`. 
 * The pipeline performs the following steps:
 * 
 * 1. **Union Data**: 
 *    - Uses the `$unionWith` stage to merge all documents from the `black` collection into the aggregation pipeline.
 *    - This effectively combines match data where players played as "White" (from `white`) and as "Black" (from `black`).
 * 
 * 2. **Output Combined Data**:
 *    - The `$out` stage writes the resulting combined dataset into a new or existing collection called `combined`.
 */

db.white.aggregate([{ $unionWith: "black" }, { $out: "combined" } ]);

/**
 * This aggregation pipeline processes the `combined` collection to group match data by individual players, creating a structured dataset that consolidates all matches played by each player. 
 * The pipeline performs the following steps:
 * 
 * 1. **Group Matches by Player**:
 *    - The `$group` stage groups all matches by the `playerId` field, representing a unique player.
 *    - The following fields are included in the grouped output:
 *      - `_id`: The `playerId`, which uniquely identifies the player.
 *      - `ELO`: The player's most recent ELO rating (determined using `$last`).
 *      - `Matches`: An array containing details about all matches played by the player, with the following fields:
 *        - `Color`: Indicates whether the player played as "White" or "Black."
 *        - `NumberOfMoves`: The total number of moves in the match.
 *        - `Winner`: The result of the match for the player (`win`, `loss`, or `draw`).
 *        - `Opening`: The ECO code representing the opening played in the match.
 *        - `OpponentId`: The ID of the player's opponent.
 *        - `TournamentEdition`: The edition of the tournament where the match occurred.
 *        - `TournamentCategory`: The category/type of the tournament.
 * 
 * 2. **Output Consolidated Player Data**:
 *    - The `$out` stage writes the aggregated and grouped data to a new collection called `user`.
 */
db.combined.aggregate([{ 
    $group: { 
        _id: "$playerId", 
        ELO: { $last: "$ELO" }, 
        Matches: { 
            $push: {
                Color:"$color", 
                NumberOfMoves: "$moves", 
                Outcome: "$winner", 
                Opening: "$opening", 
                OpponentId: "$opponentId", 
                TournamentEdition: "$tournamentEdition", 
                TournamentCategory: "$tournamentCategory" 
            } 
        } 
    } 
}, 
{ $out: "user" }]);


/**
 * This script updates the `user` collection in the database by iterating through a predefined list of users
 * and setting additional fields for each user. The updates include splitting the user's `_id` into a first
 * name and surname, assigning a predefined birthdate from the given list, and setting default values for
 * user type and password.
 *
 * Steps:
 * 1. **Define Users**: The script starts with a list of predefined users, each identified by an `_id` in
 *    the format `<surname>_<name>`, along with a specific `BirthDate`.
 *
 * 2. **Process Each User**:
 *    - Extract the surname and name by splitting the `_id` on the underscore `_`.
 *    - Retrieve the predefined birthdate from the list instead of generating a random one.
 *
 * 3. **Update the User Document**:
 *    - Adds or updates the following fields in the `user` collection:
 *      - `BirthDate`: Assigned from the predefined list.
 *      - `Surname`: Extracted surname from `_id`.
 *      - `Name`: Extracted first name from `_id`.
 *      - `Type`: Defaults to "1".
 *      - `Password`: Defaults to the value of the user's `_id`.
 */

const users = [
    { _id: 'smyslov_vassily', BirthDate: '2000-07-28' },
    { _id: 'kasparov_gary', BirthDate: '1945-03-11' },
    { _id: 'frattacci_jonathan', BirthDate: '1987-12-03' },
    { _id: 'anand_viswanathan', BirthDate: '1990-06-24' },
    { _id: 'lasker_emanuel', BirthDate: '1992-11-19' },
    { _id: 'alekhine_alexander', BirthDate: '1976-03-17' },
    { _id: 'botvinnik_mikhail', BirthDate: '1994-10-15' },
    { _id: 'lucky_luciano', BirthDate: '1943-02-04' },
    { _id: 'karpov_anatoly', BirthDate: '1987-11-26' },
    { _id: 'kortschnoj_viktor', BirthDate: '1979-07-31' },
    { _id: 'carlsen_magnus', BirthDate: '1996-05-17' },
    { _id: 'gelfand_boris', BirthDate: '1950-10-03' },
    { _id: 'calzolari_federico', BirthDate: '2000-08-13' },
    { _id: 'topalov_veselin', BirthDate: '1965-12-17' },
    { _id: 'segreto_mattia', BirthDate: '1946-02-13' },
    { _id: 'steinitz_william', BirthDate: '1965-10-23' },
    { _id: 'euwe_max', BirthDate: '1979-05-12' }
];

// Iterate over the user list and update documents
users.forEach(user => {
    const parts = user._id.split('_');
    const surname = parts[0];
    const name = parts[1];
    
    // Update the user document in the database
    db.user.updateOne(
        { _id: user._id }, 
        {
            $set: {
                BirthDate: user.BirthDate, 
                Surname: surname,          
                Name: name,                
                Type: '1',                 
                Password: user._id         
            }
        }
    );
});






// Clear temporary collections
db.black.drop();
db.white.drop();
db.combined.drop();