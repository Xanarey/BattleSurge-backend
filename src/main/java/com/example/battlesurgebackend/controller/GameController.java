package com.example.battlesurgebackend.controller;

import com.example.battlesurgebackend.model.Card;
import com.example.battlesurgebackend.model.Game;
import com.example.battlesurgebackend.model.GameStatus;
import com.example.battlesurgebackend.model.Player;
import com.example.battlesurgebackend.repositories.GameRepository;
import com.example.battlesurgebackend.repositories.PlayerRepository;
import com.example.battlesurgebackend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public GameController(GameService gameService, GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameService = gameService;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }


    @PostMapping("/start")
    public Game startGame(@RequestParam Long playerOneId, @RequestParam Long playerTwoId) {
        Player playerOne = playerRepository.findById(playerOneId).orElseThrow();
        Player playerTwo = playerRepository.findById(playerTwoId).orElseThrow();
        Game game = new Game();
        game.setPlayerOne(playerOne);
        game.setPlayerTwo(playerTwo);
        game.setStatus(GameStatus.IN_PROGRESS);
        return gameRepository.save(game);
    }

    @PostMapping("/{gameId}/attack")
    public Game performAttack(
            @PathVariable Long gameId,
            @RequestParam Long attackerId,
            @RequestParam Long cardId) {
        return gameService.performAttack(gameId, attackerId, cardId);
    }

    // Сделать ход
    @PostMapping("/move")
    public String makeMove(@RequestParam Long gameId, @RequestParam Long playerId, @RequestParam Long cardId) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        Player currentPlayer = playerRepository.findById(playerId).orElseThrow();
        Card card = currentPlayer.getCards().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow();

        Player opponent = game.getCurrentTurn() == 1 ? game.getPlayerTwo() : game.getPlayerOne();

        opponent.setHealth(opponent.getHealth() - card.getPower());


        if (opponent.getHealth() <= 0) {
            game.setStatus(game.getCurrentTurn() == 1 ? GameStatus.PLAYER_ONE_WON : GameStatus.PLAYER_TWO_WON);
        }


        game.setCurrentTurn(game.getCurrentTurn() == 1 ? 2 : 1);

        gameRepository.save(game);
        return "Move made! Current opponent health: " + opponent.getHealth();
    }

    @GetMapping("/{gameId}")
    public Game getGameState(@PathVariable Long gameId) {
        return gameRepository.findById(gameId).orElseThrow();
    }
}
