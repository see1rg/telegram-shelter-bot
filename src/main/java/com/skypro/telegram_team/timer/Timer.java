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

    @Scheduled(cron = "0 0 9-18/3 * * *")
    void endTrialPeriod() {
        List<User> allUsers = userService.findAll();

        List<User> acceptedUsers = allUsers.stream()
                .filter(user -> User.OwnerStateEnum.ACCEPTED.equals(user.getState()))
                .toList();

        acceptedUsers.stream()
                .peek(user -> user.setState(User.OwnerStateEnum.SEARCH))
                .forEach(user -> {
                    sendMessage(user.getTelegramId(), "Уважаемый " + user.getName() + " " + user.getSurname() +
                            " Поздравляем, вы прошли пробный период!");
                    sendMessage(supportChatId, "Пробный период закончился у " + user.getName() + " " + user.getSurname());
                });


        List<User> refusedUsers = allUsers.stream()
                .filter(user -> User.OwnerStateEnum.REFUSE.equals(user.getState()))
                .toList();

        refusedUsers.stream()
                .peek(user -> user.setState(User.OwnerStateEnum.BLACKLIST))
                .forEach(user -> {
                    sendMessage(user.getTelegramId(), "Уважаемый " + user.getName() + " " + user.getSurname() +
                            " Вы НЕ прошли пробный период! Пожалуйста сдайте собаку в приют!");
                    sendMessage(supportChatId, "Отказ подтвержден у " + user.getName() + " " + user.getSurname());
                });

        List<User> prolongedUsers = allUsers.stream()
                .filter(user -> User.OwnerStateEnum.PROLONGED.equals(user.getState()))
                .toList();

        prolongedUsers.stream()
                .peek(user -> user.setState(User.OwnerStateEnum.PROBATION))
                .forEach(user -> {
                    sendMessage(user.getTelegramId(), "Уважаемый " + user.getName() + " " + user.getSurname() +
                            " Мы решили продлить пробный период на "
                            + Duration.between(user.getEndTrialPeriod(), LocalDateTime.now()).toDays() + " дней!");
                    sendMessage(supportChatId, "Продлено у " + user.getName() + " " + user.getSurname()
                            + " на " + Duration.between(user.getEndTrialPeriod(), LocalDateTime.now()).toDays() + " дней!");
                });

        List<User> decisionMakingOfVolunteersAboutUsers = allUsers.stream()
                .filter(user -> user.getEndTrialPeriod().isAfter(LocalDateTime.now()))
                .toList();

        decisionMakingOfVolunteersAboutUsers.stream()
                .peek(user -> user.setState(User.OwnerStateEnum.DECISION))
                .forEach(user -> {
                    sendMessage(user.getTelegramId(), "Уважаемый " + user.getName() + " " + user.getSurname() +
                            " Закончился испытательный срок, пожалуйста дождитесь принятия решения волонтером о вашем животном!");
                    sendMessage(supportChatId, "Отказ подтвержден у " + user.getName() + " " + user.getSurname());
                });

        List<User> saveChangeOfUsers = new ArrayList<>();
        saveChangeOfUsers.addAll(refusedUsers);
        saveChangeOfUsers.addAll(prolongedUsers);
        saveChangeOfUsers.addAll(decisionMakingOfVolunteersAboutUsers);
        saveChangeOfUsers.forEach(userService::save); // сохраняем изменения в БД

    }


    @Scheduled(cron = "0 0 8-20/4 * * *")
// every 4 hours from 8 to 20
    void checkingDailyAndTwoDaysReportFromUsers() {

        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2).withHour(0);
        LocalDateTime yesterdayAt0AM = LocalDateTime.now().minusDays(1).withHour(0);

        List<Animal> animals = animalService.findAllByUserIdNotNullAndState(Animal.AnimalStateEnum.IN_TEST);
        List<User> usersWithoutReportForTwoDays = new ArrayList<>();
        List<User> usersWithoutDailyReport = new ArrayList<>();

        for (Animal animal : animals) {
            User user = animal.getUser();

            List<Report> reports = reportService.findByAnimalId(animal.getId());
            if (reports.isEmpty() || reports.get(reports.size() - 1).getDate().isBefore(yesterdayAt0AM)) {
                usersWithoutDailyReport.add(user);
            }
            if (reports.get(reports.size() - 1).getDate().isBefore(twoDaysAgo)) {
                usersWithoutReportForTwoDays.add(user);
            }
        }

        for (User user : usersWithoutReportForTwoDays) {
            sendMessage(supportChatId,
                    "Последний отчет был принят более двух дней у :" + user.getName() + " " + user.getSurname());
            sendMessage(user.getTelegramId(),
                    "Последний отчет был принят более двух дней! Пожалуйста, сдайте отчет.");
        }

        for (User user : usersWithoutDailyReport) {
            sendMessage(user.getTelegramId(),
                    "Здравствуйте, вчера от вас не поступал отчет о собаке. Пожалуйста, сдайте отчет.");
        }

    }

    private BaseResponse sendMessage(long chatId, String text) {
        SendMessage request = new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true);
        return telegramBot.execute(request);
    }
}
