package org.xyonsoft.bankingapp.controller;


import jakarta.validation.Valid;
import org.xyonsoft.bankingapp.dto.*;
import org.xyonsoft.bankingapp.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xyonsoft.bankingapp.service.UserAuthService;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthService userAuthService;
    private final AdminAuthService adminAuthService;

    @PostMapping("/api/user/register")
    public String registerUser( @Valid @RequestBody RegisterRequest request) {
        return userAuthService.register(request);
    }

    @PostMapping("/api/user/login")
    public AuthResponse loginUser(@Valid @RequestBody LoginRequest request) {
        return userAuthService.login(request);
    }

    @PostMapping("/api/admin/login")
    public AuthResponse loginAdmin(@Valid @RequestBody LoginRequest request) {
        return adminAuthService.login(request);
    }
}