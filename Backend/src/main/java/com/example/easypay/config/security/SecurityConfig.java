package com.example.easypay.config.security;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
 
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // enables @PreAuthorize
public class SecurityConfig {
 
    //  Password Encoder (BCrypt)
	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 
    // Authentication Manager
//    @Bean
//    AuthenticationManager authenticationManager(
//            AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
 
    //  Security Filter Chain
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
 
        http
            // Disable CSRF (for REST APIs)
            .csrf(csrf -> csrf.disable())
 
            // Enable CORS
            .cors(Customizer.withDefaults())
 
            // Stateless session (JWT-ready)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
 
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
 
                // Public APIs
                .requestMatchers(
                    "/api/auth/**",
                    "/api/users/register",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
 
                // Admin only
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
 
                // Manager only
                .requestMatchers("/api/manager/**").hasRole("MANAGER")
 
                // Common authenticated users
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/policies/**",
                    "/api/claims/**"
                ).hasAnyRole("USER", "ADMIN", "MANAGER")
 
                // Any other request
                .anyRequest().permitAll()
            );
 
        return http.build();
    }
 
    //  CORS Configuration
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
 
        CorsConfiguration config = new CorsConfiguration();
 
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://localhost:5173"
        ));
 
        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
 
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
 
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
 
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
 
 