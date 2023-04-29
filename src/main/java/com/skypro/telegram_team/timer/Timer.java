package com.skypro.telegram_team.timer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.services.AnimalService;
import com.skypro.telegram_team.services.ReportService;
import com.skypro.telegram_team.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
@EnableScheduling
public class Timer {
    private final TelegramBot telegramBot;
    private final AnimalService animalService;
    private final ReportService reportService;
    private final UserService userService;
    @Value("${telegram.bot.support.chat}")
    private Long supportChatId;


    /**
     * Проверка и изменение статуса пользователей. У пользователей есть следующие состояния:
     * SEARCH - ищет животное для усыновления.
     * ACCEPTED - усыновление одобрено волонтером. Отправляется сообщение усыновителю и при следующей проверке,
     * статус будет изменен на ADOPTED для хранения в БД информации о пользователе.
     * REFUSE - в усыновлении отказано волонтером. Отправляется сообщение усыновителю и при следующей проверке,
     * будет заносен в черный список (BLACKLIST) как плохой усыновитель и отправлено сообщение.
     * DECISION - Отправляется сообщение усыновителю об окончании испытательного срока, волонтер принимает решение.
     * PROLONGED - испытательный срок продлен и отправлено сообщение усыновителю о сроке продления.
     * PROBATION - испытательный срок 30 дней, устанавливается автоматически при связывании животного с пользователем,
     * а так же при продлении испытательного срока на срок указанный волонтером.
     */
//    @Scheduled(cron = "0 16 05 * * *") // demo
    @Scheduled(cron = "0 0 9-18/3 * * *")
    void checkAndChangeUsersStatus() {

        List<User> acceptedUsers = changeStateAcceptedToAdoptedAndCollect();
        List<Animal> acceptedAnimals = changeStateAcceptedToHappyEndAndCollect();

        List<User> refusedUsers = changeStateRefusedToBlackListAndCollect();
        List<Animal> backInShelterAnimals = changeStateRefusedToInShelterListAndCollect();

        List<User> prolongedUsers = findStateProlongedAndCollect();

        List<User> decisionAboutUsers = decisionMakingOfVolunteersAboutUsers();

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


    List<User> changeStateAcceptedToAdoptedAndCollect() {
        log.info("Проверяем и изменяем статус пользователей со статусом ACCEPTED на ADOPTED");
        List<User> sortUsersWithStateAccepted = userService.findByState(User.OwnerStateEnum.ACCEPTED).stream()
                .peek(user -> user.setState(User.OwnerStateEnum.ADOPTED)).toList();

        sortUsersWithStateAccepted.forEach(user -> {
            sendMessage(user.getTelegramId(),
                    String.format("Уважаемый %s %s Поздравляем, вы прошли пробный период!",
                            user.getName(), user.getSurname()));
            userService.findVolunteers().forEach(volunteer -> sendMessage(volunteer.getId(),
                    String.format("Одобрение на усыновление подтверждено у %s %s.",
                            user.getName(), user.getSurname())));

        });
        return sortUsersWithStateAccepted;
    }

    List<User> changeStateRefusedToBlackListAndCollect() {
        log.info("Проверяем и изменяем статус пользователей со статусом REFUSE на BLACKLIST");
        List<User> sortUsersWithStateRefused = userService.findByState(User.OwnerStateEnum.REFUSE).stream()
                .peek(user -> user.setState(User.OwnerStateEnum.BLACKLIST)).toList();

        sortUsersWithStateRefused.stream()
                .peek(user -> user.setState(User.OwnerStateEnum.BLACKLIST))
                .forEach(user -> {
                    sendMessage(user.getTelegramId(),
                            String.format("Уважаемый %s %s Вы НЕ прошли пробный период! " +
                                    "Пожалуйста сдайте собаку в приют!", user.getName(), user.getSurname()));
                    userService.findVolunteers().forEach(volunteer -> sendMessage(volunteer.getId(),
                            String.format("Отказ подтвержден у %s %s.",
                                    user.getName(), user.getSurname())));
                });
        return sortUsersWithStateRefused;
    }

    List<User> findStateProlongedAndCollect() {
        log.info("Проверяем статус пользователей со статусом PROLONGED и сообщаем.");
        List<User> prolongedUsers = userService.findByState(User.OwnerStateEnum.PROLONGED).stream()
                .toList();

        prolongedUsers.stream()
                .peek(user -> user.setState(User.OwnerStateEnum.PROBATION))
                .forEach(user -> {
                    sendMessage(user.getTelegramId(), String.format(
                            "Уважаемый %s %s, мы решили продлить пробный период на %s дней!",
                            user.getName(), user.getSurname(),
                            Duration.between(user.getEndTest(), LocalDateTime.now()).toDays()));
                    userService.findVolunteers().forEach(volunteer -> sendMessage(volunteer.getId(),
                            String.format("Подтверждено продление у %s %s на %s дней!",
                                    user.getName(), user.getSurname(),
                                    Duration.between(user.getEndTest(), LocalDateTime.now()).toDays())));
                });
        return prolongedUsers;
    }

    List<User> decisionMakingOfVolunteersAboutUsers() {
        log.info("Проверяем статус пользователей со статусом PROBATION на DECISION");
        List<User> decisionAboutUsers = userService.findByState(User.OwnerStateEnum.PROBATION).stream()
                .filter(user -> user.getEndTest().isBefore(LocalDateTime.now()))
                .toList();

        userService.findByState(User.OwnerStateEnum.DECISION).addAll(decisionAboutUsers);

        decisionAboutUsers.stream()
                .peek(user -> user.setState(User.OwnerStateEnum.DECISION))
                .forEach(user -> {
                    sendMessage(user.getTelegramId(), String.format(
                            "Уважаемый %s %s, у Вас закончился испытательный срок," +
                                    " пожалуйста дождитесь принятия решения волонтером о вашем животном!",
                            user.getName(), user.getSurname()));

                    userService.findVolunteers().forEach(volunteer -> sendMessage(volunteer.getId(),
                            String.format("Принять решение об усыновлении" +
                                    " животного у %s %s.", user.getName(), user.getSurname())));
                });
        return decisionAboutUsers;
    }


    List<Animal> changeStateRefusedToInShelterListAndCollect() {
        return animalService.findByUserState(User.OwnerStateEnum.BLACKLIST).stream().
                peek(animal -> animal.setState(Animal.AnimalStateEnum.IN_SHELTER)).toList();
    }

    List<Animal> changeStateAcceptedToHappyEndAndCollect() {
        return animalService.findByUserState(User.OwnerStateEnum.ADOPTED).stream().
                peek(animal -> animal.setState(Animal.AnimalStateEnum.HAPPY_END)).toList();
    }

//    @Scheduled(cron = "0 02 06 * * *") // demo
    @Scheduled(cron = "0 0 8-20/4 * * *")
// every 4 hours from 8 to 20 (cron = "0 40 21 * * *")
    void checkingDailyAndTwoDaysReportFromUsers() {
        log.info("Проверяем отчеты за день и за два дня от пользователей");

        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2).withHour(0);
        LocalDateTime yesterdayAt0AM = LocalDateTime.now().minusDays(1).withHour(0);

        List<Animal> animals = animalService.findAllByUserIdNotNullAndState(Animal.AnimalStateEnum.IN_TEST);
        List<User> usersWithoutReportForTwoDays = new ArrayList<>();
        List<User> usersWithoutDailyReport = new ArrayList<>();

        animals.forEach(animal -> {
            User user = animal.getUser();
            int testDays = 30;
            List<Report> reports = reportService.findByAnimalId(animal.getId());
            if (reports.size() == 0 && user.getEndTest().minusDays(testDays-1).isBefore(LocalDateTime.now())) {
                usersWithoutDailyReport.add(user);
            }
            if ((reports.size() == 0 && user.getEndTest().minusDays(testDays-2).isBefore(LocalDateTime.now())) ||
                    (reports.size() != 0 && reports.get(reports.size() - 1).getDate().isBefore(twoDaysAgo))) {
                usersWithoutReportForTwoDays.add(user);
            } else if (reports.size() != 0 && !usersWithoutReportForTwoDays.contains(user) &&
                    reports.get(reports.size() - 1).getDate().isBefore(yesterdayAt0AM)) {
                usersWithoutDailyReport.add(user);
            }
        });

        usersWithoutReportForTwoDays.forEach(user -> {
                sendMessage(getVolunteerChatIdOrSupportChatId(),
                        String.format("Последний отчет был принят более двух дней у : %s %s.",
                                user.getName(), user.getSurname()));
                sendMessage(user.getTelegramId(),
                        "Последний отчет был принят более двух дней! Пожалуйста, сдайте отчет.");});

        usersWithoutDailyReport.forEach(user -> sendMessage(user.getTelegramId(),
                "Здравствуйте, вчера от вас не поступал отчет о собаке. Пожалуйста, сдайте отчет."));
    }

    /**
     * Отправляет текстовое сообщение в заданный чат.
     *
     * @param chatId идентификатор чата, куда нужно отправить сообщение
     * @param text   текст сообщения
     */
    private void sendMessage(long chatId, String text) {
        log.info("Отправляем сообщение в чат {} сообщением {}", chatId, text);
        SendMessage request = new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true);
        telegramBot.execute(request);
    }

    /**
     * Ищет любого волонтера в БД
     *
     * @return chatId волонтера или, в случае отсутствия отправляет {@link #supportChatId} службы поддержки
     */
    private long getVolunteerChatIdOrSupportChatId() {
        return userService.findAnyVolunteer()
                .map(User::getTelegramId)
                .orElse(supportChatId);
    }

}
