package com.example.battlesurgebackend.dto;

import com.example.battlesurgebackend.model.Card;
import com.example.battlesurgebackend.model.Game;
import lombok.Data;

import java.util.List;

@Data
public class GameStateDTO {
    private Game game;
    private List<Card> playerOneCards;
    private List<Card> playerTwoCards;

    public GameStateDTO(Game game, List<Card> playerOneCards, List<Card> playerTwoCards) {
        this.game = game;
        this.playerOneCards = playerOneCards;
        this.playerTwoCards = playerTwoCards;
    }
}
