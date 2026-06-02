package com.spring.ai.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF (not needed for stateless JWT APIs)
            .csrf(csrf -> csrf.disable())
            
            // 2. Set session management to stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. Secure your specific API paths
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/rag/**").authenticated() // Require token for AI APIs
                .anyRequest().permitAll() // Allow other routes (like health checks)
            )
            
            // 4. Configure this app as an OAuth2 Resource Server using JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );

        return http.build();
    }

    // 5. Tell Spring how to decode and verify the JWT signature
    @Bean
    public JwtDecoder jwtDecoder() {
    	byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        
        // Change "HmacSHA256" to "HmacSHA512" to match the Java cryptography standard
        SecretKeySpec secretKey = new SecretKeySpec(secretBytes, "HmacSHA512"); 

        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512) 
                .build();
    }
}
