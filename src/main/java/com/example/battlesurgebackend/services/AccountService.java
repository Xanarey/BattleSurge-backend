package com.example.battlesurgebackend.services;

import com.example.battlesurgebackend.model.Account;
import com.example.battlesurgebackend.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> login(String email, String password) {
        return accountRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password));
    }
}
