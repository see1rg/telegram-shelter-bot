package com.skypro.telegram_team.services;

import com.skypro.telegram_team.exceptions.InvalidDataException;
import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.AnimalRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Сервис для работы с животными приюта
 */
@Log4j2
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
        if (animal.getShelter() != null && animal.getType() != animal.getShelter().getType()) {
            throw new IllegalStateException("Animal type does not match shelter type.");
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
     * Загрузка фото животного
     *
     * @param id   идентификатор животного
     * @param file фото
     */
    @Transactional
    public void photoUpload(Long id, MultipartFile file) throws IOException {
        log.info("Was invoked method to upload photo to animal {}", id);
        var fileExt = getFileExtensions(Objects.requireNonNull(file.getOriginalFilename()));
        if (!fileExt.equals(MediaType.IMAGE_JPEG.getSubtype())) {
            throw new InvalidDataException("Only JPEG files allowed");
        }
        var animal = animalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Animal not found"));
        animal.setPhoto(file.getBytes());
        animalRepository.save(animal);
    }

    /**
     * Выгрузка фото животного
     *
     * @param id идентификатор животного
     * @return данные
     */
    public byte[] photoDownload(Long id) {
        log.info("Was invoked method to download photo from animal {}", id);
        var animal = animalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Animal not found"));
        if (animal.getPhoto() == null) {
            throw new EntityNotFoundException("Animal photo not found");
        }
        return animal.getPhoto();
    }

    /**
     * Получение расширения файла
     *
     * @param fileName полное имя файла
     * @return расширение
     */
    private String getFileExtensions(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
