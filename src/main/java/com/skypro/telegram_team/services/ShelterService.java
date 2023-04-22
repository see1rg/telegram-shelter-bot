package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.Shelter;
import com.skypro.telegram_team.repositories.ShelterRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;

/**
 * Сервис для работы с приютами.
 * <p>
 * Реализует CRUD-операции (создание, чтение, обновление, удаление) для сущности Shelter
 * с использованием JpaRepository.
 */
@Log4j2
@Service
public class ShelterService {
    private final ShelterRepository shelterRepository;

    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    /**
     * получение приюта по id из БД используя метод репозитория {@link JpaRepository#findById(Object)}
     *
     * @param id ID приюта, которого нужно найти
     * @return приюта с указанным ID
     * @throws EntityNotFoundException если приют с указанным ID не найден
     */
    public Shelter findById(Long id) {
        log.info("Finding shelter by id: " + id);
        return shelterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shelter not found"));
    }

    /**
     * получение всех приютов из БД используя метод репозитория {@link JpaRepository#findAll()}
     *
     * @return список всех приютов из БД
     */
    public Collection<Shelter> findAll() {
        log.info("Finding all shelters");
        return shelterRepository.findAll();
    }

    /**
     * Создает новый приют и сохраняет его в БД.
     *
     * @param shelter Объект типа Shelter с данными нового приюта.
     * @return Объект типа Shelter, сохраненный в БД.
     */
    @Transactional
    public Shelter create(Shelter shelter) {
        log.info("Saving shelter: " + shelter.getName());
        return shelterRepository.save(shelter);
    }

    /**
     * Обновляет данные приюта в БД по заданному идентификатору, используя
     * метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param shelter объект приюта, содержащий данные для обновления
     * @param id      идентификатор приюта, данные которого нужно обновить
     * @return объект приюта после обновления данных
     * @throws EntityNotFoundException если приют с заданным идентификатором не найден в БД
     * @see ModelMapper
     */
    @Transactional
    public Shelter update(Shelter shelter, Long id) {
        log.info("Updating Shelter: " + shelter);
        //ModelMapper modelMapper = new ModelMapper();
        Shelter shelterToUpdate = shelterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shelter not found"));
        //shelter.setId(id);
        //modelMapper.map(shelter, shelterToUpdate);
        return shelterRepository.save(shelter);
    }

    /**
     * удаление приюта по id из БД используя метод репозитория {@link JpaRepository#deleteById(Object)}
     *
     * @param id ID приюта, которого нужно удалить
     * @return приют с указанным ID
     * @throws EntityNotFoundException если приют с указанным ID не найден
     */
    @Transactional
    public Shelter delete(Long id) {
        log.info("Deleting shelter by id: " + id);
        Shelter shelter = shelterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shelter not found"));
        shelterRepository.delete(shelter);
        return shelter;
    }
}
