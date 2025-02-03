package it.unipi.enPassant.model.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TournamentsAnalyticAVGModel {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("eco")
    private String eco;

    @JsonProperty("category")
    private String category;

    @JsonProperty("edition")
    private int edition;

    @JsonProperty("howMany")
    private Integer howMany;

    @JsonProperty("averageMatchDuration")
    private Double averageMatchDuration;

    // Empty Constructor
    public TournamentsAnalyticAVGModel() {}

    // Constructor 1
    public TournamentsAnalyticAVGModel(String userId, String category, int edition, int howMany) {
        this.userId = userId;
        this.category = category;
        this.edition = edition;
        this.howMany = howMany;
        this.eco = null;
        this.averageMatchDuration = null;
    }

    // Constructor 2
    public TournamentsAnalyticAVGModel(String eco, int edition, String category, int howMany, boolean isEco) {
        this.eco = eco;
        this.edition = edition;
        this.category = category;
        this.howMany = howMany;
        this.userId = null;
        this.averageMatchDuration = null;
    }

    // Constructor 3
    public TournamentsAnalyticAVGModel(int edition, String category, double averageMatchDuration) {
        this.edition = edition;
        this.category = category;
        this.averageMatchDuration = averageMatchDuration;
        this.eco = null;
        this.userId = null;
        this.howMany = null;
    }


    //getter & setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEco() {
        return eco;
    }

    public void setEco(String eco) {
        this.eco = eco;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public Integer getHowMany() {
        return howMany;
    }
    public void setHowMany(int avgNumberOfMoves) {
        this.howMany = avgNumberOfMoves;
    }

    public Double getAverageMatchDuration() {
        return averageMatchDuration;
    }

    public void setAverageMatchDuration(double averageMatchDuration) {
        this.averageMatchDuration = averageMatchDuration;
    }
}
