package com.example.battlesurgebackend.controller;

import com.example.battlesurgebackend.dto.AccountRequest;
import com.example.battlesurgebackend.model.Account;
import com.example.battlesurgebackend.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AccountRequest loginRequest) {
        Optional<Account> account = accountService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return account.map(value -> ResponseEntity.ok(value.getEmail())).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }

}
