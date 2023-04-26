package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.AnimalRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

/**
 * Сервис для работы с животными приюта
 */
@Log4j2
@Service
public class AnimalService {
    @Value("${dir.for.animal.photo}")
    private String animalPhotoDir;

    private final AnimalRepository animalRepository;


    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    /**
     * Сохранение животного в базу данных, использует метод репозитория
     * {@link JpaRepository#save(Object)}
     *
     * @param animal объект типа Animal, который будет сохранен в БД
     * @return Animal
     */
    @Transactional
    public Animal create(Animal animal, Animal.TypeAnimal type) {
        log.info("Saving animal: " + animal);
        animal.setType(type);
        return animalRepository.save(animal);
    }

    /**
     * получение животного по id из БД используя метод репозитория {@link JpaRepository#findById(Object)}}
     *
     * @param id идентификатор животного
     * @return Animal
     * @throws EntityNotFoundException если животное не найдено в базе данных
     */
    public Animal findById(Long id) {
        log.info("Finding animal by id: " + id);
        return animalRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Animal not found"));
    }

    /**
     * удаление животного по id из БД используя метод репозитория {@link JpaRepository#deleteById(Object)}
     *
     * @param id идентификатор животного
     * @return Animal
     * @throws EntityNotFoundException если животное не найдено в базе данных
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
     * @param name имя животного, которое нужно найти
     * @return List<Animals>
     * @throws EntityNotFoundException если животное не найдено в базе данных
     */
    public List<Animal> findByName(String name) {
        log.info("Finding animal by name: " + name);
        return animalRepository.findAnimalsByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Animals not found"));
    }

    /**
     * Находит животное по имени хозяина, использует метод репозитория {@link AnimalRepository#findAnimalsByUserId(Long)} (Long)}
     *
     * @param userId идентификатор пользователя
     * @return Animal
     */
    public Animal findByUserId(Long userId) {
        log.info("Finding animals by user id: " + userId);
        return animalRepository.findAnimalsByUserId(userId);
    }

    /**
     * Обновление животного в БД используя метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param animal объект животное, содержащий данные для обновления
     * @param id     идентификатор животного, которое нужно обновить
     * @return Animal
     * @throws EntityNotFoundException если животное не найдено в базе данных
     * @throws IllegalStateException   если тип животного не совпадает с типом приюта
     */
    @Transactional
    public Animal update(Animal animal, Long id) {
        log.info("Updating animal: " + animal);
        ModelMapper modelMapper = new ModelMapper();
        if (animal.getShelter() != null) {
            if (animal.getType() != animal.getShelter().getType()) {
                throw new IllegalStateException("Animal type does not match shelter type.");
            }
        }
        Animal animalToUpdate = animalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Animal not found"));
        animal.setId(id);

        modelMapper.map(animal, animalToUpdate);
        return animalRepository.save(animalToUpdate);
    }

    /**
     * Поиск всех животных по статусу животного и поля userId не равного null, использует метод репозитория
     * {@link AnimalRepository#findAllByUserIdNotNullAndState(Animal.AnimalStateEnum)}
     *
     * @param inTest статус животного
     * @return List<Animal>
     */
    public List<Animal> findAllByUserIdNotNullAndState(Animal.AnimalStateEnum inTest) {
        log.info("Finding animals by user id and state - " + inTest);
        return animalRepository.findAllByUserIdNotNullAndState(inTest);
    }

    /**
     * Поиск всех животных по статусу пользователя
     *
     * @param ownerStateEnum статус пользователя
     * @return List<Animal>
     */
    public List<Animal> findByUserState(User.OwnerStateEnum ownerStateEnum) {
        log.info("Finding animals by user state - " + ownerStateEnum);
        return animalRepository.findByUserContainsOrderByState(ownerStateEnum);
    }

    /**
     * Метод по загрузке фото животного по его id
     * @param animalId
     * @param file
     * @throws Exception
     */
    public void downloadPhoto(Long animalId, MultipartFile file) throws Exception  {
        Optional<Animal> animal = animalRepository.findById(animalId);
        if (animal.isPresent()) {
            Path path = Path.of(animalPhotoDir, animal.get().getType().toString()
                    + animal.get().getName() + animalId + "." + getExtension(file.getOriginalFilename()));
            Files.createDirectories(path.getParent());
            Files.deleteIfExists(path);
            try (InputStream is = file.getInputStream();
                 OutputStream os = Files.newOutputStream(path, CREATE_NEW);
                 BufferedInputStream bis = new BufferedInputStream(is, 1024);
                 BufferedOutputStream bos = new BufferedOutputStream(os, 1024);) {
                bis.transferTo(bos);
            }
            animal.get().setPhoto(file.getBytes());
            animalRepository.save(animal.get());
        } else {
            throw new EntityNotFoundException("animal with " + animalId + " not exist");
        }
    }

    /**
     * Приватный метод для получения расширения файла
     * @param fileName
     * @return
     */
    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * Метод для получения фото животного по его id
     * @param id
     * @return
     */
    public byte[] getPhoto(Long id) {
        Optional<Animal> animal = animalRepository.findById(id);
        return animal.get().getPhoto();
    }
}
