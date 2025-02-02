package it.unipi.enPassant.model.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAnalyticNumberModel {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("howMany")
    private int howMany;

    // Empty Constructor
    public UserAnalyticNumberModel() {}

    // Constructor
    public UserAnalyticNumberModel(String username, int howMany) {
        this.userId = username;
        this.howMany = howMany;
    }

    //getter & setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String username) {
        this.userId = username;
    }

    public int getHowMany() {
        return howMany;
    }

    public void setHowMany(int number) {
        this.howMany = number;
    }
}