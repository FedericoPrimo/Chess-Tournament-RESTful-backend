package it.unipi.enPassant.service;

import it.unipi.enPassant.model.requests.UserEntity;
import it.unipi.enPassant.model.requests.mongoModel.user.DataUserModel;
import it.unipi.enPassant.model.requests.mongoModel.user.DocumentUser;
import it.unipi.enPassant.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthenticationService implements UserDetailsService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder; //

  @Autowired
  public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder; //
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Find username in mongo and return the UserEntity associated
    DocumentUser user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }

    // Spring Security uses Authorities to check if a user has the right to access a certain endpoint
    String role = user.getType();
    List<String> rolelist = Arrays.asList(role.split(","));
    var authorities = rolelist.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    System.out.println("Authorities: " + authorities);

    return new org.springframework.security.core.userdetails.User(
            user.getid(),
            user.getPassword(),
            authorities
    );
  }

  public void createUser(DocumentUser user) {


  }

  public boolean userExists(String username) {
    DocumentUser user = userRepository.findByUsername(username);
    return user != null;
  }

  @Transactional
  public void migratePasswords() {
    // Recupera gli utenti con type "2" e "0"
    List<DocumentUser> users = userRepository.findAll();
    for (DocumentUser user : users) {
      String currentPassword = user.getPassword();
      // Opzionale: controlla se la password non è già stata hashata (nel caso di BCrypt, l'hash inizia con "$2a$", "$2b$" o "$2y$")
      if (!currentPassword.startsWith("$2a$") && !currentPassword.startsWith("$2b$") && !currentPassword.startsWith("$2y$")) {
        String hashedPassword = passwordEncoder.encode(currentPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);
      }
    }
    System.out.println("Password migration completed");
  }
}

