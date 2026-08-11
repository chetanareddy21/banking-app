package org.xyonsoft.bankingapp.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.xyonsoft.bankingapp.dto.ProfileResponse;
import org.xyonsoft.bankingapp.service.AccountService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AccountService accountService;

    @GetMapping("/dashboard")
    public String userDashboard() {
        return "Welcome User — this is your account dashboard";
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(Authentication auth) {
        return accountService.getProfile(auth.getName());
    }
}
