package it.unipi.enPassant.model.requests;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DataUserModel {

        @JsonProperty("name")
        private String name;

        @JsonProperty("surname")
        private String surname;

        @JsonProperty("username")
        private String username;

        @JsonProperty("elo")
        private int elo;

        @JsonProperty("birthdate")
        private String birthdate;

        // Costruttore vuoto necessario per Jackson
        public DataUserModel() {}

        // Costruttore con parametri
        public DataUserModel(String name, String surname, String username, int elo, String birthdate) {
            this.name = name;
            this.surname = surname;
            this.username = username;
            this.elo = elo;
            this.birthdate = birthdate;
        }
}
