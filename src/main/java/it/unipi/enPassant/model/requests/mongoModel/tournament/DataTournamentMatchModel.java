package it.unipi.enPassant.model.requests.mongoModel.tournament;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataTournamentMatchModel
{
    private DataTournamentMatchModel dataTournamentMatchModel;

    @JsonProperty("edition")
    private int edition;

    @JsonProperty("category")
    private String category;

    @JsonProperty("location")
    private String location;

    @JsonProperty("matchDate")
    private String matchDate;

    @JsonProperty("whitePlayer")
    private String whitePlayer;

    @JsonProperty("blackPlayer")
    private String blackPlayer;

    @JsonProperty("eco")
    private String eco;

    @JsonProperty("result")
    private String result;

    @JsonProperty("moves")
    private List<String> moves;

    public DataTournamentMatchModel(){}

    public DataTournamentMatchModel(int edition, String category, String location, String matchDate, String whitePlayer, String blackPlayer, String eco, String result, List<String> moves) {
        this.edition = edition;
        this.category = category;
        this.location = location;
        this.matchDate = matchDate;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.eco = eco;
        this.result = result;
        this.moves = moves;
    }

    public int getEdition() {
        return edition;
    }
    public void setEdition(int edition) {
        this.edition = edition;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getMatchDate() {
        return matchDate;
    }
    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }
    public String getWhitePlayer() {
        return whitePlayer;
    }
    public void setWhitePlayer(String whitePlayer) {
        this.whitePlayer = whitePlayer;
    }
    public String getBlackPlayer() {
        return blackPlayer;
    }
    public void setBlackPlayer(String blackPlayer) {
        this.blackPlayer = blackPlayer;
    }
    public String getEco() {
        return eco;
    }
    public void setEco(String eco) {
        this.eco = eco;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public List<String> getmoves() {
        return moves;
    }
    public void setmoves(List<String> moves) {
        this.moves = moves;
    }
}
