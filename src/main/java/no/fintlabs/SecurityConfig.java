package no.fintlabs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/swagger-ui/**", "/swagger-ui**", "/api/api-docs/**", "/api/api-docs**").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
