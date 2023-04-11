package com.skypro.telegram_team.timer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.services.AnimalService;
import com.skypro.telegram_team.services.ReportService;
import com.skypro.telegram_team.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class Timer {
    private final TelegramBot telegramBot;
    private final AnimalService animalService;
    private final ReportService reportService;
    private final UserService userService;
    @Value("${telegram.bot.support.chat}")
    private long supportChatId;

    /** Проверка и изменение статуса пользователей. У пользователей есть следующие состояния:
     * ACCEPTED - усыновление одобрено и при следующей проверке статус будет изменен на ADOPTED
     * для хранения в БД информации о пользователе
     *
     */
    @Scheduled(cron = "0 0 9-18/3 * * *")
    void checkAndChangeUsersStatus() {

        List<User> allUsers = userService.findAll();
        List<Animal> allAnimals = animalService.findAll();

        List<User> acceptedUsers = changeStateAcceptedToAdoptedAndCollect(allUsers); // изменяем статус на ADOPTED у user
        List<Animal> acceptedAnimals = changeStateAcceptedToHappyEndAndCollect(allAnimals); // изменяем статус на HAPPY_END у animal

        List<User> refusedUsers = changeStateRefusedToBlackListAndCollect(allUsers); // изменяем статус на BLACKLIST у user
        List<Animal> backInShelterAnimals = changeStateRefusedToInShelterListAndCollect(allAnimals); // изменяем статус на IN_SHELTER_LIST у animal

        List<User> prolongedUsers = findStateProlongedAndCollect(allUsers); // изменяем статус на PROLONGED у user

        List<User> decisionAboutUsers = decisionMakingOfVolunteersAboutUsers(allUsers); // изменяем статус на DECISION_ABOUT_USERS у user

        List<User> saveChangesOfUsers = new ArrayList<>();
        saveChangesOfUsers.addAll(acceptedUsers);
        saveChangesOfUsers.addAll(refusedUsers);
        saveChangesOfUsers.addAll(prolongedUsers);
        saveChangesOfUsers.addAll(decisionAboutUsers); // объединяем все в один массив

        List<Animal> saveChangesOfAnimals = new ArrayList<>();
        saveChangesOfAnimals.addAll(acceptedAnimals);
        saveChangesOfAnimals.addAll(backInShelterAnimals); // объединяем все в один массив

        saveChangesOfUsers.forEach(user -> userService.update(user, user.getId())); // обновляем изменения в БД
        saveChangesOfAnimals.forEach(animal -> animalService.update(animal, animal.getId()));
    }


    private List<User> changeStateAcceptedToAdoptedAndCollect(List<User> allUsers) {
        List<User> sortUsersWithStateAccepted = allUsers.stream()
                .filter(user -> User.OwnerStateEnum.ACCEPTED.equals(user.getState()))
                .peek(user -> user.setState(User.OwnerStateEnum.ADOPTED)).toList();

        sortUsersWithStateAccepted.forEach(user -> {
            sendMessage(user.getTelegramId(),
                    String.format("Уважаемый %s %s Поздравляем, вы прошли пробный период!",
                            user.getName(),user.getSurname()));
            sendMessage(supportChatId,
                    String.format("Одобрение на усыновление подтверждено у %s %s.",
                            user.getName(), user.getSurname()));
        });
        return sortUsersWithStateAccepted;
    }

    private List<User> changeStateRefusedToBlackListAndCollect(List<User> allUsers) {
        List<User> sortUsersWithStateRefused = allUsers.stream()
                .filter(user -> User.OwnerStateEnum.REFUSE.equals(user.getState()))
                .peek(user -> user.setState(User.OwnerStateEnum.BLACKLIST)).toList();

        sortUsersWithStateRefused.stream()
                .peek(user -> user.setState(User.OwnerStateEnum.BLACKLIST))
                .forEach(user -> {
                    sendMessage(user.getTelegramId(),
                            String.format("Уважаемый %s %s Вы НЕ прошли пробный период! " +
                                    "Пожалуйста сдайте собаку в приют!", user.getName(), user.getSurname()));
                    sendMessage(supportChatId, String.format("Отказ подтвержден у %s %s.",
                            user.getName(), user.getSurname()));
                });
        return sortUsersWithStateRefused;
    }

    private List<User> findStateProlongedAndCollect(List<User> allUsers) {
        List<User> prolongedUsers = allUsers.stream()
                .filter(user -> User.OwnerStateEnum.PROLONGED.equals(user.getState()))
                .toList();

        prolongedUsers.stream()
                .peek(user -> user.setState(User.OwnerStateEnum.PROBATION))
                .forEach(user -> {
                    sendMessage(user.getTelegramId(), String.format(
                            "Уважаемый %s %s, мы решили продлить пробный период на %s дней!",
                            user.getName(), user.getSurname(),
                            Duration.between(user.getEndTest(), LocalDateTime.now()).toDays()));
                    sendMessage(supportChatId, String.format("Продлено у %s %s на %s дней!",
                            user.getName(), user.getSurname(),
                             Duration.between(user.getEndTest(), LocalDateTime.now()).toDays()));
                });
        return prolongedUsers;
    }

    private List<User> decisionMakingOfVolunteersAboutUsers(List<User> allUsers) {
        List<User> decisionAboutUsers = allUsers.stream()
                .filter(user -> user.getEndTest().isAfter(LocalDateTime.now()))
                .toList();

        decisionAboutUsers.stream()
                .peek(user -> user.setState(User.OwnerStateEnum.DECISION))
                .forEach(user -> {
                    sendMessage(user.getTelegramId(), String.format(
                            "Уважаемый %s %s, у Вас закончился испытательный срок," +
                            " пожалуйста дождитесь принятия решения волонтером о вашем животном!",
                            user.getName(), user.getSurname()));
                    sendMessage(supportChatId, String.format("Отказ подтвержден у %s %s.",
                            user.getName(), user.getSurname()));
                });
        return decisionAboutUsers;
    }


    private List<Animal> changeStateRefusedToInShelterListAndCollect(List<Animal> allAnimals) {
        return allAnimals.stream().filter(animal -> (animal.getUser().getState().equals(User.OwnerStateEnum.BLACKLIST))).
                peek(animal -> animal.setState(Animal.AnimalStateEnum.IN_SHELTER)).toList();
    }

    private List<Animal> changeStateAcceptedToHappyEndAndCollect(List<Animal> allAnimals) {
        return allAnimals.stream().filter(animal -> (animal.getUser().getState().equals(User.OwnerStateEnum.ADOPTED))).
                peek(animal -> animal.setState(Animal.AnimalStateEnum.HAPPY_END)).toList();
    }


    @Scheduled(cron = "0 0 8-20/4 * * *")
// every 4 hours from 8 to 20
    void checkingDailyAndTwoDaysReportFromUsers() {

        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2).withHour(0);
        LocalDateTime yesterdayAt0AM = LocalDateTime.now().minusDays(1).withHour(0);

        List<Animal> animals = animalService.findAllByUserIdNotNullAndState(Animal.AnimalStateEnum.IN_TEST);
        List<User> usersWithoutReportForTwoDays = new ArrayList<>();
        List<User> usersWithoutDailyReport = new ArrayList<>();

        animals.forEach(animal -> {
            User user = animal.getUser();
            List<Report> reports = reportService.findByAnimalId(animal.getId());
            if (reports.isEmpty() || reports.get(reports.size() - 1).getDate().isBefore(yesterdayAt0AM)) {
                usersWithoutDailyReport.add(user);
            }
            if (reports.get(reports.size() - 1).getDate().isBefore(twoDaysAgo)) {
                usersWithoutReportForTwoDays.add(user);
            }
        });

        usersWithoutReportForTwoDays.forEach(user -> {
            sendMessage(supportChatId,
                    String.format("Последний отчет был принят более двух дней у : %s %s.",
                            user.getName(), user.getSurname()));
            sendMessage(user.getTelegramId(),
                    "Последний отчет был принят более двух дней! Пожалуйста, сдайте отчет.");
        });

        usersWithoutDailyReport.forEach(user -> sendMessage(user.getTelegramId(),
                "Здравствуйте, вчера от вас не поступал отчет о собаке. Пожалуйста, сдайте отчет."));

    }

    private BaseResponse sendMessage(long chatId, String text) {
        SendMessage request = new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true);
        return telegramBot.execute(request);
    }
}
