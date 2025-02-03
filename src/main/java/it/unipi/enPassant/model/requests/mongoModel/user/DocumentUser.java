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
    private List<DocumentMatchUser> Matches;

    // Costruttore vuoto
    public DocumentUser() {}

    // Costruttore completo
    public DocumentUser(String id, String BirthDate, String Name, String Password, String Surname, 
                        String Type, int ELO, List<DocumentMatchUser> Matches) {
        this.id = id;
        this.BirthDate = BirthDate;
        this.Name = Name;
        this.Password = Password;
        this.Surname = Surname;
        this.Type = Type;
        this.ELO = ELO;
        this.Matches = Matches;
    }

    // Getter e Setter
    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
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

    public List<DocumentMatchUser> getMatches() {
        return Matches;
    }

    public void setMatches(List<DocumentMatchUser> Matches) {
        this.Matches = Matches;
    }
}

