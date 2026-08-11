package org.xyonsoft.bankingapp.controller;



import jakarta.validation.Valid;
import org.xyonsoft.bankingapp.Entity.Role;
import org.xyonsoft.bankingapp.Entity.User;
import org.xyonsoft.bankingapp.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.xyonsoft.bankingapp.repository.UserRepository;
import org.xyonsoft.bankingapp.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    // controller/AccountController.java — updated relevant methods
    @PostMapping("/create")
    public AccountResponse createAccount(Authentication auth,@Valid @RequestBody CreateAccountRequest request) {
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

//        if (user.getRole() != Role.USER) { // keep this if you're still using the shared-table version;
//            // if you've already split into User/Admin tables, this check
//            // isn't needed here at all — Admin has no createAccountFor path
//            throw new RuntimeException("Admins cannot create bank accounts");
//        }

        return accountService.createAccountFor(user, request.getPin());
    }

    @PostMapping("/deposit")
    public AccountResponse deposit(Authentication auth, @Valid @RequestBody AmountRequest request) {
        return accountService.deposit(auth.getName(), request.getAccountNumber(), request.getPin(), request.getAmount());
    }

    @PostMapping("/withdraw")
    public AccountResponse withdraw(Authentication auth, @Valid @RequestBody AmountRequest request) {
        return accountService.withdraw(auth.getName(), request.getAccountNumber(), request.getPin(), request.getAmount());
    }

    @PostMapping("/transfer")
    public AccountResponse transfer(Authentication auth, @Valid @RequestBody TransferRequest request) {
        return accountService.transfer(auth.getName(), request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount());
    }

    @GetMapping("/transactions")
    public List<TransactionResponse> getTransactions(Authentication auth, @Valid @RequestParam String accountNumber) {
        return accountService.getTransactions(auth.getName(), accountNumber);
    }
}