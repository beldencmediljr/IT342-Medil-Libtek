package edu.cit.medil.libtek.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import edu.cit.medil.libtek.features.user.UserRepository;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserIdAuthFilter userIdAuthFilter() {
        return new UserIdAuthFilter(userRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                var config = new CorsConfiguration();
                config.setAllowedOriginPatterns(List.of("*"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            }))
            .addFilterBefore(userIdAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth", "/api/v1/auth/**").permitAll()
                .requestMatchers("/api/resources", "/api/resources/**").permitAll()
                .requestMatchers("/api/scanner", "/api/scanner/**").permitAll()
                .requestMatchers("/api/reservations", "/api/reservations/**").permitAll()
                .requestMatchers("/api/dashboard", "/api/dashboard/**").permitAll()
                .requestMatchers("/api/fines", "/api/fines/**").permitAll()
                
                // Open paths explicitly to support both root routing rules and sub-directories
                .requestMatchers(HttpMethod.OPTIONS, "/api/verifications/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/verifications").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/verifications").permitAll()
                .requestMatchers("/api/verifications/**").permitAll()
                
                .requestMatchers("/api/v1/user/dashboard", "/api/v1/user/profile", "/api/v1/user/profile/**").authenticated()
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable());

        return http.build();
    }
}