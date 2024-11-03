package com.example.battlesurgebackend.controller;

import com.example.battlesurgebackend.dto.DeclineInviteRequest;
import com.example.battlesurgebackend.dto.DeclineNotification;
import com.example.battlesurgebackend.dto.InviteRequest;
import com.example.battlesurgebackend.model.Game;
import com.example.battlesurgebackend.model.User;
import com.example.battlesurgebackend.services.GameService;
import com.example.battlesurgebackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/invites")
public class InviteController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final GameService gameService;

    @Autowired
    public InviteController(SimpMessagingTemplate messagingTemplate, UserService userService, GameService gameService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<String> sendInvite(@RequestBody InviteRequest inviteRequest) {
        Long inviterId = inviteRequest.getInviterId();
        Long inviteeId = inviteRequest.getInviteeId();

        User inviter = userService.getUserById(inviterId);
        String inviterName = inviter.getUsername();

        Map<String, Object> inviteMessage = new HashMap<>();
        inviteMessage.put("inviterId", inviterId);
        inviteMessage.put("inviteeId", inviteeId);
        inviteMessage.put("inviterName", inviterName);

        messagingTemplate.convertAndSendToUser(inviteeId.toString(), "/queue/invite", inviteMessage);

        return ResponseEntity.ok("Invite sent");
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptInvite(@RequestBody InviteRequest inviteRequest) {
        User inviter = userService.getUserById(inviteRequest.getInviterId());
        User invitee = userService.getUserById(inviteRequest.getInviteeId());

        Game game = gameService.startGame(inviter, invitee);

        Map<String, Object> battleStartMessageForInviter = new HashMap<>();
        battleStartMessageForInviter.put("battleId", game.getBattleId());
        battleStartMessageForInviter.put("opponentId", invitee.getId());
        battleStartMessageForInviter.put("opponentName", invitee.getUsername());

        Map<String, Object> battleStartMessageForInvitee = new HashMap<>();
        battleStartMessageForInvitee.put("battleId", game.getBattleId());
        battleStartMessageForInvitee.put("opponentId", inviter.getId());
        battleStartMessageForInvitee.put("opponentName", inviter.getUsername());


        messagingTemplate.convertAndSendToUser(
                inviter.getId().toString(), "/queue/startBattle", battleStartMessageForInviter);

        messagingTemplate.convertAndSendToUser(
                invitee.getId().toString(), "/queue/startBattle", battleStartMessageForInvitee);


        System.out.println("GAME START, battleId : " + game.getBattleId());
        return ResponseEntity.ok("Battle started");
    }

    @PostMapping("/decline")
    public ResponseEntity<Void> declineInvite(@RequestBody DeclineInviteRequest request) {
        Long inviterId = request.getInviterId();
        Long inviteeId = request.getInviteeId();

        String message = userService.getUserById(inviteeId).getUsername() + " отклонил ваше приглашение.";

        messagingTemplate.convertAndSendToUser(inviterId.toString(), "/queue/declineNotification", new DeclineNotification(message));

        return ResponseEntity.ok().build();
    }
}
