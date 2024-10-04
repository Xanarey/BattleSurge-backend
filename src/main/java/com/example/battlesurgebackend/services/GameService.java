package com.example.battlesurgebackend.services;

import com.example.battlesurgebackend.model.Card;
import com.example.battlesurgebackend.model.Game;
import com.example.battlesurgebackend.model.GameStatus;
import com.example.battlesurgebackend.model.Player;
import com.example.battlesurgebackend.repositories.CardRepository;
import com.example.battlesurgebackend.repositories.GameRepository;
import com.example.battlesurgebackend.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final CardRepository cardRepository;

    @Autowired
    public GameService(GameRepository gameRepository, PlayerRepository playerRepository, CardRepository cardRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.cardRepository = cardRepository;
    }

    // Логика создания игры с раздачей карт
    public Game startGame(Long playerOneId, Long playerTwoId) {
        Player playerOne = playerRepository.findById(playerOneId).orElseThrow();
        Player playerTwo = playerRepository.findById(playerTwoId).orElseThrow();

        // Получаем все доступные карты
        List<Card> allCards = cardRepository.findAll();

        // Раздаем по 3 случайные карты каждому игроку
        List<Card> playerOneCards = getRandomCards(allCards);
        List<Card> playerTwoCards = getRandomCards(allCards);

        playerOne.setCards(playerOneCards);
        playerTwo.setCards(playerTwoCards);

        // Сохраняем изменения для игроков
        playerRepository.save(playerOne);
        playerRepository.save(playerTwo);

        Game game = new Game();
        game.setPlayerOne(playerOne);
        game.setPlayerTwo(playerTwo);
        game.setStatus(GameStatus.valueOf("IN_PROGRESS"));

        return gameRepository.save(game);
    }

    // Метод для выбора случайных карт
    private List<Card> getRandomCards(List<Card> allCards) {
        Collections.shuffle(allCards);
        return allCards.stream()
                .limit(3)
                .collect(Collectors.toList());
    }

    public Game performAttack(Long gameId, Long attackerId, Long cardId) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        Player attacker = playerRepository.findById(attackerId).orElseThrow();
        Player opponent = (attackerId.equals(game.getPlayerOne().getId())) ? game.getPlayerTwo() : game.getPlayerOne();

        // Находим карту, которую использует атакующий
        Card attackCard = attacker.getCards().stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        // Уменьшаем здоровье соперника на силу карты
        opponent.setHealth(opponent.getHealth() - attackCard.getPower());

        // Проверяем, закончилась ли игра (если здоровье соперника <= 0)
        if (opponent.getHealth() <= 0) {
            game.setStatus(GameStatus.valueOf("COMPLETED"));
            gameRepository.save(game);
            return game;
        }

        // Меняем ход
        game.setCurrentTurn((game.getCurrentTurn() == 1) ? 2 : 1);
        playerRepository.save(opponent);
        return gameRepository.save(game);
    }
}
