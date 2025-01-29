package it.unipi.enPassant.service;

import it.unipi.enPassant.model.requests.MatchListModel;
import it.unipi.enPassant.model.requests.MatchModel;
import it.unipi.enPassant.model.requests.TournamentModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataServiceTournament {
    // @Autowire ai servizi che ci servono per mongo e redis
    public List<TournamentModel> tournamentGetList() {
        return List.of(
                new TournamentModel(25, "ok", "ok"),
                new TournamentModel(25, "ok", "ok")
        );
    }

    public List<MatchListModel> liveMatchGetList() {
        return List.of(
                new MatchListModel("white","black"),
                new MatchListModel("matti","fede")
        );
    }

    public List<MatchListModel> tournamentMatchGetList(String category,int edition,String location) {
        return List.of(
                new MatchListModel("white","black"),
                new MatchListModel("matti","fede")
        );
    }

    public MatchModel tournamentMatchGet(String Black, String White) {
        List<String> moves = List.of("e4", "e5", "Nf3", "Nc6", "Bb5", "a6");
        return new MatchModel(
                2024,            // Data del match
                White,           // Giocatore bianco passato come parametro
                2286,            // Elo bianco (dato fittizio)
                Black,           // Giocatore nero passato come parametro
                2664,            // Elo nero (dato fittizio)
                "C62",           // Codice apertura
                "The 8 Champions", // Nome evento
                "1-0",           // Risultato
                "Blitz",         // Categoria
                moves,           // Lista delle mosse
                White,           // Vincitore (ipotizziamo il bianco)
                "2024-03-22 01:08:28", // Timestamp
                1.45             // Durata del match
        );
    }

    public MatchModel liveMatchGet(String Black,String White) {
        List<String> moves = List.of("e4", "e5", "Nf3", "Nc6", "Bb5", "a6");
        return new MatchModel(
                2024,            // Data del match
                White,           // Giocatore bianco passato come parametro
                2286,            // Elo bianco (dato fittizio)
                Black,           // Giocatore nero passato come parametro
                2664,            // Elo nero (dato fittizio)
                "C62",           // Codice apertura
                "The 8 Champions", // Nome evento
                "1-0",           // Risultato
                "Blitz",         // Categoria
                moves,           // Lista delle mosse
                White,           // Vincitore (ipotizziamo il bianco)
                "2024-03-22 01:08:28", // Timestamp
                1.45             // Durata del match
        );
    }
}
