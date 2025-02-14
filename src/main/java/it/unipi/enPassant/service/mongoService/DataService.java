package it.unipi.enPassant.service.mongoService;
import it.unipi.enPassant.model.requests.mongoModel.user.DataUserModel;
import it.unipi.enPassant.model.requests.mongoModel.user.DocumentUser;
import it.unipi.enPassant.model.requests.mongoModel.user.StatsModel;
import it.unipi.enPassant.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

        if(!user.getType().equals("1")){
            return new DataUserModel(
                    user.getName(),
                    user.getSurname(),
                    user.getId(),
                    user.getBirthDate()
            );
        }
        return new DataUserModel(
                user.getName(),
                user.getSurname(),
                user.getId(),
                user.getELO(),
                user.getBirthDate()
        );
    }

    public List<String> getAllUserIds(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<DocumentUser> page = userRepository.findAll(pageable);
        List<DocumentUser> list = page.getContent();
        System.out.println(list.size());

        return list
                .stream()
                .map(user -> user.getId().toString()) // Convertiamo l'_id in stringa
                .collect(Collectors.toList());
    }

    public StatsModel dataGetPlayerStats(String username) {
        DocumentUser user = userRepository.findByUsername(username);
        System.out.println(user);
        return new StatsModel(
                user.getELO(),
                user.getNumberOfPlayedMatches(),
                user.getNumberOfVictories(),
                user.getNumberOfDefeats(),
                user.getNumberOfDraws(),
                user.getAvgMovesNumber()
        );
    }
}
