package org.xyonsoft.bankingapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {


    @GetMapping("/user-dashboard")
    public String userDashboard() { return "user-dashboard"; }

    // ============ ADMIN BLOCK START ============
    // Comment out if teaching a User-only app
    @GetMapping("/admin-dashboard")
    public String adminDashboard() { return "admin-dashboard"; }
    // ============ ADMIN BLOCK END ============

    // ============ OAUTH BLOCK START ============
    // Comment out if Google login is disabled
    @GetMapping("/oauth-success")
    public String oauthSuccess() { return "oauth-success"; }
    // ============ OAUTH BLOCK END ============

    // ViewController.java — add this method
    @GetMapping("/account")
    public String accountPage() { return "account"; }

    // ViewController.java — add these routes
    @GetMapping("/profile")
    public String profile() { return "profile"; }

    @GetMapping("/deposit")
    public String deposit() { return "deposit"; }

    @GetMapping("/withdraw")
    public String withdraw() { return "withdraw"; }

    @GetMapping("/transactions")
    public String transactions() { return "transactions"; }

    @GetMapping("/create-account")
    public String createAccountPage() { return "create-account"; }

    @GetMapping("/admin-accounts")
    public String adminAccounts() { return "admin-accounts"; }

    // ViewController.java — add/replace these routes
    @GetMapping("/login")
    public String loginChooser() { return "login"; }

    @GetMapping("/user-login")
    public String userLogin() { return "user-login"; }

    @GetMapping("/user-register")
    public String userRegister() { return "user-register"; }

    @GetMapping("/admin-login")
    public String adminLogin() { return "admin-login"; }
}