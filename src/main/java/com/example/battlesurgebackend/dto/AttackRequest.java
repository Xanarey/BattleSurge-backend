package com.example.battlesurgebackend.dto;

import lombok.Data;

@Data
public class AttackRequest {
    private Long attackerCardId;
    private Long defenderCardId;
}
