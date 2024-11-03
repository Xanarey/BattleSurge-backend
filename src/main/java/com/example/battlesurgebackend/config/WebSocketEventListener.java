package com.example.battlesurgebackend.config;

import com.example.battlesurgebackend.model.Account;
import com.example.battlesurgebackend.model.User;
import com.example.battlesurgebackend.services.AccountService;
import com.example.battlesurgebackend.services.UserService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final AccountService accountService;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate, UserService userService, AccountService accountService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.accountService = accountService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("Headers received: " + headerAccessor.toNativeHeaderMap());

        String userEmail = headerAccessor.getFirstNativeHeader("email");

        if (userEmail != null) {
            Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("email", userEmail);
            System.out.println("User connected: " + userEmail);
        } else {
            System.out.println("No email found in connect headers.");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userEmail = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("email");

        if (userEmail != null) {
            System.out.println("User disconnected: " + userEmail);
            Account account = accountService.findAccountByEmail(userEmail);

            if (account != null) {
                User user = account.getUser();
                user.setOnlineStatus(false);
                userService.updateUser(user);

                messagingTemplate.convertAndSend("/topic/userStatus", user);
            }
        } else {
            System.out.println("No email found in session attributes during disconnect.");
        }
    }



}
