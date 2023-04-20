package com.skypro.telegram_team.timer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.skypro.telegram_team.models.*;
import com.skypro.telegram_team.services.AnimalService;
import com.skypro.telegram_team.services.ReportService;
import com.skypro.telegram_team.services.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimerTest {

    @Mock
    private TelegramBot bot;

    @Mock
    private AnimalService animalService;

    @Mock
    private ReportService reportService;

    @Mock
    private UserService userService;

    @InjectMocks
    private Timer timer;

    @Test
    public void testChangeStateAcceptedToAdoptedAndCollect() {
        // Given
        User user = new User();
        user.setState(User.OwnerStateEnum.ACCEPTED);
        user.setTelegramId(1L);
        user.setName("John");
        user.setSurname("Doe");
        List<User> users = Collections.singletonList(user);
        when(userService.findByState(User.OwnerStateEnum.ACCEPTED)).thenReturn(users);

        User volunteer = new User();
        volunteer.setTelegramId(0L);
        volunteer.setVolunteer(true);
        when(userService.findVolunteers()).thenReturn(Collections.singletonList(volunteer));

        // When
        List<User> result = timer.changeStateAcceptedToAdoptedAndCollect();

        // Then
        List<SendMessage> actual = getActualSendMessages();
        Assertions.assertThat(actual.size()).isEqualTo(2);
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(1L);
        Assertions.assertThat(actual.get(0).getParameters().get("text"))
                .isEqualTo("Уважаемый John Doe Поздравляем, вы прошли пробный период!");
        Assertions.assertThat(actual.get(1).getParameters().get("chat_id")).isEqualTo(0L);
        Assertions.assertThat(actual.get(1).getParameters().get("text"))
                .isEqualTo(String.format("Одобрение на усыновление подтверждено у %s %s.",
                user.getName(), user.getSurname()));

        assertEquals(1, result.size());
        User updatedUser = result.get(0);
        assertEquals(User.OwnerStateEnum.ADOPTED, updatedUser.getState());
    }

    @Test
    public void testChangeStateRefusedToBlackListAndCollect() {
        // Given
        User user = new User();
        user.setState(User.OwnerStateEnum.REFUSE);
        user.setTelegramId(1L);
        user.setName("John");
        user.setSurname("Doe");
        List<User> users = Collections.singletonList(user);
        when(userService.findByState(User.OwnerStateEnum.REFUSE)).thenReturn(users);

        User volunteer = new User();
        volunteer.setTelegramId(0L);
        volunteer.setVolunteer(true);
        when(userService.findVolunteers()).thenReturn(Collections.singletonList(volunteer));

        // When
        List<User> result = timer.changeStateRefusedToBlackListAndCollect();

        // Then
        assertEquals(1, result.size());
        User updatedUser = result.get(0);
        assertEquals(User.OwnerStateEnum.BLACKLIST, updatedUser.getState());

        List<SendMessage> actual = getActualSendMessages();
        Assertions.assertThat(actual.size()).isEqualTo(2);
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(1L);
        Assertions.assertThat(actual.get(0).getParameters().get("text"))
                .isEqualTo(String.format("Уважаемый %s %s Вы НЕ прошли пробный период! " +
                "Пожалуйста сдайте собаку в приют!", user.getName(), user.getSurname()));
        Assertions.assertThat(actual.get(1).getParameters().get("chat_id")).isEqualTo(0L);
        Assertions.assertThat(actual.get(1).getParameters().get("text"))
                .isEqualTo( String.format("Отказ подтвержден у %s %s.",
                user.getName(), user.getSurname()));
    }


    @Test
    public void testFindStateProlongedAndCollect() {
        // Arrange
        User user = new User();
        user.setTelegramId(12345L);
        user.setName("John");
        user.setSurname("Doe");
        user.setState(User.OwnerStateEnum.PROLONGED);
        user.setEndTest(LocalDateTime.now().minusDays(1));
        List<User> users = Collections.singletonList(user);
        when(userService.findByState(User.OwnerStateEnum.PROLONGED)).thenReturn(users);

        User volunteer = new User();
        volunteer.setTelegramId(0L);
        volunteer.setVolunteer(true);
        when(userService.findVolunteers()).thenReturn(Collections.singletonList(volunteer));

        // Act
        List<User> result = timer.findStateProlongedAndCollect();

        // Then
        List<SendMessage> actual = getActualSendMessages();
        Assertions.assertThat(actual.size()).isEqualTo(2);
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(12345L);
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo(String.format(
                "Уважаемый %s %s, мы решили продлить пробный период на %s дней!",
                user.getName(), user.getSurname(),
                Duration.between(user.getEndTest(), LocalDateTime.now()).toDays()));
        Assertions.assertThat(actual.get(1).getParameters().get("chat_id")).isEqualTo(0L);
        Assertions.assertThat(actual.get(1).getParameters().get("text")).isEqualTo(String.format(
                "Подтверждено продление у %s %s на %s дней!",
                user.getName(), user.getSurname(),
                Duration.between(user.getEndTest(), LocalDateTime.now()).toDays()));
        assertEquals(1, result.size());
        User updatedUser = result.get(0);
        assertEquals(User.OwnerStateEnum.PROBATION, updatedUser.getState());
    }


    @Test
    public void testDecisionMakingOfVolunteersAboutUsers() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setTelegramId(12345);
        user.setName("John");
        user.setSurname("Doe");
        user.setState(User.OwnerStateEnum.PROBATION);
        user.setEndTest(LocalDateTime.now().plusDays(1));
        List<User> users = Collections.singletonList(user);
        when(userService.findByState(User.OwnerStateEnum.PROBATION)).thenReturn(users);

        // Act
        List<User> result = timer.decisionMakingOfVolunteersAboutUsers();

        // Then
        assertEquals(1, result.size());
        User updatedUser = result.get(0);
        assertEquals(User.OwnerStateEnum.DECISION, updatedUser.getState());
    }

    @Test
    void testChangeStateRefusedToInShelterListAndCollect() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setTelegramId(12345);
        user.setName("John");
        user.setSurname("Doe");
        user.setState(User.OwnerStateEnum.BLACKLIST);

        Animal animal = new Cat();
        animal.setId(1L);
        animal.setName("Animal");
        animal.setState(Animal.AnimalStateEnum.IN_TEST);
        animal.setUser(user);
        List<Animal> animalList = Collections.singletonList(animal);

        when(animalService.findByUserState(User.OwnerStateEnum.BLACKLIST))
                .thenReturn(animalList);

        // Act
        var result = timer.changeStateRefusedToInShelterListAndCollect();

        // Assert
        verify(animalService).findByUserState(User.OwnerStateEnum.BLACKLIST);

        assertEquals(1, result.size());
        Animal updatedAnimal = result.get(0);
        assertEquals(Animal.AnimalStateEnum.IN_SHELTER, updatedAnimal.getState());
    }

    @Test
    void testChangeStateAcceptedToHappyEndAndCollect() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setTelegramId(12345);
        user.setName("John");
        user.setSurname("Doe");
        user.setState(User.OwnerStateEnum.ADOPTED);

        Animal animal = new Cat();
        animal.setId(1L);
        animal.setName("Animal");
        animal.setState(Animal.AnimalStateEnum.IN_TEST);
        animal.setUser(user);
        List<Animal> animalList = Collections.singletonList(animal);

        when(animalService.findByUserState(User.OwnerStateEnum.ADOPTED))
                .thenReturn(animalList);

        // Act
        var result = timer.changeStateAcceptedToHappyEndAndCollect();

        // Assert
        verify(animalService).findByUserState(User.OwnerStateEnum.ADOPTED);

        assertEquals(1, result.size());
        Animal updatedAnimal = result.get(0);
        assertEquals(Animal.AnimalStateEnum.HAPPY_END, updatedAnimal.getState());
    }

    @Test
    void testCheckingDailyAndTwoDaysReportFromUsers() {

        // Create two users
        User user1 = new User();
        user1.setName("John");
        user1.setSurname("Doe");
        user1.setTelegramId(12345L);
        user1.setEmail("john.doe@example.com");
        user1.setPhone("+78123003030");
        user1.setState(User.OwnerStateEnum.PROBATION);

        User user2 = new User();
        user2.setName("Jane");
        user2.setSurname("Doe");
        user2.setTelegramId(67890L);
        user2.setEmail("jane.doe@example.com");
        user2.setPhone("+78123003030");
        user2.setState(User.OwnerStateEnum.PROBATION);

        // Create two animals belonging to user1 and user2
        Animal animal1 = new Dog();
        animal1.setUser(user1);
        animal1.setName("Buddy");
        animal1.setState(Animal.AnimalStateEnum.IN_TEST);

        Animal animal2 = new Dog();
        animal2.setUser(user2);
        animal2.setName("Max");
        animal2.setState(Animal.AnimalStateEnum.IN_TEST);

        // Create a report for animal1
        Report report = new Report();
        report.setAnimal(animal1);
        report.setDate(LocalDateTime.now().minusHours(12));
        report.setUser(user1);

        // Create a report for animal2
        Report report2 = new Report();
        report2.setAnimal(animal2);
        report2.setDate(LocalDateTime.now().minusDays(2).withHour(0));
        report2.setUser(user2);

        // Add the animals and report to the database
        animalService.create(animal1);
        animalService.create(animal2);
        reportService.create(report);
        reportService.create(report2);
        when(reportService.findByAnimalId(animal1.getId())).thenReturn(Collections.singletonList(report));
        when(reportService.findByAnimalId(animal2.getId())).thenReturn(Collections.singletonList(report2));
        when(animalService.findAllByUserIdNotNullAndState(Animal.AnimalStateEnum.IN_TEST))
                .thenReturn(List.of(animal1, animal2));

        // Act
        timer.checkingDailyAndTwoDaysReportFromUsers();

        // Verify that the users receive the correct messages
        List<SendMessage> actual = getActualSendMessages();
        Assertions.assertThat(actual.size()).isEqualTo(2);
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(12345L);
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("Последний отчет " +
                "был принят более двух дней! Пожалуйста, сдайте отчет.");
        Assertions.assertThat(actual.get(1).getParameters().get("chat_id")).isEqualTo(67890L);
        Assertions.assertThat(actual.get(1).getParameters().get("text")).isEqualTo("Последний отчет" +
                " был принят более двух дней! Пожалуйста, сдайте отчет.");
    }

    private List<SendMessage> getActualSendMessages() {
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(bot, times(2)).execute(argumentCaptor.capture());
        return argumentCaptor.getAllValues();
    }

}

