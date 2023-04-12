package com.skypro.telegram_team.services;

import com.skypro.telegram_team.exceptions.InvalidDataException;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        //Нужно переименовать метод save в create
        log.info("Saving user: " + user.getName() + " " + user.getSurname());
        validate(user);
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
        validate(user);
        ModelMapper modelMapper = new ModelMapper();
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setId(id);
        if (user.getState().equals(User.OwnerStateEnum.PROBATION) && user.getEndTest() != null) {
            user.setEndTest(LocalDateTime.now().plusDays(30));
        }
        modelMapper.map(user, userToUpdate);
        return userRepository.save(userToUpdate);
    }

    /**
     * Назначить пользователя волонтером
     *
     * @param id
     * @param isVolunteer
     * @return
     */
    @Transactional
    public User userIsVolunteer(Long id, Boolean isVolunteer) {
        log.info("User is volunteer: " + isVolunteer);
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setVolunteer(isVolunteer);
        return update(user, id);
    }

    public List<User> findByState(User.OwnerStateEnum state) {
        return userRepository.findByState(state);

    /**
     * Поиск волонтеров
     *
     * @return
     */
    public Collection<User> findVolunteers() {
        return userRepository.findByIsVolunteerTrue();
    }

    /**
     * Поиск любого волонтера
     *
     * @return
     */
    public Optional<User> findAnyVolunteer() {
        return findVolunteers().stream()
                .findAny();
    }

    /**
     * Поиск пользователя по telegramId, возвращает пустой User если не нашел в БД
     *
     * @param telegramId
     * @return
     */
    public User findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId).stream()
                .findFirst()
                .orElse(new User());
    }

    /**
     * Проверить данные пользователя, если данные некорректны то выбросить исключение {@link InvalidDataException}
     *
     * @param user
     */
    private void validate(User user) {
        if (user.getName() == null) {
            throw new InvalidDataException("Отсутствует имя пользователя");
        }
        if (user.getTelegramId() == 0L) {
            throw new InvalidDataException("Отсутствует telegramId");
        }
        if (user.getPhone() != null && !isPhoneValid(user.getPhone())) {
            throw new InvalidDataException("Некорректный телефон");
        }
        if (user.getEmail() != null && !isEmailValid(user.getEmail())) {
            throw new InvalidDataException("Некорректная почта");
        }
    }

    /**
     * Проверить телефон, вернуть true если соответствует шаблону
     *
     * @param phone
     * @return
     */
    private boolean isPhoneValid(String phone) {
        //Добавить проверку телефона
        return true;
    }

    /**
     * Проверить почту, вернуть true если соответствует шаблону
     *
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        //Добавить проверку почты
        return true;

    }
}
