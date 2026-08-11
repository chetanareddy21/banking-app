package org.xyonsoft.bankingapp.config;

import org.xyonsoft.bankingapp.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.xyonsoft.bankingapp.security.OAuthSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final OAuthSuccessHandler oAuthSuccessHandler;

    public SecurityConfig(JwtFilter jwtFilter, OAuthSuccessHandler oAuthSuccessHandler) {
        this.jwtFilter = jwtFilter;
        this.oAuthSuccessHandler = oAuthSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Enable CORS and disable CSRF for stateless REST APIs
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("*"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. PUBLIC HTML & STATIC RESOURCES
                        .requestMatchers("/login", "/user-login", "/user-register", "/admin-login",
                                "/user-dashboard", "/admin-dashboard",
                                "/profile", "/deposit", "/withdraw", "/transactions",
                                "/create-account", "/admin-accounts", "/oauth-success", "/css/**", "/js/**").permitAll()

                        // 2. PUBLIC REST API ENDPOINTS (MUST BE PLACED ABOVE /api/user/**)
                        .requestMatchers("/api/user/login", "/api/user/register", "/api/admin/login").permitAll()

                        // SecurityConfig.java — add to your permitAll list
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        // 3. PROTECTED REST API ENDPOINTS
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/account/**").hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                );

        // Custom login page setup
        http.formLogin(form -> form
                .loginPage("/login")
                .permitAll()
        );

        // OAuth setup
        http.oauth2Login(oauth -> oauth
                .loginPage("/login")
                .successHandler(oAuthSuccessHandler)
        );

        // Add JwtFilter once before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}