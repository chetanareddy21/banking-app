package org.xyonsoft.bankingapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import org.xyonsoft.bankingapp.Entity.Admin;
import org.xyonsoft.bankingapp.Entity.User;
import org.xyonsoft.bankingapp.dto.AuthResponse;
import org.xyonsoft.bankingapp.dto.LoginRequest;
import org.xyonsoft.bankingapp.dto.RegisterRequest;
import org.xyonsoft.bankingapp.exception.InvalidCredentialsException;
import org.xyonsoft.bankingapp.repository.AdminRepository;
import org.xyonsoft.bankingapp.repository.UserRepository;
import org.xyonsoft.bankingapp.security.JwtUtil;


@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(admin.getUsername(), "ADMIN");
        return new AuthResponse(token, "ADMIN");
    }
}