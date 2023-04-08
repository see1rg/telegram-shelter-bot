package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.repositories.AnimalRepository;
import lombok.extern.log4j.Log4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Сервис для работы с животными приюта
 */
@Log4j
@Service
public class AnimalService {
    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    /**
     * Сохранение животного в базу данных, использует метод репозитория
     * {@link JpaRepository#save(Object)}
     *
     * @param animal
     * @return Animal
     */
    @Transactional
    public Animal save(Animal animal) {
        log.info("Saving animal: " + animal);
        return animalRepository.save(animal);
    }

    /**
     * получение животного по id из БД используя метод репозитория {@link JpaRepository#findById(Object)}}
     *
     * @param id
     * @return Animal
     * @throws EntityNotFoundException
     */
    public Animal findById(Long id) {
        log.info("Finding animal by id: " + id);
        return animalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Animal not found"));
    }

    /**
     * удаление животного по id из БД используя метод репозитория {@link JpaRepository#deleteById(Object)}
     *
     * @param id
     * @return Animal
     * @throws EntityNotFoundException
     */
    @Transactional
    public Animal deleteById(Long id) {
        log.info("Deleting animal by id: " + id);
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Animal not found"));
        animalRepository.delete(animal);
        return animal;
    }

    /**
     * Находит всех животных в БД, сортируя их по имени, использует метод репозитория{@link JpaRepository#findAll()}
     *
     * @return List<Animal>
     */
    public List<Animal> findAll() {
        log.info("Finding all animals");
        return animalRepository.findAll(Sort.by("name"));
    }

    /**
     * Находит список животных по имени, использует метод репозитория {@link AnimalRepository#findAnimalsByName(String)}
     *
     * @param name
     * @return List<Animals>
     * @throws EntityNotFoundException
     */
    public List<Animal> findByName(String name) {
        log.info("Finding animal by name: " + name);
        return animalRepository.findAnimalsByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Animals not found"));
    }

    /**
     * Находит животное по имени хозяина, использует метод репозитория {@link AnimalRepository#findAnimalsByUserId(Long)} (Long)}
     *
     * @param userId
     * @return Animal
     */
    public Animal findByUserId(Long userId) {
        log.info("Finding animals by user id: " + userId);
        return animalRepository.findAnimalsByUserId(userId);
    }

    /**
     * Обновление животного в БД используя метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param animal
     * @param id
     * @return Animal
     * @throws EntityNotFoundException
     */
    @Transactional
    public Animal update(Animal animal, Long id) {
        log.info("Updating animal: " + animal);
        ModelMapper modelMapper = new ModelMapper();
        Animal animalToUpdate = animalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Animal not found"));
        animal.setId(id);
        modelMapper.map(animal, animalToUpdate);
        return animalRepository.save(animalToUpdate);
    }

    public List<Animal> findAllByUserIdNotNullAndState(Animal.AnimalStateEnum inTest) {
        log.info("Finding animals by user id and state");
        return animalRepository.findAllByUserIdNotNullAndState(inTest);
    }
}
