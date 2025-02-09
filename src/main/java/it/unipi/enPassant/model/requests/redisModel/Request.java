package it.unipi.enPassant.model.requests.redisModel;
import java.io.Serializable;

public class Request implements Serializable {
    private String nomeUtente;
    private String text;

    public Request() {}

    public Request(String nomeUtente, String text) {
        this.nomeUtente = nomeUtente;
        this.text = text;
    }

    // Getter & Setter
    public String getNomeUtente() { return nomeUtente; }
    public void setNomeUtente(String nomeUtente) { this.nomeUtente = nomeUtente; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}