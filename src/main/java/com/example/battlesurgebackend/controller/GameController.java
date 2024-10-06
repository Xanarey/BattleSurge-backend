package com.example.battlesurgebackend.controller;

import com.example.battlesurgebackend.model.Card;
import com.example.battlesurgebackend.services.CardService;
import com.example.battlesurgebackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {

    private final CardService cardService;
    private final UserService userService;

    @Autowired
    public GameController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

    @GetMapping("/cards/{userId}")
    public ResponseEntity<List<Card>> getUserCards(@PathVariable Long userId) {
        List<Card> cards = cardService.getAllUserCards(userId);
        return ResponseEntity.ok(cards);
    }

}
