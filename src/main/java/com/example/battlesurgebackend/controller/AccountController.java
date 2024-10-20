package com.example.battlesurgebackend.controller;

import com.example.battlesurgebackend.dto.AccountRequest;
import com.example.battlesurgebackend.model.Account;
import com.example.battlesurgebackend.model.User;
import com.example.battlesurgebackend.services.AccountService;
import com.example.battlesurgebackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public AccountController(AccountService accountService, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.accountService = accountService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody AccountRequest loginRequest) {
        Optional<Account> account = accountService.login(loginRequest.getEmail(), loginRequest.getPassword());

        if (account.isPresent()) {
            User user = userService.getUserById(account.get().getId());
            user.setOnlineStatus(true);
            userService.updateUser(user);

            messagingTemplate.convertAndSend("/topic/userStatus", user);

            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody AccountRequest logoutRequest) {
        Optional<Account> optionalAccount = Optional.ofNullable(accountService.findAccountByEmail(logoutRequest.getEmail()));

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            User user = userService.getUserById(account.getId());

            if (user != null) {
                user.setOnlineStatus(false);
                userService.updateUser(user);

                messagingTemplate.convertAndSend("/topic/userStatus", user);

                return ResponseEntity.ok("User logged out successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
        }
    }




}
