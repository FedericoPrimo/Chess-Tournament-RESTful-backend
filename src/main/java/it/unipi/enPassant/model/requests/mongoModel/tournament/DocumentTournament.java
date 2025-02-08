package it.unipi.enPassant.model.requests.mongoModel.tournament;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String Entry_Closing_Date;
    private String Location;
    private String Winner;  // Potrebbe essere null

    private List<DocumentMatch> RawMatches;  // Lista dei match

    // Costruttori, Getter e Setter
    public DocumentTournament() {}

    public DocumentTournament(int edition, String category, String entryClosingDate,
                              String location, String winner, List<DocumentMatch> RawMatches) {
        this.Edition = edition;
        this.Category = category;
        this.Entry_Closing_Date = entryClosingDate;
        this.Location = location;
        this.Winner = winner;
        this.RawMatches = RawMatches;
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

    public String getEntry_Closing_Date() {
        return Entry_Closing_Date;
    }

    public String getLocation() {
        return Location;
    }

    public String getWinner() {
        return Winner;
    }

    public List<DocumentMatch> getRawMatches() {
        return RawMatches;
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

    public void setEntry_Closing_Date(String entry_Closing_Date) {
        this.Entry_Closing_Date = entry_Closing_Date;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public void setWinner(String winner) {
        this.Winner = winner;
    }

    public void setRawMatches(List<DocumentMatch> RawMatches) {
        this.RawMatches = RawMatches;
    }
}
