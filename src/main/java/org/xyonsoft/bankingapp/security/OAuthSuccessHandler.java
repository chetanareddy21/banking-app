package org.xyonsoft.bankingapp.security;

// security/OAuthSuccessHandler.java

import org.xyonsoft.bankingapp.Entity.Role;
import org.xyonsoft.bankingapp.Entity.User;
import org.xyonsoft.bankingapp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.xyonsoft.bankingapp.service.AccountService;

import java.io.IOException;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    public OAuthSuccessHandler(UserRepository userRepository, AccountService accountService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        boolean isNewUser = userRepository.findByUsername(email).isEmpty();

        User user = userRepository.findByUsername(email).orElseGet(() ->
                userRepository.save(User.builder()
                        .username(email)
                        .password(null) // no password — this account only ever logs in via Google
                        .build()));

        if (isNewUser) {
            accountService.createAccountFor(user, null); // same as normal registration — every User gets an account
        }

        String token = jwtUtil.generateToken(user.getUsername(), "USER"); // always USER, never ADMIN
        response.sendRedirect("/oauth-success?token=" + token);
    }
}