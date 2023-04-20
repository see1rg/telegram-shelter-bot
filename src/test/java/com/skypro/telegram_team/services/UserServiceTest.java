package com.skypro.telegram_team.services;

import com.skypro.telegram_team.exceptions.InvalidDataException;
import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Dog;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.repositories.AnimalRepository;
import com.skypro.telegram_team.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AnimalRepository animalRepository;
    private User expectedUser;

    @BeforeEach
    public void setup() {
        expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setTelegramId(111L);
        expectedUser.setName("dima");
        expectedUser.setState(User.OwnerStateEnum.PROBATION);
    }

    @Test
    public void createUser() {
        when(userRepository.save(any())).thenReturn(expectedUser);
        User actualUser = userService.create(expectedUser);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void findById() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(expectedUser));
        User actualUser = userService.findById(expectedUser.getId());
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void deleteById() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(expectedUser));
        User actualUser = userService.deleteById(expectedUser.getId());
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void updateUser() {
        User userInDB = new User();
        userInDB.setName("dima");
        userInDB.setTelegramId(99L);
        userInDB.setId(1L);
        User updatedUser = new User();
        updatedUser.setName("sergei");
        updatedUser.setTelegramId(101L);
        when(userRepository.findById(any())).thenReturn(Optional.of(userInDB));
        when(userRepository.save(any())).thenReturn(updatedUser);
        User actualUser = userService.update(updatedUser, userInDB.getId());
        assertEquals(actualUser.getId(), userInDB.getId());
        assertEquals(actualUser.getName(), updatedUser.getName());
        assertEquals(actualUser.getTelegramId(), updatedUser.getTelegramId());
    }

    @Test
    public void findAll() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));
        List<User> allUsers = userService.findAll();
        assertTrue(allUsers.size() != 0);
    }

    @Test
    public void userIsVolunteer() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(expectedUser));
        when(userRepository.save(any())).thenReturn(expectedUser);
        User actualUser = userService.userIsVolunteer(expectedUser.getId(), true);
        assertTrue(actualUser.isVolunteer());
    }

    @Test
    public void findByState() {
        List<User> expectedUsers = List.of(expectedUser);
        when(userRepository.findByState(any())).thenReturn(expectedUsers);
        List<User> actualUsers = userService.findByState(expectedUser.getState());
        assertEquals(expectedUsers, actualUsers);
    }

    //нужны еще тесты
    @Test
    public void findVolunteers() {
        List<User> expectedUsers = List.of(expectedUser);
        when(userRepository.findByVolunteerTrue()).thenReturn(expectedUsers);
        Collection<User> actualUsers = userService.findVolunteers();
        assertEquals(expectedUsers, actualUsers);
    }

    //нужны еще тесты
    @Test
    public void findAnyVolunteer() {
        List<User> expectedUsers = List.of(expectedUser);
        when(userRepository.findByVolunteerTrue()).thenReturn(expectedUsers);
        Optional<User> anyVolunteer = userService.findAnyVolunteer();
        assertTrue(anyVolunteer.isPresent());
        assertEquals(anyVolunteer.get().getId(), expectedUser.getId());
    }

    @Test
    public void findByTelegramId() {
        List<User> expectedUsers = List.of(expectedUser);
        when(userRepository.findByTelegramId(any())).thenReturn(expectedUsers);
        User actualUser = userService.findByTelegramId(expectedUser.getTelegramId());
        assertEquals(expectedUsers.get(0), actualUser);
    }

    @Test
    public void joinAnimalAndUser() {
        Animal animal = new Dog();
        animal.setId(1L);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(expectedUser));
        when(userRepository.save(any())).thenReturn(expectedUser);
        when(animalRepository.findById(any())).thenReturn(Optional.of(animal));
        when(animalRepository.save(any())).thenReturn(animal);
        userService.joinAnimalAndUser(animal.getId(), expectedUser.getId());
        assertEquals(expectedUser.getAnimal().getId(), animal.getId());
        assertEquals(animal.getUser().getId(), expectedUser.getId());
    }

    @Test
    public void shouldThrowsEntityNotFoundExceptionWhenRunMethodFindById() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findById(any()));
    }

    @Test
    public void shouldThrowsEntityNotFoundExceptionWhenRunMethodDeleteById() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.deleteById(any()));
    }

    @Test
    public void shouldThrowsEntityNotFoundExceptionWhenRunMethodUpdate() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        User updatedUser = new User();
        updatedUser.setId(10L);
        updatedUser.setTelegramId(20L);
        updatedUser.setName("danil");
        assertThrows(EntityNotFoundException.class, () -> userService.update(updatedUser, any()));
    }

    @Test
    public void shouldThrowsInvalidDataExceptionWhenRunMethodUpdate() {
        User updatedUser = new User();
        updatedUser.setId(99L);
        updatedUser.setTelegramId(66L);
        assertThrows(InvalidDataException.class, () -> userService.update(updatedUser, expectedUser.getId()));
    }
}
