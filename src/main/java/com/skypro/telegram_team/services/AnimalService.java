package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.repositories.AnimalRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Log4j
@Service
public class AnimalService {
    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }
    @Transactional
    public Animal save(Animal animal) {
        log.info("Saving animal: " + animal);
        return animalRepository.save(animal);
    }

    public Animal findById(Long id) {
        log.info("Finding animal by id: " + id);
        return animalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Animal not found"));
    }
    @Transactional
    public Animal deleteById(Long id) {
        log.info("Deleting animal by id: " + id);
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Animal not found"));
        animalRepository.delete(animal);
        return animal;
    }

    public List<Animal> findAll() {
        log.info("Finding all animals");
        return animalRepository.findAll(Sort.by("name"));
    }

//    @Transactional
//    public Animal update(Animal animal) {
//        log.info("Updating animal: " + animal);
//        return animalRepository.updateAnimalById(animal, animal.getId());
//    }

    public Animal findByName(String name) {
        log.info("Finding animal by name: " + name);
        return animalRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Animal not found"));
    }

    public Animal findByUserId(Long userId) {
        log.info("Finding animals by user id: " + userId);
        return animalRepository.findAnimalsByUserId(userId);
    }

}
