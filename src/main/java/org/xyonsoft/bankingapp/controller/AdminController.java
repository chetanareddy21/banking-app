package org.xyonsoft.bankingapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.xyonsoft.bankingapp.dto.AdminAccountResponse;
import org.xyonsoft.bankingapp.dto.ModifyAccountRequest;
import org.xyonsoft.bankingapp.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AccountService accountService;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "Welcome Admin";
    }

    @GetMapping("/accounts/search")
    public List<AdminAccountResponse> search(@RequestParam String query) {
        return accountService.searchAccounts(query);
    }

    @DeleteMapping("/accounts/{accountNumber}")
    public String delete(@PathVariable String accountNumber) {
        accountService.deleteAccount(accountNumber);
        return "Account deleted successfully";
    }

    @PutMapping("/accounts/{accountNumber}")
    public AdminAccountResponse modify(@PathVariable String accountNumber,@Valid @RequestBody ModifyAccountRequest request) {
        return accountService.modifyAccountBalance(accountNumber, request.getBalance());
    }

}