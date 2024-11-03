package com.example.battlesurgebackend.services;

import com.example.battlesurgebackend.dto.GameStateDTO;
import com.example.battlesurgebackend.model.Card;
import com.example.battlesurgebackend.model.Game;
import com.example.battlesurgebackend.model.GameStatus;
import com.example.battlesurgebackend.model.User;
import com.example.battlesurgebackend.repositories.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Service
public class GameService {

    private final GameRepository gameRepository;
    private final CardService cardService;

    @Autowired
    public GameService(GameRepository gameRepository, CardService cardService) {
        this.gameRepository = gameRepository;
        this.cardService = cardService;
    }

    public Game getGameByBattleId(UUID id) {
        return gameRepository.findByBattleId(id);
    }

    public Game startGame(User playerOne, User playerTwo) {
        Game game = new Game();
        game.setPlayerOne(playerOne);
        game.setPlayerTwo(playerTwo);
        game.setBattleId(UUID.randomUUID());
        game.setStatus(GameStatus.ACTIVE);
        game.setStartTime(LocalDateTime.now());
        game.setCurrentTurn(new Random().nextInt(2) + 1);

        gameRepository.save(game);

        return game;
    }

    public GameStateDTO getGameState(UUID battleId) {
        Game game = gameRepository.findByBattleId(battleId);

        if (game == null) {
            throw new RuntimeException("Game not found");
        }

        List<Card> playerOneCards = cardService.getAllUserCards(game.getPlayerOne().getId());
        List<Card> playerTwoCards = cardService.getAllUserCards(game.getPlayerTwo().getId());

        System.out.println("TEST playerOneCards" + playerOneCards);
        System.out.println("TEST playerTwoCards" + playerTwoCards);

        return new GameStateDTO(game, playerOneCards, playerTwoCards);
    }



    @Transactional
    public GameStateDTO processAttack(UUID battleId, Long attackerCardId, Long defenderCardId) {
        System.out.println("Attacker Card ID: " + attackerCardId + ", Defender Card ID: " + defenderCardId);

        if (attackerCardId == null || defenderCardId == null) {
            System.err.println("Attacker or defender card ID is null.");
            throw new IllegalArgumentException("Invalid cards for attack");
        }

        Game game = gameRepository.findByBattleId(battleId);
        if (game == null) {
            System.err.println("Game not found for battleId: " + battleId);
            throw new RuntimeException("Game not found");
        }

        System.out.println("Attacker Card ID: " + attackerCardId + ", Defender Card ID: " + defenderCardId);

        Card attackerCard = cardService.getCardById(attackerCardId);
        Card defenderCard = cardService.getCardById(defenderCardId);
        if (attackerCard == null || defenderCard == null) {
            System.err.println("One or both cards not found.");
            throw new IllegalArgumentException("Invalid cards for attack");
        }

        defenderCard.setHealth(defenderCard.getHealth() - attackerCard.getAttack());
        attackerCard.setMana(attackerCard.getMana() - attackerCard.getAttack());

        if (defenderCard.getHealth() <= 0) {
            defenderCard.setAlive(false);
        }

        cardService.saveCard(attackerCard);
        cardService.saveCard(defenderCard);



        List<Card> playerOneCards = cardService.getAllUserCards(game.getPlayerOne().getId());
        List<Card> playerTwoCards = cardService.getAllUserCards(game.getPlayerTwo().getId());

        System.out.println("Returning updated GameStateDTO");

        game.setCurrentTurn(game.getCurrentTurn() == 1 ? 2 : 1);
        gameRepository.save(game);

        return new GameStateDTO(game, playerOneCards, playerTwoCards);
    }

}
