package it.unipi.enPassant.config;

import it.unipi.enPassant.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


// Used to configure Spring Security
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Autowired
  private CustomAccessDeniedHandler accessDeniedHandler;

  @Autowired
  private JwtFilter jwtFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authManager(
          HttpSecurity http,
          PasswordEncoder passwordEncoder,
          AuthenticationService userDetailsService) throws Exception {

    AuthenticationManagerBuilder authManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
    authManagerBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    return authManagerBuilder.build();

  }
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            // Disable CSRF protection since our service is stateless
            .csrf(csrf -> csrf.disable())
            // Disable session management since our service is stateless
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Set security rules
            /*
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/player/login").permitAll()
                    .requestMatchers("/api/manager/login").permitAll()
                    .requestMatchers("/api/spectator/login").permitAll()
                    .requestMatchers("/api/player/**").hasAuthority("1")
                    .requestMatchers("/api/manager/**").hasAuthority("0")
                    .requestMatchers("/api/spectator/**").hasAuthority("2")
                    .anyRequest().authenticated()
            )
            */
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
            )

            // Disable form login and HTTP Basic authentication since we are using JWT
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .exceptionHandling(exception -> exception
                    .accessDeniedHandler(accessDeniedHandler)
            )
            // Add JWT token filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
  @Bean
  public DefaultMethodSecurityExpressionHandler expressionHandler() {
    DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setDefaultRolePrefix(""); // Removes the prefix ROLE_ from roles
    return expressionHandler;
  }

}
