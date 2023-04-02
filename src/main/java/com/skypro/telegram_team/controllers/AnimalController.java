package com.skypro.telegram_team.controllers;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.services.AnimalService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/animals")
public class AnimalController {
    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping("/{id}")
    public Animal getAnimal(@PathVariable long id) {
        return animalService.findById(id);
    }

    @GetMapping
    public Iterable<Animal> getAllAnimals() {
        return animalService.findAll();
    }

    @DeleteMapping("/{id}")
    public Animal deleteAnimal(@PathVariable long id) {
        return animalService.deleteById(id);
    }

    @PostMapping
    public Animal createAnimal(@RequestBody Animal animal) {
        return animalService.save(animal);
    }

    @PutMapping("/{id}")
    public Animal updateAnimal(@RequestBody Animal animal, @PathVariable Long id) {
        return animalService.update(animal, id);
    }
}
