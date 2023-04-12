package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.services.JoinAnimalAndUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/join_animal_user")
public class JoinAnimalAndUserController {

    private final JoinAnimalAndUserService joinAnimalAndUserService;

    public JoinAnimalAndUserController(JoinAnimalAndUserService joinAnimalAndUserService) {
        this.joinAnimalAndUserService = joinAnimalAndUserService;
    }

    @Operation(summary = "Связывание собаки и усыновителя.", tags = " Join animal and user" )
    @PostMapping()
    public void joinAnimalAndUser(@RequestParam("animalId") long animalId, @RequestParam("userId") long userId) {
        log.info("Joining animal and user with animal id: " + animalId + " and user id: " + userId);
        joinAnimalAndUserService.joinAnimalAndUser(animalId, userId);
    }
}
