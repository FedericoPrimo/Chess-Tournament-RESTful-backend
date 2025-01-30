package it.unipi.enPassant.model.requests;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter

@Document
public class DocumentMatch {

    private int date;
    private String white;
    private int whiteElo;
    private String eco;
    private String black;
    private int blackElo;
    private String event;
    private String result;
    private String category;
    private List<String> moves;
    private String winner;
    private String timestamp;
    private double duration;

    // Costruttori, Getter e Setter
    public DocumentMatch() {}

    public DocumentMatch(int date, String white, int whiteElo, String eco, String black, int blackElo, String event,
                 String result, String category, List<String> moves, String winner, String timestamp, double duration) {
        this.date = date;
        this.white = white;
        this.whiteElo = whiteElo;
        this.eco = eco;
        this.black = black;
        this.blackElo = blackElo;
        this.event = event;
        this.result = result;
        this.category = category;
        this.moves = moves;
        this.winner = winner;
        this.timestamp = timestamp;
        this.duration = duration;
    }
}
