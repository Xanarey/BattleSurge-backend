package com.example.battlesurgebackend.dto;

import lombok.Data;

@Data
public class DeclineNotification {
    private String message;

    public DeclineNotification(String message) {
        this.message = message;
    }
}
