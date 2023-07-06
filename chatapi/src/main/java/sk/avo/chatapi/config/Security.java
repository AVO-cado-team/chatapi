package sk.avo.chatapi.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sk.avo.chatapi.security.JwtRequestFilter;
import sk.avo.chatapi.security.shared.UserRoles;

@Configuration
public class Security {
  private final JwtRequestFilter jwtRequestFilter;

  public Security(JwtRequestFilter jwtRequestFilter) {
    this.jwtRequestFilter = jwtRequestFilter;
  }

  @Bean
  public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
    return http.cors(withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            (authorize) ->
                authorize
                    .requestMatchers(
                        "/api/auth/signup",
                        "/api/auth/login",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/api/dev/**")
                    .permitAll()
                    .requestMatchers("/api/auth/refresh")
                    .hasAnyAuthority(UserRoles.USER_VERIFIED, UserRoles.USER_UNVERIFIED)
                    .requestMatchers("/api/auth/email/verify", "/api/auth/email/resend-code")
                    .hasAuthority(UserRoles.USER_UNVERIFIED)
                    .anyRequest()
                    .hasAuthority(UserRoles.USER_VERIFIED))
        .sessionManagement(
            (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
