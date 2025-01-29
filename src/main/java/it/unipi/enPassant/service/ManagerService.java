package it.unipi.enPassant.service;

import it.unipi.enPassant.model.requests.PlayerRequestModel;
import org.springframework.stereotype.Service;

@Service
public class ManagerService {
    public boolean addPlayer(String username){
        /*here we have to add a player to a bucket with all the
        added player so at the end of the day we pass to mongoDB*/
        return true;
    }
    public boolean disqualifyPlayer(String username){
        /*here we have to add a player to a bucket with all the
        added player so at the end of the day we pass to mongoDB*/
        return true;
    }
    public boolean deleteMatch(String White, String Black){
        /*here we have to add a player to a bucket with all the
        added player so at the end of the day we pass to mongoDB*/
        return true;
    }
    public boolean createMaindraw(){
        /*here we have to add a player to a bucket with all the
        added player so at the end of the day we pass to mongoDB*/
        return true;
    }

    public PlayerRequestModel checkPlayerRequest() {
        return new PlayerRequestModel(
                "ciao","ciao"
        );
    }
}
