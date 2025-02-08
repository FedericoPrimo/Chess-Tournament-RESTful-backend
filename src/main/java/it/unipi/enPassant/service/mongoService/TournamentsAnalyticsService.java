package it.unipi.enPassant.service.mongoService;

import it.unipi.enPassant.model.requests.mongoModel.tournament.TournamentsAnalyticsModel;
import it.unipi.enPassant.repositories.TournamentsAnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentsAnalyticsService {
    private TournamentsAnalyticsRepository tournamentsAnalyticsRepository;

    public TournamentsAnalyticsService(TournamentsAnalyticsRepository tournamentsAnalyticsRepository) {
        this.tournamentsAnalyticsRepository = tournamentsAnalyticsRepository;
    }

    //1
    public List<TournamentsAnalyticsModel> AVGmovesPerWinner(int edition, String category){
        return tournamentsAnalyticsRepository.calculateAvgMovesPerWinner(edition, category);
    }

    //2
    public List<TournamentsAnalyticsModel> wonMatchesPerPlayer(int edition, String category){
        return tournamentsAnalyticsRepository.countGamesWonByPlayerPerEdition(edition, category);
    }

    //3
    public List<TournamentsAnalyticsModel> allOpeningsPercentages(){
        return tournamentsAnalyticsRepository.calculateAllOpeningsRates();
    }

    //3 VAR
    public TournamentsAnalyticsModel openingsPercentages(String opening){
        return tournamentsAnalyticsRepository.calculateRatesByOpening(opening);
    }

    //4
    public List<TournamentsAnalyticsModel> AVGmovesPerTournament(int edition){
        return tournamentsAnalyticsRepository.calculateAverageMovesPerTournament(edition);
    }

    //5
    public List<TournamentsAnalyticsModel> tournamentMFO(int edition){
        return tournamentsAnalyticsRepository.findMostFrequentOpeningPerTournament(edition);
    }

    //8
    public List<TournamentsAnalyticsModel> tournamentMatchDuration(int edition){
        return tournamentsAnalyticsRepository.findAverageMatchDuration(edition);
    }
}
