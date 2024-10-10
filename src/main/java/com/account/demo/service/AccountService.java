package com.account.demo.service;

import com.account.demo.dao.AccountRepository;
import com.account.demo.model.Account;
import com.account.demo.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
    }

    public Account createAccount(Account account) {

        if (validatePassword(account.getPassword())) {

            account.setPassword(PasswordUtil.hashPassword(account.getPassword()));
            return accountRepository.save(account);

        }
        throw new IllegalArgumentException("Invalid password");
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account updateAccount(Long id, Account updatedAccount) {
        Optional<Account> existingAccount = accountRepository.findById(id);
        if (existingAccount.isPresent())
        {
            Account account = existingAccount.get();
            account.setName(updatedAccount.getName());
            account.setEmail(updatedAccount.getEmail());
            return accountRepository.save(account);
        }
        return null;
    }

    public Account patchAccount(Long id, Account updatedAccount) {
        Optional<Account> existingAccount = accountRepository.findById(id);
        if (existingAccount.isPresent())
        {
            Account account = existingAccount.get();
            if (validatePassword(updatedAccount.getPassword())) {
                account.setPassword(PasswordUtil.hashPassword(updatedAccount.getPassword()));
                return accountRepository.save(account);
            }
            throw new IllegalArgumentException("Invalid password");
        }
        return null;
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    public boolean validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        // Password must contain at least one uppercase letter, one lowercase letter, one digit, and at least 8 characters
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        if (!password.matches(passwordRegex)) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter, one lowercase letter, and one digit");
        }
        return true;
    }

}