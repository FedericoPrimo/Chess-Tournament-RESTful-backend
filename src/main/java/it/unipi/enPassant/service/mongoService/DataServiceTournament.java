package it.unipi.enPassant.service.mongoService;

import it.unipi.enPassant.model.requests.DocumentTournament;
import it.unipi.enPassant.model.requests.mongoModel.tournament.*;
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

    public List<MatchListModel> tournamentMatchGetList(String category,int edition,String location) {
        return tournamentRepository.findTournamentMatches(edition, category, location);
    }

    public DataTournamentMatchModel tournamentMatchGet(String category, int edition, String location, String Black, String White) {
        DataTournamentMatchModel tournament = tournamentRepository.findMatchofTournament(edition, category, location, Black, White);
        return tournament;
    }
}
