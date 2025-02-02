//SEZIONE INDEX
//COMMENTO GENERALE INDICI: sono stati pensati per accedere direttamente ai giocatori nel campo index user. In generale si sono salvati solo i campi utili alle analytics.
// 1. INDEX USER
db.user.createIndex({
    "_id": 1,            
    "Matches": 1       
  }, { name: "userIdMatchesIndex" });

// 2. INDEX TOURNAMENTS
db.tournaments.createIndex({
    "Edition": 1,            
    "Category": 1,
    "RawMatches": 1,               
  }, { name: "queryUno" });

// SEZIONE QUERY & ANALYTICS
// SOTTOSEZIONE USER
// 1. Query per trovare l'opening più frequente per ogni giocatore
// SPIEGAZIONE: si fa unwind dei matches per avere ogni match separato e poterci lavorare meglio, 
// raggruppo in base ad utenti e opening cosi ho per ogni opening effettuata da ognni utente quante volte è stata fatta; 
// il risultato viene ordinato in ordine decrescente; raggruppando sul id del giocatore e scegliendo la prima voce per quanto riguarda l'apertura 
// troviamo infine la opening più usata per ogni giocatore visto che era stato precedentemente ordinato.
  db.user.aggregate([
    //{ $match: {"_id": 'frattacci_jonathan'}},
    { $unwind: "$Matches" },
    { $project: { userId: "$_id", opening: "$Matches.Opening" } },
    { $group: { _id: { userId: "$userId", opening: "$opening" }, count: { $sum: 1 } } },
    { $sort: { count: -1 } }, 
    { $group: { _id: "$_id.userId", openingPiuFrequente: { $first: "$_id.opening" }, maxCount: { $first: "$count" } } },
    { $sort: { "_id": 1 } } 
  ]);
  
//2. Variante Query 1 in cui si fa una distinzione ulteriore in base all' esito.
// SPIEGAZIONE: si aggiunge alle varie group l'esito che può assumere i valori:  win draw loss
 db.user.aggregate([
    { $unwind: "$Matches" },
    { $project: {userId: "$_id", opening: "$Matches.Opening", winner: "$Matches.Winner"}},
    { $group: {_id: { userId: "$userId", opening: "$opening",  result: "$Matches.Winner"}, count: { $sum: 1 } } },
    { $sort: { count: -1 } },
    { $group: { _id: {userId: "$_id.userId",result: "$_id.result"}, openingPiuFrequente: { $first: "$_id.opening" }, maxCount: { $first: "$count" } } },
    { $sort: { "_id": 1 } }
    ]);

//3. Query per calcolare per ogni giocatore il numero di avversari distinti contro cui ha vinto perso e pareggiato
// SPIEGAZIONE: dopo la solita unwind e project quello che è stato fatto è un raggruppaamento in base alla coppia id-avversario e esito,
//  si ottiente cosi una terna che viene ulteriormente raggruppata in  base ad id e risultato cosi da poter contare il numero di avversari 
// distinti che sono stati affrontati per ogni esito.
db.user.aggregate([
    { $unwind: "$Matches" },
    { $project: {userId: "$_id", opponentId: "$Matches.OpponentId", result: "$Matches.Winner"}},
    { $group: { _id: { userId: "$userId", result: "$result", opponentId: "$opponentId"}, totalOpponents: { $sum: 1 } } },
    { $group: { _id: { userId: "$_id.userId", result: "$_id.result"}, total: { $sum: 1 } } },
    { $sort: { "_id": 1 }}
    ]);
  
//4. Query per calcolare il numero di partite vinte, perse e pareggiate da ogni giocatore
// SPIEGAZIONE:  dopo la solita unwind e project quello che è stato fatto è un raggruppaamento in base alla coppia id-avversario e esito e conteggiato .
db.user.aggregate([
    { $unwind: "$Matches" },
    { $project: {userId: "$_id",winner: "$Matches.Winner"}},
    { $group: {_id: { userId: "$userId", result: "$winner"}, count: { $sum: 1 } } },
    { $sort: { "_id": 1 }}
    ]);

//5. Query per ottenere la lista dei giocatori squalificati
// SPIEGAZIONE : è una sempice find -> non può usare l'indice perchè state non c'è!!!
db.user.find(
    { state: 1}, 
    { id: 1, Name: 1, Surname: 1,
    });

