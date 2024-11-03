package com.example.battlesurgebackend.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "game")
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID battleId;

    private int currentTurn;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    private LocalDateTime startTime;

    @ManyToOne
    @JoinColumn(name = "player_one_id")
    private User playerOne;

    @ManyToOne
    @JoinColumn(name = "player_two_id")
    private User playerTwo;


}
