package it.unipi.enPassant.model.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

public class LoginModel {
    private String username;
    private String password;

    public LoginModel(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}
