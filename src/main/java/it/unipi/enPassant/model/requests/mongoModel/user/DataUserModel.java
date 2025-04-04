package it.unipi.enPassant.model.requests.mongoModel.user;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

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
        private Integer elo;

        @JsonProperty("birthdate")
        private String birthdate;


        public DataUserModel() {}

        public DataUserModel(String name, String surname, String username, int elo, String birthdate) {
            this.name = name;
            this.surname = surname;
            this.username = username;
            this.elo = elo;
            this.birthdate = birthdate;
        }

        public DataUserModel(String name, String surname, String username, String birthdate) {
            this.name = name;
            this.surname = surname;
            this.username = username;
            this.elo = null;
            this.birthdate = birthdate;
        }
}
