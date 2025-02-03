package it.unipi.enPassant.model.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TournamentsAnalyticsModel {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("eco")
    private String eco;

    @JsonProperty("category")
    private String category;

    @JsonProperty("edition")
    private Integer edition;

    @JsonProperty("howMany")
    private Integer howMany;

    @JsonProperty("averageMatchDuration")
    private Double averageMatchDuration;

    @JsonProperty("numberOfMatches")
    private Integer numberOfMatches;

    @JsonProperty("whiteWinPercentage")
    private Double whiteWinPercentage;

    @JsonProperty("drawPercentage")
    private Double drawPercentage;

    @JsonProperty("blackWinPercentage")
    private Double blackWinPercentage;

    // Empty Constructor
    public TournamentsAnalyticsModel() {}

    // Constructor 1
    public TournamentsAnalyticsModel(String userId, String category, int edition, int howMany) {
        this.userId = userId;
        this.category = category;
        this.edition = edition;
        this.howMany = howMany;
        this.eco = null;
        this.averageMatchDuration = null;
        this.numberOfMatches = null;
        this.whiteWinPercentage = null;
        this.drawPercentage = null;
        this.blackWinPercentage = null;
    }

    // Constructor 2
    public TournamentsAnalyticsModel(String eco, int edition, String category, int howMany, boolean isEco) {
        this.eco = eco;
        this.edition = edition;
        this.category = category;
        this.howMany = howMany;
        this.userId = null;
        this.averageMatchDuration = null;
        this.numberOfMatches = null;
        this.whiteWinPercentage = null;
        this.drawPercentage = null;
        this.blackWinPercentage = null;
    }

    // Constructor 3
    public TournamentsAnalyticsModel(int edition, String category, double averageMatchDuration) {
        this.edition = edition;
        this.category = category;
        this.averageMatchDuration = averageMatchDuration;
        this.eco = null;
        this.userId = null;
        this.howMany = null;
        this.numberOfMatches = null;
        this.whiteWinPercentage = null;
        this.drawPercentage = null;
        this.blackWinPercentage = null;
    }

    //Constructor 4
    public TournamentsAnalyticsModel(String eco, int numberOfMatches, double whiteWinPercentage, double drawPercentage, double blackWinPercentage) {
        this.eco= eco;
        this.numberOfMatches = numberOfMatches;
        this.whiteWinPercentage = whiteWinPercentage;
        this.drawPercentage = drawPercentage;
        this.blackWinPercentage = blackWinPercentage;
        this.userId = null;
        this.averageMatchDuration = null;
        this.edition = null;
        this.howMany = null;
        this.category = null;
    }


    //getter & setter
    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public String getEco() {return eco;}
    public void setEco(String eco) {this.eco = eco;}

    public String getCategory() {return category;}
    public void setCategory(String category) {this.category = category;}

    public Integer getEdition() {return edition;}
    public void setEdition(int edition) {this.edition = edition;}

    public Integer getHowMany() {return howMany;}
    public void setHowMany(int avgNumberOfMoves) {this.howMany = avgNumberOfMoves;}

    public Double getAverageMatchDuration() {return averageMatchDuration;}
    public void setAverageMatchDuration(Double averageMatchDuration) {this.averageMatchDuration = averageMatchDuration;}

    public Integer getNumberOfMatches() {return numberOfMatches;}
    public void setNumberOfMatches(int numberOfMatches) {this.numberOfMatches = numberOfMatches;}

    public Double getWhiteWinPercentage() {return whiteWinPercentage;}
    public void setWhiteWinPercentage(double whiteWinPercentage) {this.whiteWinPercentage = whiteWinPercentage;}

    public Double getDrawPercentage() {return drawPercentage;}
    public void setDrawPercentage(double drawPercentage) {this.drawPercentage = drawPercentage;}

    public Double getBlackWinPercentage() {return blackWinPercentage;}
    public void setBlackWinPercentage(double blackWinPercentage) {this.blackWinPercentage = blackWinPercentage;}
}
