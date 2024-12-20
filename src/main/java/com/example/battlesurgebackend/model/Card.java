package com.example.battlesurgebackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cards")
@Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int health;
    private int attack;
    private int mana;
    private boolean alive = true;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
}
