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


  @Autowired
  public AuthenticationService(UserRepository userRepository) {
    this.userRepository = userRepository;
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

  public boolean userExists(String username) {
    DocumentUser user = userRepository.findByUsername(username);
    return user != null;
  }


}

