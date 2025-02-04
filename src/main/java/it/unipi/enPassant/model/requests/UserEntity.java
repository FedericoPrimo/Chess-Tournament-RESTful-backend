package it.unipi.enPassant.model.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class UserEntity {
  private String username;
  private String password;
  private List<String> role;

  public UserEntity(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
