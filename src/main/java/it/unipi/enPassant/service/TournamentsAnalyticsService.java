package it.unipi.enPassant.service;

import it.unipi.enPassant.model.requests.TournamentsAnalytic3Model;
import it.unipi.enPassant.model.requests.TournamentsAnalyticAVGModel;
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
    public List<TournamentsAnalyticAVGModel> AVGmovesPerWinner(int edition, String category){
        return tournamentsAnalyticsRepository.calculateAvgMovesPerWinner(edition, category);
    }

    //2
    public List<TournamentsAnalyticAVGModel> wonMatchesPerPlayer(int edition, String category){
        return tournamentsAnalyticsRepository.countGamesWonByPlayerPerEdition(edition, category);
    }

    //3
    public List<TournamentsAnalytic3Model> allOpeningsPercentages(){
        return tournamentsAnalyticsRepository.calculateAllOpeningsRates();
    }

    //3 VAR
    public TournamentsAnalytic3Model openingsPercentages(String opening){
        return tournamentsAnalyticsRepository.calculateRatesByOpening(opening);
    }

    //4
    public List<TournamentsAnalyticAVGModel> AVGmovesPerTournament(int edition){
        return tournamentsAnalyticsRepository.calculateAverageMovesPerTournament(edition);
    }

    //5
    public List<TournamentsAnalyticAVGModel> tournamentMFO(int edition){
        return tournamentsAnalyticsRepository.findMostFrequentOpeningPerTournament(edition);
    }

    //8
    public List<TournamentsAnalyticAVGModel> tournamentMatchDuration(int edition){
        return tournamentsAnalyticsRepository.findAverageMatchDuration(edition);
    }
}
