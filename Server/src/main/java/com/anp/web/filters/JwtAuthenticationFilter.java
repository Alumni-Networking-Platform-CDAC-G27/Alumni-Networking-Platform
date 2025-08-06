package com.anp.web.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.anp.config.JwtConfig;
import com.anp.domain.entities.User;
import com.anp.domain.models.bindingModels.user.UserLoginBindingModel;
import com.anp.services.LoggerService;
import com.anp.services.UserService;
import com.anp.utils.responseHandler.exceptions.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final ObjectMapper mapper;
    private final LoggerService loggerService;
    private final UserService userService;
    private final JwtConfig jwtConfig;
  

    @Autowired
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper mapper, LoggerService loggerService, UserService userService, JwtConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        this.mapper = mapper;
        this.loggerService = loggerService;
        this.userService = userService;
        this.jwtConfig=jwtConfig;
    }

   

	@Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserLoginBindingModel loginBindingModel = new ObjectMapper()
                    .readValue(request.getInputStream(), UserLoginBindingModel.class);

            Authentication authenticate = this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginBindingModel.getUsername(),
                            loginBindingModel.getPassword(),
                            new ArrayList<>()));

            return authenticate;

        } catch (IOException | AuthenticationException ex) {
            ex.printStackTrace();
            throw new CustomException(ex.getMessage());
//            return null;
        }
    }

//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
//        User user = ((User) authResult.getPrincipal());
//        String authority = user.getAuthorities()
//                .stream()
//                .findFirst()
//                .orElse(null)
//                .getAuthority();
//        String id = user.getId();
//        String profilePicUrl = user.getProfilePicUrl();
//        String firstName = user.getFirstName();
//
//        String token = Jwts.builder()
//                .setSubject(user.getUsername())
//                .setExpiration(new Date(System.currentTimeMillis() + 1200000000))
//                .claim("role", authority)
//                .claim("id", id)
//                .claim("profilePicUrl", profilePicUrl)
//                .claim("firstName", firstName)
//                .signWith(SignatureAlgorithm.HS256, "Secret".getBytes())
//                .compact();
//
//        String tokenJson = this.mapper.writeValueAsString("token " + token);
//
////        response.getWriter()
////                .append("Authorization: Bearer " + token);
//        response.getWriter()
//                .append(tokenJson);
//
//
//        if (request.getMethod().equals("POST") && request.getRequestURI().endsWith("/login")) {
//                String username = user.getUsername();
//                loggerService.createLog("POST", username, "-", "login");
//        }
//
//        response.addHeader("Authorization", "Bearer " + token);
//    }
    
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        User user = ((User) authResult.getPrincipal());
        String authority = user.getAuthorities().stream().findFirst().orElse(null).getAuthority();
        String id = user.getId();
        String profilePicUrl = user.getProfilePicUrl();
        String firstName = user.getFirstName();

        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpirationTime()))
                .claim("role", authority)
                .claim("id", id)
                .claim("profilePicUrl", profilePicUrl)
                .claim("firstName", firstName)
                .signWith(jwtConfig.getSecretKey()) // ✅ SECURE key usage
                .compact();

        String tokenJson = this.mapper.writeValueAsString("token " + token);
        response.getWriter().append(tokenJson);

        if ("POST".equals(request.getMethod()) && request.getRequestURI().endsWith("/login")) {
            loggerService.createLog("POST", user.getUsername(), "-", "login");
        }

        response.addHeader("Authorization", "Bearer " + token);
    }
}
