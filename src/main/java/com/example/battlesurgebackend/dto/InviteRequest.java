package com.example.battlesurgebackend.dto;

import lombok.Data;

@Data
public class InviteRequest {

    private Long inviterId;
    private Long inviteeId;

    public InviteRequest() {
    }


}
