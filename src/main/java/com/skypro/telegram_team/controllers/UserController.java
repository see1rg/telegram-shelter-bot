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

    @Operation(summary = "Поиск пользователя по id", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Поиск пользователя по id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            })
    }
    )
    @GetMapping("/{id}")
    public User findUserById(@Parameter(description = "Id пользователя") @PathVariable long id) {
        return userService.findById(id);
    }

    @Operation(summary = "Получение списка всех пользователей", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получение списка всех пользователей", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            })
    })
    @GetMapping
    public Iterable<User> getAllUsers() {
        return userService.findAll();
    }

    @Operation(summary = "Удаление пользователя по id", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Удаление пользователя по id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            })
    })
    @DeleteMapping("/{id}")
    public User deleteUserById(@PathVariable long id) {
        return userService.deleteById(id);
    }

    @Operation(summary = "Создание пользователя", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Создание пользователя", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            })
    })
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

    @Operation(summary = "Обновление данных пользователя", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обновление данных пользователя", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            })
    })
    @PutMapping("/{id}")
    public User updateUser(@RequestBody User user, @PathVariable Long id) {
        return userService.update(user, id);
    }

    @Operation(summary = "Назначить пользователя волонтером", tags = "Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные обновлены", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            })
    })
    @PatchMapping("/{id}/volunteer")
    public User userIsVolunteer(@PathVariable Long id, @RequestParam("isVolunteer") Boolean isVolunteer) {
        return userService.userIsVolunteer(id, isVolunteer);
    }

    @Operation(summary = "Связывание собаки и усыновителя.", tags = "Users" )
    @PostMapping("/join")
    public void joinAnimalAndUser(@RequestParam("animalId") long animalId, @RequestParam("userId") long userId) {
        userService.joinAnimalAndUser(animalId, userId);
    }
}
