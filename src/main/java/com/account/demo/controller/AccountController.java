package com.account.demo.controller;

import com.account.demo.model.Account;
import com.account.demo.service.AccountService;
import com.account.demo.util.CustomResponsePojo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Object> createAccount(@Valid @RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
       return CustomResponsePojo.generateResponse("Account Successfully Created", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        if (account != null) {
            return new ResponseEntity<>(account, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable
            Long id, @Valid @RequestBody Account updatedAccount) {
        Account account = accountService.updateAccount(id, updatedAccount);
        if (account != null) {
            return new ResponseEntity<>(account, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Account> patchAccount(@PathVariable Long id, @Valid @RequestBody Account updatedAccount) {
        Account account = accountService.patchAccount(id, updatedAccount);
        if (account != null) {
            return new ResponseEntity<>(account, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return CustomResponsePojo.generateResponse("Account Successfully Deleted", HttpStatus.OK);
    }

}