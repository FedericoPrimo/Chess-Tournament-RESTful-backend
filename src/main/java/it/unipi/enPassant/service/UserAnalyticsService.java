package it.unipi.enPassant.service;

import it.unipi.enPassant.model.requests.UserAnalyticsModel;
import it.unipi.enPassant.repositories.UserAnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAnalyticsService {
    private UserAnalyticsRepository userAnalyticsRepository;

    public UserAnalyticsService(UserAnalyticsRepository userAnalyticsRepository) {
        this.userAnalyticsRepository = userAnalyticsRepository;
    }

    //1
    public List<UserAnalyticsModel> getAllPlayersMFO() {
        return userAnalyticsRepository.findAllMostFrequentOpenings();
    }

    //1 VAR
    public UserAnalyticsModel getPlayersMFO(String username) {
        return userAnalyticsRepository.findPlayerMostFrequentOpening(username);
    }

    //2
    public List<UserAnalyticsModel> getAllPlayersWinningMFO(String outcome){
        return userAnalyticsRepository.findAllMostFrequentWinningOpening(outcome);
    }

    //2 VAR
    public UserAnalyticsModel getPlayersWinningMFO(String username, String outcome) {
        return userAnalyticsRepository.findPlayerMostFrequentWinningOpening(username, outcome);
    }

    //3
    public List<UserAnalyticsModel> getNumberOfDefeatedOpponents(){
        return userAnalyticsRepository.numberOfDefeatedOpponents();
    }

    //3 VAR
    public UserAnalyticsModel getPlayerNumberOfDefeatedOpponents(String username) {
        return userAnalyticsRepository.playerNumberOfDefeatedOpponents(username);
    }


    //4
    public List<UserAnalyticsModel> getNumberOfWonMatches(String outcome){
        return userAnalyticsRepository.numberOfWonMatches(outcome);
    }

    //4 VAR
    public UserAnalyticsModel getPlayersNumberOfWonMatches(String outcome, String username) {
        return userAnalyticsRepository.playerNumberOfWonMatches(outcome, username);
    }


    //5
    public List<String> getDisqualifiedPlayers(){
        return userAnalyticsRepository.findDisqualified();
    }
}
