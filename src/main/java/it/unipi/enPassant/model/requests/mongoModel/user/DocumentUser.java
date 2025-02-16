package it.unipi.enPassant.model.requests.mongoModel.user;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "user")
public class DocumentUser {

    @Id
    private String id;
    
    private String BirthDate;
    private String Name;
    private String Password;
    private String Surname;
    private String Type;
    private Integer ELO;
    private Boolean Status;
    private List<DocumentMatchUser> Matches;


    private Integer NumberOfPlayedMatches;
    private Integer NumberOfVictories;
    private Integer NumberOfDefeats;
    private Integer NumberOfDraws;
    private Double avgMovesNumber;


    public DocumentUser() {}

    public DocumentUser(String Id, String BirthDate, String Name, String Password, String Surname,
                        String Type, Integer ELO, Boolean Status, List<DocumentMatchUser> Matches,
                        Integer NumberOfPlayedMatches, Integer NumberOfVictories, Integer NumberOfDefeats,
                        Integer NumberOfDraws, Double avgMovesNumber) {
        this.id = Id;
        this.BirthDate = BirthDate;
        this.Name = Name;
        this.Password = Password;
        this.Surname = Surname;
        this.Type = Type;
        this.ELO = ELO;
        this.Status = Status;
        this.Matches = Matches;
        this.NumberOfPlayedMatches = NumberOfPlayedMatches;
        this.NumberOfVictories = NumberOfVictories;
        this.NumberOfDefeats = NumberOfDefeats;
        this.NumberOfDraws = NumberOfDraws;
        this.avgMovesNumber = avgMovesNumber;
    }

    public DocumentUser(String Id, String BirthDate, String Name, String Password, String Surname,
                        String Type, Integer ELO, List<DocumentMatchUser> Matches,
                        Integer NumberOfPlayedMatches, Integer NumberOfVictories, Integer NumberOfDefeats,
                        Integer NumberOfDraws, Double avgMovesNumber) {
        this.id = Id;
        this.BirthDate = BirthDate;
        this.Name = Name;
        this.Password = Password;
        this.Surname = Surname;
        this.Type = Type;
        this.ELO = ELO;
        this.Status = Boolean.FALSE;
        this.Matches = Matches;
        this.NumberOfPlayedMatches = NumberOfPlayedMatches;
        this.NumberOfVictories = NumberOfVictories;
        this.NumberOfDefeats = NumberOfDefeats;
        this.NumberOfDraws = NumberOfDraws;
        this.avgMovesNumber = avgMovesNumber;
    }

    public DocumentUser(String Id, String BirthDate, String Name, String Password, String Surname, String Type) {
        this.id = Id;
        this.BirthDate = BirthDate;
        this.Name = Name;
        this.Password = Password;
        this.Surname = Surname;
        this.Type = Type;
        this.Status = null;
        this.ELO = null;
        this.Matches = null;
        this.NumberOfPlayedMatches = null;
        this.NumberOfVictories = null;
        this.NumberOfDefeats = null;
        this.NumberOfDraws = null;
        this.avgMovesNumber = null;
    }

    // Getter & Setter
    public String getId() {
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

    public Integer getELO() {
        return ELO;
    }

    public void setELO(Integer ELO) {
        this.ELO = ELO;
    }

    public Boolean isStatus() {
        return Status;
    }

    public void setStatus(Boolean Status) {
        this.Status = Status;
    }

    public List<DocumentMatchUser> getMatches() {
        return Matches;
    }

    public void setMatches(List<DocumentMatchUser> Matches) {
        this.Matches = Matches;
    }

    public Integer getNumberOfPlayedMatches() {
        return NumberOfPlayedMatches;
    }

    public void setNumberOfPlayedMatches(Integer NumberOfPlayedMatches) {
        this.NumberOfPlayedMatches = NumberOfPlayedMatches;
    }

    public Integer getNumberOfVictories() {
        return NumberOfVictories;
    }

    public void setNumberOfVictories(Integer NumberOfVictories) {
        this.NumberOfVictories = NumberOfVictories;
    }

    public Integer getNumberOfDefeats() {
        return NumberOfDefeats;
    }

    public void setNumberOfDefeats(Integer NumberOfDefeats) {
        this.NumberOfDefeats = NumberOfDefeats;
    }

    public Integer getNumberOfDraws() {
        return NumberOfDraws;
    }

    public void setNumberOfDraws(Integer NumberOfDraws) {
        this.NumberOfDraws = NumberOfDraws;
    }

    public Double getAvgMovesNumber() {
        return avgMovesNumber;
    }

    public void setAvgMovesNumber(Double avgMovesNumber) {
        this.avgMovesNumber = avgMovesNumber;
    }
}
