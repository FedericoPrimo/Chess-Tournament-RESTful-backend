package it.unipi.enPassant.service.mongoService;
import it.unipi.enPassant.model.requests.mongoModel.user.DataUserModel;
import it.unipi.enPassant.model.requests.mongoModel.user.DocumentUser;
import it.unipi.enPassant.model.requests.mongoModel.user.StatsModel;
import it.unipi.enPassant.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class DataService {
    @Autowired
    private UserRepository userRepository;

    public DataUserModel dataUserGet(String username) {
        DocumentUser user = userRepository.findByUsername(username);
        System.out.println(user);
        return new DataUserModel(
                user.getName(),
                user.getSurname(),
                user.getid(),
                user.getELO(),
                user.getBirthDate()
        );
    }

    public List<String> getAllUserIds() {
        List<DocumentUser> users = userRepository.findAll();
        System.out.println(users.size());
        return userRepository.findAll()
                .stream()
                .map(user -> user.getid().toString()) // Convertiamo l'_id in stringa
                .collect(Collectors.toList());
    }

    public StatsModel dataGetPlayerStats(String username) {
        DocumentUser user = userRepository.findByUsername(username);
        System.out.println(user);
        return new StatsModel( user.getELO());
    }
}
