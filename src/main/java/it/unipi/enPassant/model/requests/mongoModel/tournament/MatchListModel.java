package it.unipi.enPassant.model.requests.mongoModel.tournament;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class MatchListModel {

    @JsonProperty("White")
    private String White;

    @JsonProperty("Black")
    private String Black;

    public MatchListModel() {}

    public MatchListModel(String white, String black) {
        this.White = white;
        this.Black = black;
    }
}
