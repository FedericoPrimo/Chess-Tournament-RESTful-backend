package it.unipi.enPassant.model.requests.mongoModel.user;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class DocumentMatchUser {
    private String Color;
    private int NumberOfMoves;
    private String Winner;
    private String Opening;
    private String OpponentId;
    private int TournamentEdition;
    private String TournamentCategory;

    // Costruttore vuoto
    public DocumentMatchUser() {}

    // Costruttore completo
    public DocumentMatchUser(String Color, int NumberOfMoves, String Winner, String Opening, String OpponentId, int TournamentEdition, String TournamentCategory) {
        this.Color = Color;
        this.NumberOfMoves = NumberOfMoves;
        this.Winner = Winner;
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

    public String getWinner() {
        return Winner;
    }

    public void setWinner(String Winner) {
        this.Winner = Winner;
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
