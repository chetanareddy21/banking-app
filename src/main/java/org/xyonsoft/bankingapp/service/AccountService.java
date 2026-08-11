package org.xyonsoft.bankingapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xyonsoft.bankingapp.Entity.Account;
import org.xyonsoft.bankingapp.Entity.Transaction;
import org.xyonsoft.bankingapp.Entity.TransactionType;
import org.xyonsoft.bankingapp.Entity.User;
import org.xyonsoft.bankingapp.dto.AccountResponse;
import org.xyonsoft.bankingapp.dto.AdminAccountResponse;
import org.xyonsoft.bankingapp.dto.ProfileResponse;
import org.xyonsoft.bankingapp.dto.TransactionResponse;
import org.xyonsoft.bankingapp.exception.AccountNotFoundException;
import org.xyonsoft.bankingapp.exception.InsufficientBalanceException;
import org.xyonsoft.bankingapp.exception.InvalidPinException;
import org.xyonsoft.bankingapp.repository.AccountRepository;
import org.xyonsoft.bankingapp.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder; // NEW

    // Inject PasswordEncoder using @Lazy
    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository, @Lazy PasswordEncoder passwordEncoder /* other dependencies */) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Called from AuthService.register(), AND from the "Create New Account" button. */
    public AccountResponse createAccountFor(User user, String rawPin) {
        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .balance(BigDecimal.ZERO)
                .pin(passwordEncoder.encode(rawPin)) // NEW — hash it, same as a password
                .user(user)
                .build();
        accountRepository.save(account);
        return new AccountResponse(account.getAccountNumber(), account.getBalance());
    }

    @Transactional
    public AccountResponse deposit(String username, String accountNumber, String pin, BigDecimal amount) {
        validateAmount(amount);
        Account account = getOwnedAccount(username, accountNumber);
        verifyPin(account, pin); // NEW
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        recordTransaction(account, TransactionType.DEPOSIT, amount, "Deposit");
        return new AccountResponse(account.getAccountNumber(), account.getBalance());
    }

    @Transactional
    public AccountResponse withdraw(String username, String accountNumber, String pin, BigDecimal amount) {
        validateAmount(amount);
        Account account = getOwnedAccount(username, accountNumber);
        verifyPin(account, pin); // NEW
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        recordTransaction(account, TransactionType.WITHDRAW, amount, "Withdrawal");
        return new AccountResponse(account.getAccountNumber(), account.getBalance());
    }

    private void verifyPin(Account account, String rawPin) {
        if (rawPin == null || !passwordEncoder.matches(rawPin, account.getPin())) {
            throw new InvalidPinException("Incorrect PIN");
        }
    }

    public ProfileResponse getProfile(String username) {
        List<AccountResponse> accounts = accountRepository.findAllByUser_Username(username)
                .stream()
                .map(a -> new AccountResponse(a.getAccountNumber(), a.getBalance()))
                .toList();
        return new ProfileResponse(username, accounts);
    }


    @Transactional
    public AccountResponse transfer(String username, String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        validateAmount(amount);
        Account sender = getOwnedAccount(username, fromAccountNumber);
        Account receiver = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Recipient account not found"));

        if (sender.getAccountNumber().equals(receiver.getAccountNumber())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        accountRepository.save(sender);
        accountRepository.save(receiver);

        recordTransaction(sender, TransactionType.TRANSFER_OUT, amount, "Transfer to " + receiver.getAccountNumber());
        recordTransaction(receiver, TransactionType.TRANSFER_IN, amount, "Transfer from " + sender.getAccountNumber());

        return new AccountResponse(sender.getAccountNumber(), sender.getBalance());
    }

    public List<TransactionResponse> getTransactions(String username, String accountNumber) {
        Account account = getOwnedAccount(username, accountNumber);
        return transactionRepository.findByAccount_IdOrderByTimestampDesc(account.getId())
                .stream()
                .map(t -> new TransactionResponse(t.getType(), t.getAmount(), t.getBalanceAfter(), t.getDescription(), t.getTimestamp()))
                .toList();
    }

    // ---------------- ADMIN OPERATIONS ----------------
    // These deliberately do NOT check ownership — an admin can act on any account.
    // Access is restricted at the controller layer instead (hasRole("ADMIN") in SecurityConfig).

    public List<AdminAccountResponse> searchAccounts(String query) {
        return accountRepository
                .findByAccountNumberContainingIgnoreCaseOrUser_UsernameContainingIgnoreCase(query, query)
                .stream()
                .map(a -> new AdminAccountResponse(a.getUser().getUsername(), a.getAccountNumber(), a.getBalance()))
                .toList();
    }

    @Transactional
    public void deleteAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        transactionRepository.deleteAll(transactionRepository.findByAccount_IdOrderByTimestampDesc(account.getId()));
        accountRepository.delete(account);
    }

    @Transactional
    public AdminAccountResponse modifyAccountBalance(String accountNumber, BigDecimal newBalance) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(newBalance);
        accountRepository.save(account);
        recordTransaction(account, TransactionType.DEPOSIT, newBalance, "Admin balance correction");
        return new AdminAccountResponse(account.getUser().getUsername(), account.getAccountNumber(), account.getBalance());
    }

    // ---------------- PRIVATE HELPERS ----------------

    /** Ensures the account being acted on actually belongs to the calling user. */
    private Account getOwnedAccount(String username, String accountNumber) {
        return accountRepository.findByAccountNumberAndUser_Username(accountNumber, username)
                .orElseThrow(() -> new AccountNotFoundException("Account not found or does not belong to you"));
    }

    private void recordTransaction(Account account, TransactionType type, BigDecimal amount, String description) {
        transactionRepository.save(Transaction.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .balanceAfter(account.getBalance())
                .description(description)
                .timestamp(LocalDateTime.now())
                .build());
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
    }

    private String generateAccountNumber() {
        return "AC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}