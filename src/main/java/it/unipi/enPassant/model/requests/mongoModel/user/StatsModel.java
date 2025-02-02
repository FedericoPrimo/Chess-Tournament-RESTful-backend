package it.unipi.enPassant.model.requests;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatsModel {
    private int elo;
    // Costruttore vuoto
    public StatsModel() {
    }

    // Costruttore con parametri
    public StatsModel(int elo) {
        this.elo = elo;
    }

    // Getter
    public int getElo() {
        return elo;
    }

    // Setter
    public void setElo(int elo) {
        this.elo = elo;
    }
}
