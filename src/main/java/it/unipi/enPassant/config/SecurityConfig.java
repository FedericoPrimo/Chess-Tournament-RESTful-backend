package it.unipi.enPassant.security;

import it.unipi.enPassant.service.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


// Used to configure Spring Security
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authManager(
          HttpSecurity http,
          PasswordEncoder passwordEncoder,
          AuthenticationService userDetailsService) throws Exception {

    return http
            .getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder)
            .and()
            .build();
  }
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            // Disable CSRF protection since our service is stateless
            .csrf(csrf -> csrf.disable())
            // Disable session management since our service is stateless
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Set security rules
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
            )
            //.authorizeHttpRequests(auth -> {
            //  auth.requestMatchers("/login").permitAll();
            //  auth.anyRequest().authenticated();
            //})

            // Disable form login and HTTP Basic authentication since we are using JWT
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable());

    return http.build();
  }
}
