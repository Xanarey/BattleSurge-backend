package com.example.battlesurgebackend.controller;

import com.example.battlesurgebackend.model.Card;
import com.example.battlesurgebackend.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {

    private final CardService cardService;

    @Autowired
    public GameController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/cards/{userId}")
    public ResponseEntity<List<Card>> getUserCards(@PathVariable Long userId) {
        List<Card> cards = cardService.getAllUserCards(userId);
        return ResponseEntity.ok(cards);
    }


}
