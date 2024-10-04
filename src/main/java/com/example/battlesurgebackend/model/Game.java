package com.example.battlesurgebackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Player playerOne;

    @ManyToOne
    private Player playerTwo;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    private int currentTurn = 1;

}
