package it.unipi.enPassant.service;

import it.unipi.enPassant.model.requests.*;
import it.unipi.enPassant.repositories.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataServiceTournament {
    @Autowired
    private TournamentRepository tournamentRepository;
    // @Autowire ai servizi che ci servono per mongo e redis
    public List<TournamentModel> tournamentGetList() {
        List<DocumentTournament> lista = tournamentRepository.findAll();

        return lista.stream()
                .map(doc -> new TournamentModel( doc.getEdition(), doc.getCategory(), doc.getLocation()))
                .collect(Collectors.toList());
    }

    public List<MatchListModel> liveMatchGetList() {
        return List.of(
                new MatchListModel("white","black"),
                new MatchListModel("matti","fede")
        );
    }

    public List<MatchListModel> tournamentMatchGetList(String category,int edition,String location) {
        return tournamentRepository.findTournamentMatches(edition, category, location);
    }

    public DocumentMatch tournamentMatchGet(String category, int edition, String location, String Black, String White) {
        System.out.println(Black);
        System.out.println(White);
        DocumentMatch match = tournamentRepository.findMatchofTournament(edition, category, location, Black, White);
        System.out.println(match);
        return tournamentRepository.findMatchofTournament(edition, category, location, Black, White);
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
