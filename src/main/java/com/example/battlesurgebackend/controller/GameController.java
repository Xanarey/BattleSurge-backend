package com.example.battlesurgebackend.controller;

import com.example.battlesurgebackend.dto.AttackRequest;
import com.example.battlesurgebackend.dto.GameStateDTO;
import com.example.battlesurgebackend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public GameController(GameService gameService, SimpMessagingTemplate simpMessagingTemplate) {
        this.gameService = gameService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @GetMapping("/{battleId}")
    public ResponseEntity<GameStateDTO> getGameState(@PathVariable UUID battleId) {
        GameStateDTO gameState = gameService.getGameState(battleId);
        System.out.println("Returning GameStateDTO: " + gameState);
        return ResponseEntity.ok(gameState);
    }


    @PostMapping("/{battleId}/attack")
    public GameStateDTO processAttack(@PathVariable UUID battleId, @RequestBody AttackRequest attackRequest) {
        Long attackerCardId = attackRequest.getAttackerCardId();
        Long defenderCardId = attackRequest.getDefenderCardId();

        GameStateDTO gameStateDTO = gameService.processAttack(battleId, attackerCardId, defenderCardId);

        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + battleId, gameStateDTO);

        return gameStateDTO;
    }
}
