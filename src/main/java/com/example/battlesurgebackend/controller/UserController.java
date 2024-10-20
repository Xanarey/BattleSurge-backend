package com.example.battlesurgebackend.controller;

import com.example.battlesurgebackend.model.User;
import com.example.battlesurgebackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/game")
public class UserController {

    @Autowired
    private UserService userService;

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


}
