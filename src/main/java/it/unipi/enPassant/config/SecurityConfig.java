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

            .authorizeHttpRequests(auth -> auth
                    // Login is permitted to unauthenticated users
                      .requestMatchers("/api/player/login").permitAll()
                      .requestMatchers("/api/manager/login").permitAll()
                      .requestMatchers("/api/spectator/login").permitAll()
                    // All other endpoints require authentication
                      .requestMatchers("/api/tournaments-analytics").authenticated()
                      .requestMatchers("/api/user-analytics").authenticated()
                      .requestMatchers("/api/userCRUD/read/**").authenticated()
                      .requestMatchers("/api/tournamentsCRUD/read/**").authenticated()
                      .requestMatchers("/api/LiveMatch/getLiveMatches").authenticated()
                      .requestMatchers("/api/LiveMatch/matchDetails/**").authenticated()
                      .requestMatchers("/api/LiveMatch/retrieveMoveList/**").authenticated()
                      .requestMatchers("/api/managePlayer/disqualifiedList").authenticated()
                      .requestMatchers("/api/managePlayer/disqualifiedList").authenticated()
                      .requestMatchers("/api/managePlayer/registerList").authenticated()
                      .requestMatchers("/api/managePlayer/registerList").authenticated()
                      .requestMatchers("/api/managePlayer/disqualified/{playerId}").authenticated()

                    // Manager endpoints require manager role ("0")
                      .requestMatchers("/api/manager/**").hasAuthority("0")
                      .requestMatchers("/api/tournament/matchMaking/**").hasAuthority("0")
                      .requestMatchers("/api/requests/next").hasAuthority("0")
                      .requestMatchers("/api/requests/reset").hasAuthority("0")
                      .requestMatchers("/api/requests/size").hasAuthority("0")
                      .requestMatchers("/api/appUpdate").hasAuthority("0")
                      .requestMatchers("/api/userCRUD/**").hasAuthority("0")
                      .requestMatchers("/api/tournamentsCRUD/**").hasAuthority("0")
                      .requestMatchers("/api/LiveMatch/addLiveMatch/**").hasAuthority("0")
                      .requestMatchers("/api/LiveMatch/removeLiveMatch/**").hasAuthority("0")
                      .requestMatchers("/api/LiveMatch/addLiveMatches").hasAuthority("0")
                      .requestMatchers("/api/managePlayer/disqualify/**").hasAuthority("0")
                      .requestMatchers("/api/LiveMatch/insertMatchResult/**").hasAuthority("0")
                    // Player endpoints require player role ("1")
                      .requestMatchers("/api/player/**").hasAuthority("1")
                      .requestMatchers("/api/LiveMatch/insertMoves/**").hasAuthority("1")
                      .requestMatchers("/api/requests/insert").hasAuthority("1")
                      .requestMatchers("/api/managePlayer/register/{playerId}/{category}").hasAuthority("1")
                    // Spectator endpoints require spectator role ("2")
                      .requestMatchers("/api/spectator/**").hasAuthority("2")
                    // Register endpoints require manager or player role
                      .requestMatchers("/api/managePlayer/register/**").hasAnyAuthority("0", "1")
                      .requestMatchers("/api/managePlayer/register/{playerId}").hasAnyAuthority("0", "1")
                      .anyRequest().authenticated()
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