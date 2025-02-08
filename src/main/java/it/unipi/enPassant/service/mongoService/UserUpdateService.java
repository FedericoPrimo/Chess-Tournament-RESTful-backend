package it.unipi.enPassant.service.mongoService;

import it.unipi.enPassant.model.requests.mongoModel.tournament.UserMatchUpdateModel;
import it.unipi.enPassant.model.requests.mongoModel.user.DocumentUser;
import it.unipi.enPassant.repositories.TournamentRepository;
import it.unipi.enPassant.repositories.UserRepository;
import it.unipi.enPassant.repositories.UserUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class UserUpdateService {
    private final UserUpdateRepository userUpdateRepository;
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserUpdateService.class);

    @Autowired
    public UserUpdateService(UserUpdateRepository userUpdateRepository, TournamentRepository tournamentRepository, UserRepository userRepository) {
        this.userUpdateRepository = userUpdateRepository;
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
    }

    public void updateFields(String username) {
        //Fetch matches from tournaments collection
        List<UserMatchUpdateModel> list = tournamentRepository.findMatchesForUser(username);

        //Update matches array in user collection
        userUpdateRepository.updateUserMatches(username, list);

        //Update player's stats
        userUpdateRepository.updateUserStats(username);

        //update player's elo
        userUpdateRepository.updateEloForUser(username);
    }
}
