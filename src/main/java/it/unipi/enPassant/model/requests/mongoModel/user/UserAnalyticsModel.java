package it.unipi.enPassant.model.requests.mongoModel.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAnalyticsModel {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("mostFrequentOpening")
    private String mostFrequentOpening;

    @JsonProperty("howMany")
    private int howMany;

    // Empty Constructor
    public UserAnalyticsModel() {}

    // Constructor
    public UserAnalyticsModel(String username, String mostFrequentOpening, int howMany) {
        this.userId = username;
        this.mostFrequentOpening = mostFrequentOpening;
        this.howMany = howMany;
    }

    public UserAnalyticsModel(String username, int howMany) {
        this.userId = username;
        this.howMany = howMany;
        this.mostFrequentOpening = null;
    }

    //getter & setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String username) {
        this.userId = username;
    }

    public String getMostFrequentOpening() {
        return mostFrequentOpening;
    }

    public void setMostFrequentOpening(String mostFrequentOpening) {
        this.mostFrequentOpening = mostFrequentOpening;
    }

    public int getHowMany() {
        return howMany;
    }

    public void setHowMany(int quantity) {
        this.howMany = quantity;
    }
}
