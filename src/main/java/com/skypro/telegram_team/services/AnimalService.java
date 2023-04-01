package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.repositories.AnimalRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

@Log4j
@Service
public class AnimalService {
    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public void save(Animal animal) {
        log.info("Saving animal: " + animal);
        animalRepository.save(animal);
    }

    public Animal findById(Long id) {
        log.info("Finding animal by id: " + id);
        return animalRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        log.info("Deleting animal by id: " + id);
        animalRepository.deleteById(id);
    }

    public Animal findByName(String name) {
        log.info("Finding animal by name: " + name);
        return animalRepository.findByName(name).orElse(null);
    }


}
