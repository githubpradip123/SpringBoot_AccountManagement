package com.account.demo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class PasswordUtil {

    public static String hashPassword(String password) {
        // Use a BCryptPasswordEncoder to securely hash passwords
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}