package it.unipi.enPassant.model.requests.redisModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class PlayerRequestModel {
    @JsonProperty("username")
    private String username;
    @JsonProperty("text")
    private String text;

    // Costruttore vuoto (necessario per la deserializzazione JSON)
    public PlayerRequestModel() {}

    // Costruttore con parametri
    public PlayerRequestModel(String username, String text) {
        this.username = username;
        this.text = text;
    }

}
