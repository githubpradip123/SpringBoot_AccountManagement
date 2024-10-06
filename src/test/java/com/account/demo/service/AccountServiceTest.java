package com.account.demo.service;

import com.account.demo.dao.AccountRepository;
import com.account.demo.model.Account;
import com.account.demo.service.AccountService;
import com.account.demo.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        accountService = new AccountService(accountRepository);
    }

    // **Create Account Tests (Positive & Negative)**

    @Test
    public void testCreateAccount_Success() {
        Account account = new Account("John Doe", "johndoe@example.com", "password123");
        Account savedAccount = new Account(1L, "John Doe", "johndoe@example.com", PasswordUtil.hashPassword("password123"));

        when(accountRepository.save(account)).thenReturn(savedAccount);

        Account createdAccount = accountService.createAccount(account);

        Assert.notNull(createdAccount, "Created account should not be null");
        Assert.isTrue(createdAccount.getId() > 0, "Created account should have an ID");
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testCreateAccount_NullAccount() {
        try {
            accountService.createAccount(null);
            fail("Expected an exception for null account");
        } catch (Exception e) {
            // Expected exception
        }
    }

    // **Get Account Tests (Positive & Negative)**

    @Test
    public void testGetAccountById_ExistingId() {
        Long id = 1L;
        Account account = new Account(id, "John Doe", "johndoe@example.com", "hashedPassword");
        Optional<Account> optionalAccount = Optional.of(account);

        when(accountRepository.findById(id)).thenReturn(optionalAccount);

        Account retrievedAccount = accountService.getAccountById(id);

        Assert.notNull(retrievedAccount, "Retrieved account should not be null");
        Assertions.assertEquals(id, retrievedAccount.getId(), "Retrieved account ID should match");
        verify(accountRepository, times(1)).findById(id);
    }

    @Test
    public void testGetAccountById_NonExistingId() {
        Long id = 1L;
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        Account retrievedAccount = accountService.getAccountById(id);

        Assert.isNull(retrievedAccount, "Retrieved account should be null for non-existent ID");
        verify(accountRepository, times(1)).findById(id);
    }

    // **Get All Accounts Test**

    @Test
    public void testGetAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1L, "John Doe", "johndoe@example.com", "hashedPassword"));
        accounts.add(new Account(2L, "Jane Doe", "janedoe@example.com", "hashedPassword"));

        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> retrievedAccounts = accountService.getAllAccounts();

        Assert.notNull(retrievedAccounts, "Retrieved accounts list should not be null");
        Assertions.assertEquals(2, retrievedAccounts.size(), "Retrieved accounts list size should match");
        verify(accountRepository, times(1)).findAll();
    }

    // **Update Account Tests (Positive & Negative)**

    @Test
    public void testUpdateAccount_ExistingId() {
        Long id = 1L;
        Account existingAccount = new Account(id, "John Doe", "johndoe@example.com", "hashedPassword");
        Optional<Account> optionalAccount = Optional.of(existingAccount);
        Account updatedAccount = new Account(null, "Jane Doe", "janedoe@example.com", null);

        when(accountRepository.findById(id)).thenReturn(optionalAccount);
        // Simulate saving the updated account (mocked behavior)
        Account savedAccount = new Account(id, "Jane Doe", "janedoe@example.com", "hashedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Account updated = accountService.updateAccount(id, updatedAccount);

        Assert.notNull(updated, "Updated account should not be null");
        Assertions.assertEquals(id, updated.getId(), "Updated account ID should match");
        Assertions.assertEquals("Jane Doe", updated.getName(), "Updated account name should match");
        Assertions.assertEquals("janedoe@example.com", updated.getEmail(), "Updated account email should match");
        verify(accountRepository, times(1)).findById(id);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

 }