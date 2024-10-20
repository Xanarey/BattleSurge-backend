package com.example.battlesurgebackend.controller;

import com.example.battlesurgebackend.model.User;
import com.example.battlesurgebackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public List<User> getAllUsers(@RequestParam("currentUserName") String currentUserName) {
        // Выводим переданное имя для отладки
        System.out.println("Current User Name from frontend: " + currentUserName);

        List<User> list = userService.getAllUsers();

        // Выводим всех пользователей для отладки
        list.forEach(user -> System.out.println("User in DB: " + user.getUsername()));

        // Фильтруем текущего пользователя
        List<User> filteredList = list.stream()
                .filter(user -> !user.getUsername().trim().equalsIgnoreCase(currentUserName.trim()))
                .collect(Collectors.toList());

        // Выводим результат фильтрации
        filteredList.forEach(user -> System.out.println("User after filtering: " + user.getUsername()));

        return filteredList;
    }





}
