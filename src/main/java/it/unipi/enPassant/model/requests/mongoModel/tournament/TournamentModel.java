package it.unipi.enPassant.model.requests;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TournamentModel {

    @JsonProperty("edition")
    private int edition;

    @JsonProperty("category")
    private String category;

    @JsonProperty("location")
    private String location;


    public TournamentModel(int edition, String category, String location) {
        this.category = category;
        this.edition = edition;
        this.location = location;
    }
    public TournamentModel() {
        // Costruttore vuoto necessario per Jackson
    }
}
