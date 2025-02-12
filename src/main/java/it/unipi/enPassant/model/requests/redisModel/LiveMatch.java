package it.unipi.enPassant.model.requests.redisModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LiveMatch {
    private String category;
    private String startingTime;
    private String winner;
    private String endTime;
    private String ECO;

    // Costruttore vuoto per Jackson
    public LiveMatch() {}

    public LiveMatch(String category, String startingTime) {
        this.category = category;
        this.startingTime = startingTime;
        this.winner = null;
        this.endTime = null;
        this.ECO = null;
    }

    // Getter e Setter
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStartingTime() { return startingTime; }
    public void setStartingTime(String startingTime) { this.startingTime = startingTime; }

    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getECO() { return ECO; }
    public void setECO(String ECO) { this.ECO = ECO; }

    // Metodo per convertire l'oggetto in JSON
    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

    // Metodo per creare un oggetto da JSON
    public static LiveMatch fromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, LiveMatch.class);
    }
}
