package com.skypro.telegram_team.services;

import com.skypro.telegram_team.exceptions.InvalidDataException;
import com.skypro.telegram_team.models.Animal;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        expectedUser.setEndTest(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    }

    @Test
    public void createUser() {
        when(userRepository.save(any())).thenReturn(expectedUser);
        User actualUser = userService.create(expectedUser);
        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void findById() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(expectedUser));
        User actualUser = userService.findById(expectedUser.getId());
        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void deleteById() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(expectedUser));
        User actualUser = userService.deleteById(expectedUser.getId());
        assertEquals(expectedUser, actualUser);
        verify(userRepository, times(1)).findById(any());
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
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findById(any());
    }

    @Test
    public void findAll() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3));
        List<User> allUsers = userService.findAll();
        assertTrue(allUsers.size() != 0);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void userIsVolunteer() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(expectedUser));
        when(userRepository.save(any())).thenReturn(expectedUser);
        User actualUser = userService.userIsVolunteer(expectedUser.getId(), true);
        assertTrue(actualUser.isVolunteer());
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(2)).findById(any());
    }

    @Test
    public void findByState() {
        List<User> expectedUsers = List.of(expectedUser);
        when(userRepository.findByState(any())).thenReturn(expectedUsers);
        List<User> actualUsers = userService.findByState(expectedUser.getState());
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository, times(1)).findByState(any());
    }

    @Test
    public void findVolunteers() {
        List<User> expectedUsers = List.of(expectedUser);
        when(userRepository.findByVolunteerTrue()).thenReturn(expectedUsers);
        Collection<User> actualUsers = userService.findVolunteers();
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository, times(1)).findByVolunteerTrue();
    }

    @Test
    public void findAnyVolunteer() {
        List<User> expectedUsers = List.of(expectedUser);
        when(userRepository.findByVolunteerTrue()).thenReturn(expectedUsers);
        Optional<User> anyVolunteer = userService.findAnyVolunteer();
        assertTrue(anyVolunteer.isPresent());
        assertEquals(anyVolunteer.get().getId(), expectedUser.getId());
        verify(userRepository, times(1)).findByVolunteerTrue();
    }

    @Test
    public void findByTelegramId() {
        List<User> expectedUsers = List.of(expectedUser);
        when(userRepository.findByTelegramId(any())).thenReturn(expectedUsers);
        User actualUser = userService.findByTelegramId(expectedUser.getTelegramId());
        assertEquals(expectedUsers.get(0), actualUser);
        verify(userRepository, times(1)).findByTelegramId(any());
    }

    @Test
    public void joinAnimalAndUser() {
        Animal animal = new Animal();
        animal.setId(1L);
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(expectedUser));
        when(userRepository.save(any())).thenReturn(expectedUser);
        when(animalRepository.findById(any())).thenReturn(Optional.of(animal));
        when(animalRepository.save(any())).thenReturn(animal);
        userService.joinAnimalAndUser(animal.getId(), expectedUser.getId());
        assertEquals(expectedUser.getAnimal().getId(), animal.getId());
        assertEquals(animal.getUser().getId(), expectedUser.getId());
        verify(userRepository, times(1)).save(any());
        verify(animalRepository, times(1)).save(any());
        verify(userRepository, times(2)).findById(any());
        verify(animalRepository, times(2)).findById(any());
    }

    @Test
    public void updateState() {
        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(expectedUser));
        when(userRepository.save(any())).thenReturn(expectedUser);
        User.OwnerStateEnum newState = User.OwnerStateEnum.BLACKLIST;
        Long newDaysForTest = 10L;
        LocalDateTime localDateTime = expectedUser.getEndTest().plusDays(newDaysForTest);
        User actualUser = userService.updateState(expectedUser.getId(), newState, newDaysForTest);
        assertEquals(newState, actualUser.getState());
        assertEquals(localDateTime, actualUser.getEndTest().truncatedTo(ChronoUnit.MINUTES));
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(2)).findById(any());
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
    public void shouldThrowsInvalidDataExceptionWhenRunMethodUpdateWhereTelegramIdIsZero() {
        User updatedUser = new User();
        updatedUser.setName("dima");
        assertThrows(InvalidDataException.class, () -> userService.update(updatedUser, expectedUser.getId()));
    }

    @Test
    public void shouldThrowsInvalidDataExceptionWhenRunMethodUpdateWherePhoneIsInvalid() {
        User updatedUser = new User();
        updatedUser.setName("dima");
        updatedUser.setTelegramId(101L);
        updatedUser.setPhone("123");
        assertThrows(InvalidDataException.class, () -> userService.update(updatedUser, expectedUser.getId()));
    }

    @Test
    public void shouldThrowsInvalidDataExceptionWhenRunMethodUpdateWhereEmailIsInvalid() {
        User updatedUser = new User();
        updatedUser.setName("dima");
        updatedUser.setTelegramId(101L);
        updatedUser.setEmail("123");
        assertThrows(InvalidDataException.class, () -> userService.update(updatedUser, expectedUser.getId()));
    }

    @Test
    public void shouldThrowsInvalidDataExceptionWhenRunMethodCreate() {
        User user = new User();
        assertThrows(InvalidDataException.class, () -> userService.create(user));
    }

    @Test
    public void shouldThrowsEntityNotFoundExceptionWhenRunMethodJoinAnimalAndUser() {
        Animal animal = new Animal();
        when(animalRepository.findById(any())).thenReturn(Optional.of(animal));
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.joinAnimalAndUser(animal.getId(), expectedUser.getId()));
    }

    @Test
    public void shouldIllegalArgumentExceptionWhenRunMethodSetUpdate() {
        User userForTest = new User();
        userForTest.setState(User.OwnerStateEnum.PROLONGED);
        userForTest.setId(100L);
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateState(userForTest.getId(), userForTest.getState(), null));
    }
}
