package com.example.battlesurgebackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User owner;
}
