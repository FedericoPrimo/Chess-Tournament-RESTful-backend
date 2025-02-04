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

    @JsonProperty("whiteElo")
    private int whiteElo;

    @JsonProperty("blackPlayer")
    private String blackPlayer;

    @JsonProperty("blackElo")
    private int blackElo;

    @JsonProperty("eco")
    private String eco;

    @JsonProperty("event")
    private String event;

    @JsonProperty("result")
    private String result;

    @JsonProperty("movelist")
    private List<String> movelist;

    public DataTournamentMatchModel(){}

    public DataTournamentMatchModel(int edition, String category, String location, String matchDate, String whitePlayer, int whiteElo, String blackPlayer, int blackElo, String eco, String event, String result, List<String> movelist) {
        this.edition = edition;
        this.category = category;
        this.location = location;
        this.matchDate = matchDate;
        this.whitePlayer = whitePlayer;
        this.whiteElo = whiteElo;
        this.blackPlayer = blackPlayer;
        this.blackElo = blackElo;
        this.eco = eco;
        this.event = event;
        this.result = result;
        this.movelist = movelist;
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
    public int getWhiteElo() {
        return whiteElo;
    }
    public void setWhiteElo(int whiteElo) {
        this.whiteElo = whiteElo;
    }
    public String getBlackPlayer() {
        return blackPlayer;
    }
    public void setBlackPlayer(String blackPlayer) {
        this.blackPlayer = blackPlayer;
    }
    public int getBlackElo() {
        return blackElo;
    }
    public void setBlackElo(int blackElo) {
        this.blackElo = blackElo;}
    public String getEco() {
        return eco;
    }
    public void setEco(String eco) {
        this.eco = eco;
    }
    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public List<String> getMovelist() {
        return movelist;
    }
    public void setMovelist(List<String> movelist) {
        this.movelist = movelist;
    }
}
