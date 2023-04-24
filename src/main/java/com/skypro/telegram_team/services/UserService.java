package com.skypro.telegram_team.services;

import com.skypro.telegram_team.exceptions.InvalidDataException;
import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.AnimalRepository;
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
import java.util.regex.Pattern;

/**
 * Сервис для работы с пользователями.
 * <p>
 * Реализует CRUD-операции (создание, чтение, обновление, удаление) для сущности User
 * с использованием JpaRepository.
 */
@Log4j2
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;


    /**
     * Конструктор класса UserService.
     *
     * @param userRepository   Репозиторий для работы с сущностью User.
     * @param animalRepository Репозиторий для работы с сущностью Animal.
     */
    public UserService(UserRepository userRepository, AnimalRepository animalRepository) {
        this.userRepository = userRepository;
        this.animalRepository = animalRepository;
    }

    /**
     * Создает нового пользователя и сохраняет его в БД.
     *
     * @param user Объект типа User с данными нового пользователя.
     * @return Объект типа User, сохраненный в БД.
     * @throws InvalidDataException Если данные пользователя некорректны.
     */
    @Transactional
    public User create(User user) {
        log.info("Saving user: " + user.getName() + " " + user.getSurname());
        validate(user);
        return userRepository.save(user);
    }

    /**
     * получение пользователя по id из БД используя метод репозитория {@link JpaRepository#findById(Object)}
     *
     * @param id ID пользователя, которого нужно найти
     * @return пользователь с указанным ID
     * @throws EntityNotFoundException если пользователь с указанным ID не найден
     */
    public User findById(Long id) {
        log.info("Finding user by id: " + id);
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    /**
     * удаление пользователя по id из БД используя метод репозитория {@link JpaRepository#deleteById(Object)}
     *
     * @param id ID пользователя, которого нужно удалить
     * @return пользователь с указанным ID
     * @throws EntityNotFoundException если пользователь с указанным ID не найден
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
     * @return список всех пользователей из БД
     */
    public List<User> findAll() {
        log.info("Finding all users");
        return userRepository.findAll();
    }

    /**
     * Обновляет данные пользователя в БД по заданному идентификатору, используя
     * метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param user объект пользователя, содержащий данные для обновления
     * @param id   идентификатор пользователя, данные которого нужно обновить
     * @return объект пользователя после обновления данных
     * @throws EntityNotFoundException  если пользователь с заданным идентификатором не найден в БД
     * @throws IllegalArgumentException если объект пользователя не проходит валидацию
     * @see ModelMapper
     */
    @Transactional
    public User update(User user, Long id) {
        log.info("Updating myUser: " + user);
        validate(user);
        ModelMapper modelMapper = new ModelMapper();
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setId(id);
        modelMapper.map(user, userToUpdate);
        return userRepository.save(userToUpdate);
    }

    /**
     * Устанавливает флаг волонтера для пользователя.
     *
     * @param id          идентификатор пользователя.
     * @param isVolunteer флаг волонтера.
     * @return обновленный экземпляр пользователя.
     * @throws EntityNotFoundException если пользователь не найден в БД.
     */
    @Transactional
    public User userIsVolunteer(Long id, Boolean isVolunteer) {
        log.info("User is volunteer: " + isVolunteer);
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setVolunteer(isVolunteer);
        return update(user, id);
    }

    /**
     * Возвращает список пользователей из БД, чей статус соответствует переданному значению
     *
     * @param state статус владельца
     * @return список пользователей, чей статус соответствует переданному значению
     * @see User
     */
    public List<User> findByState(User.OwnerStateEnum state) {
        return userRepository.findByState(state);
    }

    /**
     * Поиск волонтеров
     *
     * @return список волонтеров
     */
    public Collection<User> findVolunteers() {
        return userRepository.findByVolunteerTrue();
    }

    /**
     * Возвращает любого волонтера из списка волонтеров.
     *
     * @return объект типа Optional<User>, содержащий любого волонтера из списка волонтеров,
     * если список не пустой, иначе пустой объект Optional.
     */
    public Optional<User> findAnyVolunteer() {
        log.info("Поиск волонтера");
        return findVolunteers().stream()
                .findAny();
    }

    /**
     * Находит пользователя по заданному идентификатору Telegram.
     *
     * @param telegramId идентификатор Telegram, по которому нужно найти пользователя.
     * @return объект типа User, соответствующий заданному идентификатору Telegram,
     * или пустой объект User, если пользователь не найден.
     */
    public User findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId).stream()
                .findFirst()
                .orElse(new User());
    }

    /**
     * Проверяет данные пользователя на корректность и выбрасывает исключение {@link InvalidDataException},
     * если данные некорректны.
     *
     * @param user объект типа User, содержащий данные пользователя, которые нужно проверить.
     * @throws InvalidDataException если данные пользователя некорректны.
     */
    private void validate(User user) {
        log.info("Валидация пользователя: " + user);
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
     * Проверяет, соответствует ли заданный номер телефона российскому формату.
     *
     * @param phone номер телефона, который нужно проверить.
     * @return true, если номер телефона соответствует российскому формату, и false в противном случае.
     */
    private boolean isPhoneValid(String phone) {
        log.info("Валидация номера телефона: {}", phone);
        final String regexPattern = "^((\\+7|7|8)+([0-9]){10})$";
        return Pattern.compile(regexPattern).matcher(phone).matches();
    }

    /**
     * Проверяет, соответствует ли заданный email адрес заданному шаблону.
     *
     * @param email email адрес, который нужно проверить.
     * @return true, если email адрес соответствует шаблону, и false в противном случае.
     */
    private boolean isEmailValid(String email) {
        log.info("Валидация email: {}", email);
        final String regexPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.compile(regexPattern).matcher(email).matches();
    }

    /**
     * Метод, который связывает животное с пользователем по их идентификаторам, устанавливает статус
     * {@link User.OwnerStateEnum#PROBATION} и конец испытательного срока {@link User#setEndTest} у пользователя
     * и у животного статус {@link Animal.AnimalStateEnum#IN_TEST}.
     * <p>
     * @param animalId идентификатор животного, которую нужно связать с пользователем
     * @param userId   идентификатор пользователя, который будет связан с животным
     * @throws EntityNotFoundException если животное или пользователь не найдены в базе данных
     */

    @Transactional
    public void joinAnimalAndUser(long animalId, long userId) {
        log.info("Joining animal and user with animal id: " + animalId + " and user id: " + userId);
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new EntityNotFoundException("Animal not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        animal.setUser(user);
        animal.setState(Animal.AnimalStateEnum.IN_TEST);
        user.setAnimal(animal);
        user.setState(User.OwnerStateEnum.PROBATION);
        user.setEndTest(LocalDateTime.now().plusMonths(1));
        update(user, userId);
        AnimalService animalService = new AnimalService(animalRepository);
        animalService.update(animal, animalId);

    }

    @Transactional
    public User updateState(long userId, User.OwnerStateEnum state, Long daysForTest) {
        String exceptionMessage = "Для продления тестового периода усыновителя" +
                " необходимо задать количество дней для теста";
        if (daysForTest != null) {
            if (state == User.OwnerStateEnum.PROLONGED && daysForTest == 0) {
                throw new IllegalArgumentException(exceptionMessage);
            }
        } else if (state == User.OwnerStateEnum.PROLONGED){
            throw new IllegalArgumentException(exceptionMessage);
        }
        User user = findById(userId);
        user.setState(state);
        if (daysForTest != null && daysForTest > 0) {
            LocalDateTime dateEndTest = LocalDateTime.now().plusDays(daysForTest);
            user.setEndTest(dateEndTest);
        }
        return update(user, userId);
    }

}