// SOTTOSEZIONE TOURNAMENTS
// 1. Query per calcolare la media del numero di mosse per ogni giocatore nelle partite vinte, diviso per edizione
// SPIEGAZIONE: per ogni documento si fa l'unwind e poi viene fatto un raggruppamento sul vincitore categoria e 
// edizione cosi da ottenere un record per ogni giocatore che ha partecipato ad una specifica edizione di una 
// specifica categoria per ogniuno di questi record viene computata la media di quante mosse ha impiegato per vincere
    db.tournaments.aggregate([
        { $unwind: "$RawMatches" },
        { $match: { "RawMatches.Winner": { $exists: true, $ne: "draw"}, "Edition": { $exists: true }  } },//aggiunta questa riga è da testare
        { $project: { edizione: "$Edition", category: "$Category", vincitore: "$RawMatches.Winner", numeroMosse: { $size: "$RawMatches.Moves" } } },
        { $group: { _id: { edizione: "$edizione",category:"$category", vincitore: "$vincitore" }, mediaMosse: { $avg: "$numeroMosse" }  } },
        { $sort: { "_id.edizione": 1,  "_id.category":1, "_id.vincitore": 1 } }
    ]);
  
  // 2. Query per calcolare il numero di partite vinte per ogni giocatore in ogni edizione
  // SPIEGAZIONE: il procedimento è analogo alla query 1 ma si conta quante partite sono state vinte
  db.tournaments.aggregate([
    { $unwind: "$RawMatches" },
    { $match: { "RawMatches.Winner": { $exists: true, $ne: "draw"}, "Edition": { $exists: true }  } },
    { $project: { edizione: "$Edition",  vincitore: "$RawMatches.Winner" } },
    { $group: {  _id: { edizione: "$edizione", vincitore: "$vincitore" }, partiteVinte: { $sum: 1 } } },
    { $sort: { "_id.edizione": 1, "_id.vincitore": 1 } }
  ]);
  
  // 3. Query per trovare per ogni opening la percentuale di vittorie del bianco, del pareggio e del nero
  // SPIEGAZIONE: dopo aver fatto unwind , si procede a raggruppare per ECO e creare gli attributi totalMatches,  whiteWins, draws, blackWins 
  // creati con delle contatorie con condizione. questi attributi vengono poi manipolati dividendo per  totalMatches e moltiplicando per 100 cosi da ottenere delle percentuali. 
  // Come ultima cosa si ordina in base al nome dell'ECO.
  db.tournaments.aggregate([
    { $unwind: "$RawMatches" },
    { $group: {
        _id: "$RawMatches.ECO", totalMatches: { $sum: 1 }, 
        whiteWins: {  $sum: { $cond: [{ $eq: ["$RawMatches.Result", "1-0"] }, 1, 0] } }, 
        draws:  { $sum: { $cond: [{ $eq: ["$RawMatches.Result", "1/2-1/2"] }, 1, 0] } },
        blackWins: { $sum: { $cond: [{ $eq: ["$RawMatches.Result", "0-1"] }, 1, 0]  } } } },
   
    { $project: { _id: 1,  totalMatches: 1,
        whiteWinPercentage: { $multiply: [{ $divide: ["$whiteWins", "$totalMatches"] }, 100] },
        drawPercentage: { $multiply: [{ $divide: ["$draws", "$totalMatches"] }, 100] },
        blackWinPercentage: { $multiply: [{ $divide: ["$blackWins", "$totalMatches"] }, 100] } } },
    { $sort: { _id: 1 } }
  ]);
  
 // 4. Query per calcolare il numero medio di mosse per ogni torneo (identificato da edizione e categoria)
 // SPIEGAZIONE: uguale alla query uno ma non si tiene conto dei giocatori o degli esiti quindi si raggruppa solo per edizione e categoria e si computa la media delle mosse
  db.tournaments.aggregate([
    { $unwind: "$RawMatches" },
    { $project: { edition: "$Edition", category: "$Category", numeroMosse: { $size: "$RawMatches.Moves" } } },
    { $group: { _id: { edition: "$edition", category: "$category" }, mediaMosse:{ $avg: "$numeroMosse" } } },
    { $sort: { "_id.edition": 1, "_id.category": 1 } }
  ]);

// 5. Query per trovare l'opening più frequente per ogni torneo (identificato da edizione e categoria)
// SPIEGAZIONE: viene fatta una unwind e poi un raggruppamento su eduizione categoria e opening contanto il numero di istanze, dopo si ordina il risultato in 
// base al valore di count decrescente e infine  si arriva al risultato raggruppando su categoria e edizione e prendondo il primo valore sia per opening che 
// per count che quindi essendo ordinati corrispondono alla opening più frequente e quante volte è stata impiegata.
  db.tournaments.aggregate([
    { $unwind: "$RawMatches" },
    { $project: {  edition: "$Edition", category: "$Category", opening: "$RawMatches.ECO" } },
    { $group: { _id: { edition: "$edition", category: "$category", opening: "$opening" }, count: { $sum: 1 } } },
    { $sort:  {"count":-1}},
    { $group: {_id: { edition: "$_id.edition", category: "$_id.category" }, mostFrequentOpening: { $first: "$_id.opening" },  maxCount: { $first: "$count" } } },
    { $sort: { "_id.edition": 1, "_id.category": 1 } }
  ]);
  
  // 6. Per ogni torneo trova la lista delle partite
  // SPIEGAZIONE: dopo aver fatto unwind per ogni partita viene creato nella project un campo playerIds dove viene scritto chi giocava. 
  // Dopo si raggruppa per edizione e categoria e si crea un set che contenga tutte le partite così facendo è possibile avere 
  // la lista di tutti i match disputati divisi per edizione e categoria
  db.tournaments.aggregate([
    { $unwind: "$RawMatches" },
    { $project: { edition: "$Edition", category: "$Category", playerIds: ["$RawMatches.White", "$RawMatches.Black"]}},
    { $group: { _id: { edition: "$edition", category: "$category" }, participants: { $addToSet: "$playerIds" }  } },
    { $sort: { "_id.edition": 1, "_id.category": 1 } }
  ]);

  // 7. Per ogni torneo ritrova le statistiche
  //SPIEGAZIONE: da testare
  db.tournaments.aggregate([
  { $project: { edition: "$Edition", category: "$Category", statistics: "$Statistiche"  }  },
  { $sort: { "edition": 1, "category": 1 } }
  ]);

  // 8. Per ogni torneo restituisci la durata media di una partita
  //SPIEGAZIONE: dopo aver fatto unwind si procede a fare un raggruppamento in base ad edizione e categoria e si fa una media della durata.
  db.tournaments.aggregate([
  { $unwind: "$RawMatches" },
  { $project: { edition: "$Edition", category: "$Category", duration: "$RawMatches.Duration" } },
  { $group: { _id: { edition: "$edition", category: "$category" }, averageMatchDuration: { $avg: "$duration" } } },
  { $sort: { "_id.edition": 1, "_id.category": 1 } }
  ]);