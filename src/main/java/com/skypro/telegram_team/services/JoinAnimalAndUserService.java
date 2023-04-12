package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.AnimalRepository;
import com.skypro.telegram_team.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

/**
 * Сервис для соединения животного и усыновителя
 */
@Log4j2
@Service
public class JoinAnimalAndUserService {
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;

    public JoinAnimalAndUserService(AnimalRepository animalRepository, UserRepository userRepository) {
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
    }

    public void joinAnimalAndUser(long animalId, long userId) {
        log.info("Joining animal and user with animal id: " + animalId + " and user id: " + userId);
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Animal not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        System.out.println("хотим добавить животное");
        animal.setUser(user);
        System.out.println("добавили животное");
        animalRepository.save(animal);

    }
}
