package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        return userService.findById(id);
    }

    @GetMapping
    public Iterable<User> getAllUsers() {
        return userService.findAll();
    }

    @DeleteMapping("/{id}")
    public User deleteUser(@PathVariable long id) {
        return userService.deleteById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@RequestBody User user, @PathVariable Long id) {
        return userService.update(user,id);
    }
}
