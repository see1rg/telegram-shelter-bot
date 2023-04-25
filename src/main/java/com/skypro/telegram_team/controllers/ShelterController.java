package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Shelter;
import com.skypro.telegram_team.services.ShelterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/shelters")
public class ShelterController {
    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @Operation(summary = "Поиск приюта по id", tags = "Shelters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Приют найден по id", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Shelter.class))
            })
    })
    @GetMapping("/{id}")
    public Shelter findById(@Parameter(description = "Id приюта") @PathVariable Long id) {
        return shelterService.findById(id);
    }

    @Operation(summary = "Получение списка всех приютов", tags = "Shelters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список приютов", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(
                            schema = @Schema(implementation = Shelter.class)))
            })
    })
    @GetMapping
    public Collection<Shelter> findAll() {
        return shelterService.findAll();
    }

    @Operation(summary = "Создание приюта", tags = "Shelters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Приют создан", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Shelter.class))
            })
    })
    @PostMapping
    public Shelter create(@Parameter(description = "Данные приюта") @RequestBody Shelter shelter, @RequestParam("type") Animal.TypeAnimal typeAnimal) {
        return shelterService.create(shelter, typeAnimal);
    }

    @Operation(summary = "Обновление данных приюта", tags = "Shelters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные обновлены", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Shelter.class))
            })
    })
    @PutMapping("/{id}")
    public Shelter update(@Parameter(description = "Данные приюта") @RequestBody Shelter shelter,
                          @Parameter(description = "Id приюта") @PathVariable Long id) {
        return shelterService.update(shelter, id);
    }

    @Operation(summary = "Удаление приюта по id", tags = "Shelters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Приют удален", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Shelter.class))
            })
    })
    @DeleteMapping("/{id}")
    public Shelter delete(@Parameter(description = "Id приюта") @PathVariable Long id) {
        return shelterService.delete(id);
    }

    @Operation(summary = "Присвоить животных соответствующему приюту", tags = "Shelters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Животное присвоено", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Shelter.class))
            })
    })
    @PutMapping
    public void assignAnimalsToShelters(@RequestParam("shelterId") Long shelterId, @RequestParam("animalId") Long animalId) {
        shelterService.assignAnimalsToShelters(shelterId, animalId);
    }
}
