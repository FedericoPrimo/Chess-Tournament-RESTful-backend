package it.unipi.enPassant.model.requests;
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

    @JsonProperty("whiteElo")
    private int whiteElo;

    @JsonProperty("black")
    private String blackPlayer;

    @JsonProperty("blackElo")
    private int blackElo;

    @JsonProperty("eco")
    private String eco;

    @JsonProperty("event")
    private String event;

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

    // Costruttore vuoto
    public MatchModel() {}

    // Costruttore con parametri
    public MatchModel(int date, String whitePlayer, int whiteElo, String blackPlayer, int blackElo,
                      String eco, String event, String result, String category, List<String> moves,
                      String winner, String timestamp, double duration) {
        this.date = date;
        this.whitePlayer = whitePlayer;
        this.whiteElo = whiteElo;
        this.blackPlayer = blackPlayer;
        this.blackElo = blackElo;
        this.eco = eco;
        this.event = event;
        this.result = result;
        this.category = category;
        this.moves = moves;
        this.winner = winner;
        this.timestamp = timestamp;
        this.duration = duration;
    }
}
