package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @Operation(summary = "Поиск пользователя по id", description = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Поиск пользователя по id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            })
    }
    )
    @GetMapping("/{id}")
    public User getUser(@Parameter(description = "Id пользователя") @PathVariable long id) {
        return userService.findById(id);
    }

    @Operation(summary = "Получение списка всех пользователей", description = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получение списка всех пользователей", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            })
    })
    @GetMapping
    public Iterable<User> getAllUsers() {
        return userService.findAll();
    }

    @Operation(summary = "Удаление пользователя по id", description = "Delete user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Удаление пользователя по id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            })
    })
    @DeleteMapping("/{id}")
    public User deleteUser(@PathVariable long id) {
        return userService.deleteById(id);
    }

    @Operation(summary = "Создание пользователя", description = "Create user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Создание пользователя", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            })
    })
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

    @Operation(summary = "Обновление данных пользователя", description = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обновление данных пользователя", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            })
    })
    @PutMapping("/{id}")
    public User updateUser(@RequestBody User user, @PathVariable Long id) {
        return userService.update(user,id);
    }
}
