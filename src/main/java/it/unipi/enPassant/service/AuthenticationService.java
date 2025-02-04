package it.unipi.enPassant.service;

import it.unipi.enPassant.model.requests.UserEntity;
import it.unipi.enPassant.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import it.unipi.enPassant.model.requests.DocumentUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
            // .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    // Spring Security uses Authorities to check if a user has the right to access a certain endpoint
    String role = user.getType();
    List<String> rolelist = Arrays.asList(role.split(","));
    var authorities = rolelist.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    return new org.springframework.security.core.userdetails.User(
            user.getid(),
            user.getPassword(),
            authorities
    );
  }

}

