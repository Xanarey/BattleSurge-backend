package com.example.battlesurgebackend.controller;

import com.example.battlesurgebackend.model.Card;
import com.example.battlesurgebackend.model.User;
import com.example.battlesurgebackend.services.CardService;
import com.example.battlesurgebackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CardService cardService;

    @Autowired
    public UserController(UserService userService, CardService cardService) {
        this.userService = userService;
        this.cardService = cardService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(value="excludeEmail", required=false) String excludeEmail) {
        List<User> users = userService.getAllUsers();

        if (excludeEmail != null && !excludeEmail.trim().isEmpty()) {
            users = users.stream()
                    .filter(user -> !user.getAccount().getEmail().equalsIgnoreCase(excludeEmail.trim()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}/cards")
    public ResponseEntity<List<Card>> getUserCards(@PathVariable Long userId) {
        List<Card> cards = cardService.getAllUserCards(userId);
        return ResponseEntity.ok(cards);
    }
}
