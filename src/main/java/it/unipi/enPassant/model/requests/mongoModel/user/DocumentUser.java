package it.unipi.enPassant.model.requests.mongoModel.user;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "user") // Assicurati che il nome della collezione sia corretto
public class DocumentUser {

    @Id
    private String id;
    private String BirthDate;
    private String Name;
    private String Password;
    private String Surname;
    private String Type;
    private int ELO;
    private boolean State; // Indica se l'utente è squalificato
    private List<DocumentMatchUser> Matches;

    // Nuovi campi per le statistiche
    private int NumberOfPlayedMatches;
    private int NumberOfVictories;
    private int NumberOfDefeats;
    private int NumberOfDraws;
    private double avgMovesNumber; // Unico campo che inizia con minuscola

    // Costruttore vuoto
    public DocumentUser() {}

    // Costruttore completo
    public DocumentUser(String Id, String BirthDate, String Name, String Password, String Surname,
                        String Type, int ELO, boolean State, List<DocumentMatchUser> Matches,
                        int NumberOfPlayedMatches, int NumberOfVictories, int NumberOfDefeats,
                        int NumberOfDraws, double avgMovesNumber) {
        this.id = Id;
        this.BirthDate = BirthDate;
        this.Name = Name;
        this.Password = Password;
        this.Surname = Surname;
        this.Type = Type;
        this.ELO = ELO;
        this.State = State;
        this.Matches = Matches;
        this.NumberOfPlayedMatches = NumberOfPlayedMatches;
        this.NumberOfVictories = NumberOfVictories;
        this.NumberOfDefeats = NumberOfDefeats;
        this.NumberOfDraws = NumberOfDraws;
        this.avgMovesNumber = avgMovesNumber;
    }

    // Getter e Setter
    public String getid() {
        return id;
    }

    public void setId(String Id) {
        this.id = Id;
    }

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String BirthDate) {
        this.BirthDate = BirthDate;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String Surname) {
        this.Surname = Surname;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public int getELO() {
        return ELO;
    }

    public void setELO(int ELO) {
        this.ELO = ELO;
    }

    public boolean isState() {
        return State;
    }

    public void setState(boolean State) {
        this.State = State;
    }

    public List<DocumentMatchUser> getMatches() {
        return Matches;
    }

    public void setMatches(List<DocumentMatchUser> Matches) {
        this.Matches = Matches;
    }

    public int getNumberOfPlayedMatches() {
        return NumberOfPlayedMatches;
    }

    public void setNumberOfPlayedMatches(int NumberOfPlayedMatches) {
        this.NumberOfPlayedMatches = NumberOfPlayedMatches;
    }

    public int getNumberOfVictories() {
        return NumberOfVictories;
    }

    public void setNumberOfVictories(int NumberOfVictories) {
        this.NumberOfVictories = NumberOfVictories;
    }

    public int getNumberOfDefeats() {
        return NumberOfDefeats;
    }

    public void setNumberOfDefeats(int NumberOfDefeats) {
        this.NumberOfDefeats = NumberOfDefeats;
    }

    public int getNumberOfDraws() {
        return NumberOfDraws;
    }

    public void setNumberOfDraws(int NumberOfDraws) {
        this.NumberOfDraws = NumberOfDraws;
    }

    public double getAvgMovesNumber() {
        return avgMovesNumber;
    }

    public void setAvgMovesNumber(double avgMovesNumber) {
        this.avgMovesNumber = avgMovesNumber;
    }
}
