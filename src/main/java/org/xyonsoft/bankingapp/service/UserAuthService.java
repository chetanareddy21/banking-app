package org.xyonsoft.bankingapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xyonsoft.bankingapp.Entity.User;
import org.xyonsoft.bankingapp.dto.AuthResponse;
import org.xyonsoft.bankingapp.dto.LoginRequest;
import org.xyonsoft.bankingapp.dto.RegisterRequest;
import org.xyonsoft.bankingapp.exception.DuplicateUsernameException;
import org.xyonsoft.bankingapp.exception.InvalidCredentialsException;
import org.xyonsoft.bankingapp.repository.UserRepository;
import org.xyonsoft.bankingapp.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AccountService accountService;


    public String register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateUsernameException("Username already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        accountService.createAccountFor(user, request.getPin()); // CHANGED — pin now passed through
        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), "USER");
        return new AuthResponse(token, "USER");
    }
}
