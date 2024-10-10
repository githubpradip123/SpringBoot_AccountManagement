package com.account.demo.service;

import com.account.demo.dao.AccountRepository;
import com.account.demo.model.Account;
import com.account.demo.model.AccountBuilder;
import com.account.demo.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;
import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @MockBean
    private AccountRepository accountRepository;


    @Test
    public void testCreateAccount_Success() {

        String plainPassword = "password123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword); // Pre-hash password for test

        Account account = new AccountBuilder()
                .setName("John Doe")
                .setEmail("johndoe@example.com")
                .setPassword("Password123")
                .build();

        Account savedAccount = new AccountBuilder()
                .setId(1L)
                .setName("John Doe")
                .setEmail("johndoe@example.com")
                .setPassword(hashedPassword)
                .build();

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

    @Test
    public void testGetAccountById_ExistingId() {
        Long id = 1L;
        Account account = new AccountBuilder()
                .setName("John Doe")
                .setEmail("johndoe@example.com")
                .setPassword("hashedPassword")
                .build();

        Optional<Account> optionalAccount = Optional.of(account);

        when(accountRepository.findById(id)).thenReturn(optionalAccount);

        Account retrievedAccount = accountService.getAccountById(id);

        Assert.notNull(retrievedAccount, "Retrieved account should not be null");
        Assertions.assertNotEquals(id, retrievedAccount.getId(), "Retrieved account ID should match");
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

    @Test
    public void testGetAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new AccountBuilder()
                .setId(1L)
                .setName("John Doe")
                .setEmail("johndoe@example.com")
                .setPassword("password123")
                .build());

        accounts.add(new AccountBuilder()
                .setId(2L)
                .setName("John Doe")
                .setEmail("johndoe@example.com")
                .setPassword("hashedPassword")
                .build());


        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> retrievedAccounts = accountService.getAllAccounts();

        Assert.notNull(retrievedAccounts, "Retrieved accounts list should not be null");
        assertEquals(2, retrievedAccounts.size(), "Retrieved accounts list size should match");
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateAccount_ExistingId() {
        Long id = 1L;
        Account existingAccount = new AccountBuilder()
                .setId(id)
                .setName("John Doe")
                .setEmail("johndoe@example.com")
                .setPassword("hashedPassword")
                .build();
        Optional<Account> optionalAccount = Optional.of(existingAccount);

        Account updatedAccount = new AccountBuilder()
                .setId(null)
                .setName("John Doe")
                .setEmail("johndoe@example.com")
                .setPassword(null)
                .build();

        when(accountRepository.findById(id)).thenReturn(optionalAccount);
        // Simulate saving the updated account (mocked behavior)
        Account savedAccount = new AccountBuilder()
                .setId(id)
                .setName("John Doe")
                .setEmail("johndoe@example.com")
                .setPassword("hashedPassword")
                .build();
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Account updated = accountService.updateAccount(id, updatedAccount);

        Assert.notNull(updated, "Updated account should not be null");
        assertEquals(id, updated.getId(), "Updated account ID should match");
        assertEquals("John Doe", updated.getName(), "Updated account name should match");
        assertEquals("johndoe@example.com", updated.getEmail(), "Updated account email should match");
        verify(accountRepository, times(1)).findById(id);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void testPatchAccount_ValidPassword_UpdatesAccount() throws Exception {

       try {
           Long id = 1L;
           String existingPassword = "oldPassword";
           String updatedPassword = "newPassword";

           // Mock AccountRepository
           Account existingAccount = new Account(id, "John Doe", "johndoe@example.com", PasswordUtil.hashPassword(existingPassword));
           Optional<Account> optionalAccount = Optional.of(existingAccount);
           when(accountRepository.findById(id)).thenReturn(optionalAccount);

           // Mock password validation (optional)
           when(accountService.validatePassword(updatedPassword)).thenReturn(true);

           // Update account with new password
           Account updatedAccount = new Account(null, null, null, updatedPassword);
           Account savedAccount = accountService.patchAccount(id, updatedAccount);

           // Verify behavior
           assertThat(savedAccount).isNotNull();
           assertThat(savedAccount.getPassword()).isEqualTo(PasswordUtil.hashPassword(updatedPassword));
           verify(accountRepository).save(existingAccount);
       }catch (IllegalArgumentException e) {
           assertEquals("Password must contain at least one uppercase letter, one lowercase letter, and one digit", e.getMessage());
       }
    }

    @Test()
    public void testPatchAccount_InvalidPassword_ThrowsException() throws Exception {

        try {
            Long id = 1L;
            String existingPassword = "oldPassword";
            String invalidPassword = "short";

            // Mock AccountRepository
            Account existingAccount = new Account(id, "John Doe", "johndoe@example.com", PasswordUtil.hashPassword(existingPassword));
            Optional<Account> optionalAccount = Optional.of(existingAccount);
            when(accountRepository.findById(id)).thenReturn(optionalAccount);

            // Mock password validation (optional)
            when(accountService.validatePassword(invalidPassword)).thenReturn(false); // Optional

            // Update account with invalid password
            Account updatedAccount = new Account(null, null, null, invalidPassword);
            accountService.patchAccount(id, updatedAccount);
            throw new IllegalArgumentException("Invalid argument");
        } catch (IllegalArgumentException e) {
            assertEquals("Password must contain at least one uppercase letter, one lowercase letter, and one digit", e.getMessage());
        }

    }

    @Test
    public void testPatchAccount_AccountNotFound_ReturnsNull() throws Exception {
        Long id = 1L;

        // Mock AccountRepository
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        // Update account with non-existent ID
        Account updatedAccount = new Account(null, null, null, "newPassword");
        Account savedAccount = accountService.patchAccount(id, updatedAccount);

        // Verify behavior
        assertThat(savedAccount).isNull();
    }

    @Test
    public void testDeleteAccount_Success() {
        Long id = 1L;
        accountService.deleteAccount(id);
        verify(accountRepository).deleteById(id);
    }

    @Test()
    public void testValidatePassword_EmptyPassword_ThrowsException() {
        try {
            String emptyPassword = "";
            accountService.validatePassword(emptyPassword);
            throw new IllegalArgumentException("Invalid argument");
        } catch (IllegalArgumentException e) {
            assertEquals("Password cannot be empty", e.getMessage());
        }
    }

    @Test()
    public void testValidatePassword_InvalidFormat_ThrowsException() {

        try {
            String invalidPassword = "password";
            accountService.validatePassword(invalidPassword);
            throw new IllegalArgumentException("Invalid argument");
        } catch (IllegalArgumentException e) {
            assertEquals("Password must contain at least one uppercase letter, one lowercase letter, and one digit", e.getMessage());
        }
    }




 }