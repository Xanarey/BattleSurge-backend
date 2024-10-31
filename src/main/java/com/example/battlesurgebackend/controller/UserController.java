package com.example.battlesurgebackend.controller;

import com.example.battlesurgebackend.dto.DeclineInviteRequest;
import com.example.battlesurgebackend.dto.DeclineNotification;
import com.example.battlesurgebackend.dto.InviteRequest;
import com.example.battlesurgebackend.model.User;
import com.example.battlesurgebackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/game")
public class UserController {


    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry simpUserRegistry;

    @Autowired
    public UserController(UserService userService, SimpMessagingTemplate messagingTemplate, SimpUserRegistry simpUserRegistry) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.simpUserRegistry = simpUserRegistry;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/usersListFight")
    public List<User> getAllUsers(@RequestParam("currentEmail") String currentEmail) {
        List<User> list = userService.getAllUsers();
        list.forEach(user -> System.out.println("User in DB: " + user.getAccount().getEmail()));

        List<User> filteredList = list.stream()
                .filter(user -> !user.getAccount().getEmail().trim().equalsIgnoreCase(currentEmail.trim()))
                .collect(Collectors.toList());

        filteredList.forEach(user -> System.out.println("User after filtering: " + user.getAccount().getEmail()));
        return filteredList;
    }

    @PostMapping("/invite")
    public ResponseEntity<String> invitePlayer(@RequestBody InviteRequest inviteRequest) {
        Long inviterId = inviteRequest.getInviterId();
        Long inviteeId = inviteRequest.getInviteeId();

        User inviter = userService.getUserById(inviterId);
        String inviterName = inviter.getUsername();

        System.out.println("ПРОВЕРКА : " + inviterName);

        // Создаем объект приглашения, который будет отправлен через WebSocket
        Map<String, Object> inviteMessage = new HashMap<>();
        inviteMessage.put("inviterId", inviterId);
        inviteMessage.put("inviteeId", inviteeId);
        inviteMessage.put("inviterName", inviterName);
        // Здесь можно добавить дополнительную информацию, например, имя приглашающего

        // Отправляем сообщение на канал /topic/invite
        messagingTemplate.convertAndSend("/topic/invite", inviteMessage);

        messagingTemplate.convertAndSendToUser(
                inviteeId.toString(), "/queue/invite", inviteMessage);

        System.out.println(inviterId + " пригласил " + inviteeId);

        return ResponseEntity.ok("Invite sent");
    }

    @PostMapping("/acceptInvite")
    public ResponseEntity<String> acceptInvite(@RequestBody InviteRequest inviteRequest) {
        Long inviterId = inviteRequest.getInviterId();
        Long inviteeId = inviteRequest.getInviteeId();
        String battleId = UUID.randomUUID().toString();

        // Получаем имена игроков
        String inviterName = userService.getUserById(inviterId).getUsername();
        String inviteeName = userService.getUserById(inviteeId).getUsername();

        // Сообщение для приглашающего
        Map<String, Object> battleStartMessageForInviter = new HashMap<>();
        battleStartMessageForInviter.put("battleId", battleId);
        battleStartMessageForInviter.put("opponentId", inviteeId);
        battleStartMessageForInviter.put("opponentName", inviteeName);

        // Сообщение для приглашённого
        Map<String, Object> battleStartMessageForInvitee = new HashMap<>();
        battleStartMessageForInvitee.put("battleId", battleId);
        battleStartMessageForInvitee.put("opponentId", inviterId);
        battleStartMessageForInvitee.put("opponentName", inviterName);

        // Отправляем сообщения обоим игрокам с нужной информацией
        messagingTemplate.convertAndSendToUser(
                inviterId.toString(), "/queue/startBattle", battleStartMessageForInviter);

        messagingTemplate.convertAndSendToUser(
                inviteeId.toString(), "/queue/startBattle", battleStartMessageForInvitee);

        return ResponseEntity.ok("Battle started");
    }



    @PostMapping("/declineInvite")
    public ResponseEntity<Void> declineInvite(@RequestBody DeclineInviteRequest request) {
        String inviterIdStr = request.getInviterId().toString();

        System.out.println("Attempting to send decline notification to user with ID: " + inviterIdStr);

        SimpUser user = simpUserRegistry.getUser(inviterIdStr);
        if (user != null) {
            messagingTemplate.convertAndSendToUser(
                    inviterIdStr,
                    "/queue/declineNotification",
                    new DeclineNotification(userService.getUserById(request.getInviteeId()).getUsername() + " отклонил ваше приглашение.")
            );
            System.out.println("Decline notification sent to user with ID: " + inviterIdStr);
        } else {
            System.out.println("User with ID " + inviterIdStr + " is not connected.");
        }

        return ResponseEntity.ok().build();
    }





}
