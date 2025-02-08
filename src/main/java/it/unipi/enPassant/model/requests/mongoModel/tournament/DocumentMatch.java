package it.unipi.enPassant.model.requests.mongoModel.tournament;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private int WhiteElo;
    private String Eco;
    private String Black;
    private int BlackElo;
    private String Event;
    private String Result;
    private String Category;
    private List<String> Moves;
    private String Winner;
    private String Timestamp;
    private double Duration;

    // Costruttore vuoto
    public DocumentMatch() {}

    // Costruttore completo
    public DocumentMatch(int Date, String White, int WhiteElo, String Eco, String Black, int BlackElo, String Event,
                         String Result, String Category, List<String> Moves, String Winner, String Timestamp, double Duration) {
        this.Date = Date;
        this.White = White;
        this.WhiteElo = WhiteElo;
        this.Eco = Eco;
        this.Black = Black;
        this.BlackElo = BlackElo;
        this.Event = Event;
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

    public int getWhiteElo() {
        return WhiteElo;
    }

    public void setWhiteElo(int WhiteElo) {
        this.WhiteElo = WhiteElo;
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

    public int getBlackElo() {
        return BlackElo;
    }

    public void setBlackElo(int BlackElo) {
        this.BlackElo = BlackElo;
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String Event) {
        this.Event = Event;
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