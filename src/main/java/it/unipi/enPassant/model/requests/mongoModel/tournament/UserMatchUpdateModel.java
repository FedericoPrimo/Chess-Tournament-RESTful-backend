package it.unipi.enPassant.model.requests.mongoModel.tournament;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserMatchUpdateModel {
    private UserMatchUpdateModel userMatch;

    @JsonProperty("Color")
    private String Color;

    @JsonProperty("OpponentId")
    private String OppponentId;

    @JsonProperty("Outcome")
    private String Outcome;

    @JsonProperty("Opening")
    private String Opening;

    @JsonProperty("TournamentEdition")
    private Integer TournamentEdition;

    @JsonProperty("TournamentCategory")
    private String TournamentCategory;

    @JsonProperty("NumberOfMoves")
    private Integer NumberOfMoves;

    public UserMatchUpdateModel() {}

    public UserMatchUpdateModel(UserMatchUpdateModel userMatch, String OpponentId, String Outcome, String Opening, Integer TournamentEdition, String TournamentCategory, Integer NumberOfMoves) {
        this.userMatch = userMatch;
        this.OppponentId = OpponentId;
        this.Outcome = Outcome;
        this.Opening = Opening;
        this.TournamentEdition = TournamentEdition;
        this.TournamentCategory = TournamentCategory;
        this.NumberOfMoves = NumberOfMoves;
    }

    //getter & setter
    public String getOpponentId() {return OppponentId;}
    public void setOpponentId(String opponentId) {OppponentId = opponentId;}

    public String getOutcome() {return Outcome;}
    public void setOutcome(String outcome) {Outcome = outcome;}

    public String getOpening() {return Opening;}
    public void setOpening(String opening) {Opening = opening;}

    public Integer getTournamentEdition() {return TournamentEdition;}
    public void setTournamentEdition(Integer tournamentEdition) {TournamentEdition = tournamentEdition;}

    public String getTournamentCategory() {return TournamentCategory;}
    public void setTournamentCategory(String tournamentCategory) {TournamentCategory = tournamentCategory;}
}
