package it.unipi.enPassant.model.requests.mongoModel.tournament;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter

@Document
public class DocumentMatch {

    private int Date;
    private String White;
    private String Eco;
    private String Black;
    private String Result;
    private String Category;
    private List<String> Moves;
    private String Winner;
    private String Timestamp;
    private double Duration;

    // Costruttore vuoto
    public DocumentMatch() {}

    // Costruttore completo
    public DocumentMatch(int Date, String White, String Eco, String Black,
                         String Result, String Category, List<String> Moves, String Winner, String Timestamp, double Duration) {
        this.Date = Date;
        this.White = White;
        this.Eco = Eco;
        this.Black = Black;
        this.Result = Result;
        this.Category = Category;
        this.Moves = Moves;
        this.Winner = Winner;
        this.Timestamp = Timestamp;
        this.Duration = Duration;
    }

    // Getter e Setter per tutti i campi
    public int getDate() {
        return Date;
    }

    public void setDate(int Date) {
        this.Date = Date;
    }

    public String getWhite() {
        return White;
    }

    public void setWhite(String White) {
        this.White = White;
    }

    public String getEco() {
        return Eco;
    }

    public void setEco(String Eco) {
        this.Eco = Eco;
    }

    public String getBlack() {
        return Black;
    }

    public void setBlack(String Black) {
        this.Black = Black;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String Result) {
        this.Result = Result;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public List<String> getMoves() {
        return Moves;
    }

    public void setMoves(List<String> Moves) {
        this.Moves = Moves;
    }

    public String getWinner() {
        return Winner;
    }

    public void setWinner(String Winner) {
        this.Winner = Winner;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String Timestamp) {
        this.Timestamp = Timestamp;
    }

    public double getDuration() {
        return Duration;
    }

    public void setDuration(double Duration) {
        this.Duration = Duration;
    }
}