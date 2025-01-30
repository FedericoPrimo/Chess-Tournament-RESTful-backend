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
    private int edition;
    private String category;
    private String entryClosingDate;
    private String location;
    private String winner;  // Potrebbe essere null

    private List<DocumentMatch> rawMatches;  // Lista dei match

    // Costruttori, Getter e Setter
    public DocumentTournament() {}

    public DocumentTournament(int edition, String category, String entryClosingDate,
                              String location, String winner, List<DocumentMatch> rawMatches) {
        this.edition = edition;
        this.category = category;
        this.entryClosingDate = entryClosingDate;
        this.location = location;
        this.winner = winner;
        this.rawMatches = rawMatches;
    }
}
