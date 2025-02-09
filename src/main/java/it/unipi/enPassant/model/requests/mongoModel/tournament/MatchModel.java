package it.unipi.enPassant.model.requests.mongoModel.tournament;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)


public class MatchModel {
    @JsonProperty("date")
    private int date;

    @JsonProperty("white")
    private String whitePlayer;

    @JsonProperty("black")
    private String blackPlayer;

    @JsonProperty("eco")
    private String eco;

    @JsonProperty("result")
    private String result;

    @JsonProperty("category")
    private String category;

    @JsonProperty("moves")
    private List<String> moves;

    @JsonProperty("winner")
    private String winner;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("duration")
    private double duration;

    public MatchModel() {}


    public MatchModel(int date, String whitePlayer, String blackPlayer,
                      String eco, String result, String category, List<String> moves,
                      String winner, String timestamp, double duration) {
        this.date = date;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.eco = eco;
        this.result = result;
        this.category = category;
        this.moves = moves;
        this.winner = winner;
        this.timestamp = timestamp;
        this.duration = duration;
    }
}
