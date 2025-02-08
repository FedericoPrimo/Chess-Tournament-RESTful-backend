package it.unipi.enPassant.model.requests.mongoModel.user;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatsModel {
    private int elo;
    private int numberOfPlayedMatches;
    private int numberOfVictories;
    private int numberOfDefeats;
    private int numberOfDraws;
    private double avgMovesNumber;

    // Costruttore vuoto
    public StatsModel() {
    }

    // Costruttore con parametri
    public StatsModel(int elo, int numberOfPlayedMatches, int numberOfVictories,
                      int numberOfDefeats, int numberOfDraws, double avgMovesNumber) {
        this.elo = elo;
        this.numberOfPlayedMatches = numberOfPlayedMatches;
        this.numberOfVictories = numberOfVictories;
        this.numberOfDefeats = numberOfDefeats;
        this.numberOfDraws = numberOfDraws;
        this.avgMovesNumber = avgMovesNumber;
    }

    // Getters
    public int getElo() {
        return elo;
    }

    public int getNumberOfPlayedMatches() {
        return numberOfPlayedMatches;
    }

    public int getNumberOfVictories() {
        return numberOfVictories;
    }

    public int getNumberOfDefeats() {
        return numberOfDefeats;
    }

    public int getNumberOfDraws() {
        return numberOfDraws;
    }

    public double getAvgMovesNumber() {
        return avgMovesNumber;
    }

    // Setters
    public void setElo(int elo) {
        this.elo = elo;
    }

    public void setNumberOfPlayedMatches(int numberOfPlayedMatches) {
        this.numberOfPlayedMatches = numberOfPlayedMatches;
    }

    public void setNumberOfVictories(int numberOfVictories) {
        this.numberOfVictories = numberOfVictories;
    }

    public void setNumberOfDefeats(int numberOfDefeats) {
        this.numberOfDefeats = numberOfDefeats;
    }

    public void setNumberOfDraws(int numberOfDraws) {
        this.numberOfDraws = numberOfDraws;
    }

    public void setAvgMovesNumber(double avgMovesNumber) {
        this.avgMovesNumber = avgMovesNumber;
    }
}
