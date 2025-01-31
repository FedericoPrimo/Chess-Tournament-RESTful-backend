package it.unipi.enPassant.model.requests;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@Document(collection = "tournaments")
public class DocumentTournament {
    @Id
    private String id;
    private int Edition;
    private String Category;
    private String entryClosingDate;
    private String Location;
    private String winner;  // Potrebbe essere null

    private List<DocumentMatch> rawMatches;  // Lista dei match

    // Costruttori, Getter e Setter
    public DocumentTournament() {}

    public DocumentTournament(int edition, String category, String entryClosingDate,
                              String location, String winner, List<DocumentMatch> rawMatches) {
        this.Edition = edition;
        this.Category = category;
        this.entryClosingDate = entryClosingDate;
        this.Location = location;
        this.winner = winner;
        this.rawMatches = rawMatches;
    }

    // Getter
    public String getId() {
        return id;
    }

    public int getEdition() {
        return Edition;
    }

    public String getCategory() {
        return Category;
    }

    public String getEntryClosingDate() {
        return entryClosingDate;
    }

    public String getLocation() {
        return Location;
    }

    public String getWinner() {
        return winner;
    }

    public List<DocumentMatch> getRawMatches() {
        return rawMatches;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setEdition(int edition) {
        this.Edition = edition;
    }

    public void setCategory(String category) {
        this.Category = category;
    }

    public void setEntryClosingDate(String entryClosingDate) {
        this.entryClosingDate = entryClosingDate;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setRawMatches(List<DocumentMatch> rawMatches) {
        this.rawMatches = rawMatches;
    }
}
