package com.sidney.banking.account.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class AccountNumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generate() {

        int number = 100000 + RANDOM.nextInt(900000);
        int digit = RANDOM.nextInt(10);

        return number + "-" + digit;
    }
}
