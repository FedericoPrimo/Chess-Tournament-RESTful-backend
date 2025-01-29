package it.unipi.enPassant.service;

import it.unipi.enPassant.model.requests.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DataService {
    public DataUserModel dataUserGet(String username) {
        return new DataUserModel(
                "Alexander",      // Nome
                "Alekhine",       // Cognome
                username,         // Username passato come parametro
                2500,             // ELO fittizio
                LocalDate.of(1990, 5, 15) // Data di nascita fittizia
        );
    }

    public StatsModel dataGetPlayerStats(String username) {
        return new StatsModel(
                /*mettere le statistiche*/
        );
    }
}
