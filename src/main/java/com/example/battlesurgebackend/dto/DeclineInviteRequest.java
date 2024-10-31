package com.example.battlesurgebackend.dto;

import lombok.Data;

@Data
public class DeclineInviteRequest {
    private Long inviterId;
    private Long inviteeId;
}
