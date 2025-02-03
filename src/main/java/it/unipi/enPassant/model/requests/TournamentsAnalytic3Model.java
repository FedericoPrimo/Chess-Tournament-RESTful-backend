package it.unipi.enPassant.model.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TournamentsAnalytic3Model {

    @JsonProperty("eco")
    private String eco;

    @JsonProperty("numberOfMatches")
    private int numberOfMatches;

    @JsonProperty("whiteWinPercentage")
    private double whiteWinPercentage;

    @JsonProperty("drawPercentage")
    private double drawPercentage;

    @JsonProperty("blackWinPercentage")
    private double blackWinPercentage;

    public TournamentsAnalytic3Model() {}

    public TournamentsAnalytic3Model(String eco, int numberOfMatches, double whiteWinPercentage, double drawPercentage, double blackWinPercentage) {
        this.eco= eco;
        this.numberOfMatches = numberOfMatches;
        this.whiteWinPercentage = whiteWinPercentage;
        this.drawPercentage = drawPercentage;
        this.blackWinPercentage = blackWinPercentage;
    }

    public String getEco() {
        return eco;
    }

    public void setEco(String ECO) {
        this.eco = eco;
    }

    public int getNumberOfMatches() {
        return numberOfMatches;
    }

    public void setNumberOfMatches(int numberOfMatches) {
        this.numberOfMatches = numberOfMatches;
    }

    public double getWhiteWinPercentage() {
        return whiteWinPercentage;
    }

    public void setWhiteWinPercentage(double whiteWinPercentage) {
        this.whiteWinPercentage = whiteWinPercentage;
    }

    public double getDrawPercentage() {
        return drawPercentage;
    }

    public void setDrawPercentage(double drawPercentage) {
        this.drawPercentage = drawPercentage;
    }

    public double getBlackWinPercentage() {
        return blackWinPercentage;
    }

    public void setBlackWinPercentage(double blackWinPercentage) {
        this.blackWinPercentage = blackWinPercentage;
    }
}
