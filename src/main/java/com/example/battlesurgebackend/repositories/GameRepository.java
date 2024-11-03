package com.example.battlesurgebackend.repositories;

import com.example.battlesurgebackend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, Long> {
    Game findByBattleId(UUID battleId);
}
