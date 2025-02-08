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
    { _id: 'smyslov_vassily', BirthDate: '2000-07-28', Password:'$2a$10$6lfytGWURMcnUw.oS8ZZDOAd4iV5H3XHPwTiv8K6t4UuihIyI8dey' },
    { _id: 'kasparov_gary', BirthDate: '1945-03-11', Password:'$2a$10$VrCFrHzbTfBomry0jGhdquwrm0y5zNEkqbrHJ8EsFvWT0.C8dLMx6' },
    { _id: 'frattacci_jonathan', BirthDate: '1987-12-03', Password:'$2a$10$6OdY/TeQbAOXc01EeKlnxenQR4xPqRlNwb70fjAz1RWtPxrEqJQgm' },
    { _id: 'anand_viswanathan', BirthDate: '1990-06-24', Password:'$2a$10$868iA6jaMuJ622Zt.VTxDeYEKWtTvQq6hMIEEmy9GtAjUknyhJdsK' },
    { _id: 'lasker_emanuel', BirthDate: '1992-11-19', Password:'$2a$10$je7jeOBZlQ1UfbXKoHBGv.DoILsx7oTP5ruEy8mrfNNlKtSPCCPHe' },
    { _id: 'alekhine_alexander', BirthDate: '1976-03-17', Password:'$2a$10$Je/99MwCc8oE08xwdd45SuBvrgJiaK2IDx8zzlfSZ8N0K/F9J/M6e' },
    { _id: 'botvinnik_mikhail', BirthDate: '1994-10-15', Password:'$2a$10$5PGofe03.iyIV2ON9GMRs./P0WWYETu7otkblnFDdHbSKYKJMNEpe' },
    { _id: 'lucky_luciano', BirthDate: '1943-02-04', Password:'$2a$10$4bohAccDUO66mbxUCvEtg.IE03WGa9C9lOb.dxSfuhtlMO/XeJiua' },
    { _id: 'karpov_anatoly', BirthDate: '1987-11-26', Password:'$2a$10$On8yjqek1hwbUgVud5npZOav5IjrFfbhlrs57n8qFAyzxUAdxAbai' },
    { _id: 'kortschnoj_viktor', BirthDate: '1979-07-31', Password:'$2a$10$DrIXIpzoDMC2PM4Etzttw.M/SVBPP23HKqDBWe4qPr0NwkBgLU7Am' },
    { _id: 'carlsen_magnus', BirthDate: '1996-05-17', Password:'$2a$10$Jr.cxA/sqV8beTUV7sHede5YjTZhwhJoKsPDl8YOW7DK8c6YWsHjG' },
    { _id: 'gelfand_boris', BirthDate: '1950-10-03', Password:'$2a$10$SMg31AW6sqmwHsv5T81zqO4UAwN35pLW4i8ynCKJ2ZvYV7hemQCr2' },
    { _id: 'calzolari_federico', BirthDate: '2000-08-13', Password:'$2a$10$rB6vINUMVkAP213GDIBQRu5oojvTpW9fXaFwYhRjJVi/vCvqcc5Gy' },
    { _id: 'topalov_veselin', BirthDate: '1965-12-17', Password:'$2a$10$N6tkLd/3HcKgDCT77IW.y.eBOQAL9t8Y8cO2XDLxvIkJZhDlAVWD.' },
    { _id: 'segreto_mattia', BirthDate: '1946-02-13', Password:'$2a$10$1ppRHn1Q4BTEwkjVp7tVb..SLdYQVP/rTSPhf1EVWt5YW4mntWNKO'},
    { _id: 'steinitz_william', BirthDate: '1965-10-23', Password:'$2a$10$TGduMgY0qk.FFriAksQBiu0No/HWJi1qiAn7KF/INDLMa.w4bgoPq' },
    { _id: 'euwe_max', BirthDate: '1979-05-12', Password:'$2a$10$8HLLewIFIX/1xaMICnSl9.9T0qhxh.OZSP3YqvgnTKqksCZkvtBxq' }
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
                Password: user.Password         
            }
        }
    );
});






// Clear temporary collections
db.black.drop();
db.white.drop();
db.combined.drop();