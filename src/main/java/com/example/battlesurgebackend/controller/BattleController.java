package com.example.battlesurgebackend.controller;

import com.example.battlesurgebackend.dto.AttackRequest;
import com.example.battlesurgebackend.model.Card;
import com.example.battlesurgebackend.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/game")
public class BattleController {

    @Autowired
    private CardService cardService;

    @PostMapping("/attack")
    public ResponseEntity<Map<String, Card>> handleAttack(@RequestBody AttackRequest attackRequest) {
        // Получаем карты атакующего и защищающегося
        Card attacker = cardService.getCardById(attackRequest.getAttackerCardId());
        Card defender = cardService.getCardById(attackRequest.getDefenderCardId());

        // Обрабатываем логику атаки
        cardService.attack(attacker, defender);

        // Возвращаем обновлённые данные карт
        Map<String, Card> response = new HashMap<>();
        response.put("attackerCard", attacker);
        response.put("defenderCard", defender);

        return ResponseEntity.ok(response);
    }
}
