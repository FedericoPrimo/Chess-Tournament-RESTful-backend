package it.unipi.enPassant.model.requests.mongoModel.user;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DocumentMatchUser {
    private String Color;
    private int NumberOfMoves;
    private String Outcome;
    private String Opening;
    private String OpponentId;
    private int TournamentEdition;
    private String TournamentCategory;

    // Costruttore vuoto
    public DocumentMatchUser() {}

    // Costruttore completo
    public DocumentMatchUser(String Color, int NumberOfMoves, String Outcome, String Opening, String OpponentId, int TournamentEdition, String TournamentCategory) {
        this.Color = Color;
        this.NumberOfMoves = NumberOfMoves;
        this.Outcome = Outcome;
        this.Opening = Opening;
        this.OpponentId = OpponentId;
        this.TournamentEdition = TournamentEdition;
        this.TournamentCategory = TournamentCategory;
    }

    // Getter e Setter
    public String getColor() {
        return Color;
    }

    public void setColor(String Color) {
        this.Color = Color;
    }

    public int getNumberOfMoves() {
        return NumberOfMoves;
    }

    public void setNumberOfMoves(int NumberOfMoves) {
        this.NumberOfMoves = NumberOfMoves;
    }

    public String getOutcome() {
        return Outcome;
    }

    public void setOutcome(String Outcome) {
        this.Outcome = Outcome;
    }

    public String getOpening() {
        return Opening;
    }

    public void setOpening(String Opening) {
        this.Opening = Opening;
    }

    public String getOpponentId() {
        return OpponentId;
    }

    public void setOpponentId(String OpponentId) {
        this.OpponentId = OpponentId;
    }

    public int getTournamentEdition() {
        return TournamentEdition;
    }

    public void setTournamentEdition(int TournamentEdition) {
        this.TournamentEdition = TournamentEdition;
    }

    public String getTournamentCategory() {
        return TournamentCategory;
    }

    public void setTournamentCategory(String TournamentCategory) {
        this.TournamentCategory = TournamentCategory;
    }
}
