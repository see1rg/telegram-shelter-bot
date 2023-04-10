package com.skypro.telegram_team.services;

import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для работы с пользователями
 */
@Log4j2
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * сохранение пользователя в БД используя метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param user
     * @return
     * @see
     */
    @Transactional
    public User save(User user) {
        log.info("Saving user: " + user.getName() + " " + user.getSurname());
        return userRepository.save(user);
    }

    /**
     * получение пользователя по id из БД используя метод репозитория {@link JpaRepository#findById(Object)}
     *
     * @param id
     * @return
     */
    public User findById(Long id) {
        log.info("Finding user by id: " + id);
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    /**
     * удаление пользователя по id из БД используя метод репозитория {@link JpaRepository#deleteById(Object)}
     *
     * @param id
     * @return
     */
    @Transactional
    public User deleteById(Long id) {
        log.info("Deleting user by id: " + id);
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.delete(user);
        return user;
    }

    /**
     * получение всех пользователей из БД используя метод репозитория {@link JpaRepository#findAll()}
     *
     * @return
     */
    public List<User> findAll() {
        log.info("Finding all users");
        return userRepository.findAll();
    }

    /**
     * обновление пользователя в БД используя метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param user
     * @param id
     * @return
     */
    @Transactional
    public User update(User user, Long id) {
        log.info("Updating myUser: " + user);
        ModelMapper modelMapper = new ModelMapper();
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setId(id);
        if (user.getState().equals(User.OwnerStateEnum.PROBATION) && user.getEndTrialPeriod() != null) {
            user.setEndTrialPeriod(LocalDateTime.now().plusDays(30));
        }
        modelMapper.map(user, userToUpdate);
        return userRepository.save(userToUpdate);
    }

}
