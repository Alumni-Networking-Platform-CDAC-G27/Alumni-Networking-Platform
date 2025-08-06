package com.anp.config;

import java.util.List;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.anp.services.LoggerService;
import com.anp.services.UserService;
import com.anp.web.filters.JwtAuthenticationFilter;
import com.anp.web.filters.JwtAuthorizationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfiguration {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final LoggerService loggerService;
    private final JwtConfig jwtConfig;

    public ApplicationSecurityConfiguration(UserService userService,
                                            BCryptPasswordEncoder passwordEncoder,
                                            ObjectMapper objectMapper,
                                            LoggerService loggerService,
                                            JwtConfig jwtConfig) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.loggerService = loggerService;
        this.jwtConfig = jwtConfig;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        JwtAuthenticationFilter authenticationFilter =
                new JwtAuthenticationFilter(authenticationManager, objectMapper, loggerService, userService, jwtConfig);

        JwtAuthorizationFilter authorizationFilter =
                new JwtAuthorizationFilter(authenticationManager, userService, jwtConfig);

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/users/register", "/favicon.ico", "/index.html").permitAll()

                .requestMatchers(
                        "/users/details/{id}",
                        "/users/update/{id}",
                        "/relationship/friends/{id}",
                        "/relationship/findFriends/{id}",
                        "/relationship/addFriend",
                        "/relationship/removeFriend",
                        "/relationship/acceptFriend",
                        "/relationship/cancelRequest",
                        "/relationship/search",
                        "/pictures/all/{id}",
                        "/pictures/add",
                        "/pictures/remove",
                        "/post/create",
                        "/post/remove",
                        "/like/add",
                        "/comment/create",
                        "/comment/remove",
                        "/post/all/{id}",
                        "/message/create",
                        "/message/all/{id}",
                        "/message/friend",
                        "/socket/**"
                ).hasAnyAuthority("ADMIN", "ROOT", "USER")

                .requestMatchers(
                        "/users/promote",
                        "/users/demote",
                        "/users/all/{id}",
                        "/users/details/username",
                        "/logs/all",
                        "/logs/findByUserName/{username}"
                ).hasAnyAuthority("ADMIN", "ROOT")

                .requestMatchers(
                        "/users/delete/{id}",
                        "/logs/clear",
                        "/logs/clearByName/{username}"
                ).hasAuthority("ROOT")

                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAnyAuthority("ADMIN", "ROOT", "USER")

                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(authorizationFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("OPTIONS", "HEAD", "GET", "PUT", "POST", "DELETE", "PATCH"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
