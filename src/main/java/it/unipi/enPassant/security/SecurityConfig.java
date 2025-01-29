package it.unipi.enPassant.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            // tutte le richieste sono pubbliche
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
            )

            // puoi eventualmente disabilitare CSRF se lavori in stateless
            .csrf(csrf -> csrf.disable())

            // evita la schermata di login generata da Spring Security
            .formLogin(form -> form.disable())
            .httpBasic(Customizer.withDefaults());

    return http.build(); // costruiamo il SecurityFilterChain
  }

  // (Opzionale) per definire eventuali risorse statiche “ignorate”
  // @Bean
  // public WebSecurityCustomizer webSecurityCustomizer() {
  //     return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**");
  // }
}

