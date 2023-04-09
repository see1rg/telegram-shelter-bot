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
        List<User> acceptedUsers = new ArrayList<>();
        List<User> refusedUsers = new ArrayList<>();
        List<User> prolongedUsers = new ArrayList<>();

        for (User user : allUsers) {
            if (User.OwnerStateEnum.ACCEPTED.equals(user.getState())) {
                acceptedUsers.add(user);
            } else if (User.OwnerStateEnum.REFUSE.equals(user.getState())) {
                refusedUsers.add(user);
            } else if (User.OwnerStateEnum.PROLONGED.equals(user.getState())) {
                prolongedUsers.add(user);
            }
        }

        for (User user : acceptedUsers) {
            sendMessage(user.getTelegramId(), "Уважаемый " + user.getName() + " " + user.getSurname() +
                    " Поздравляем, вы прошли пробный период!");
            sendMessage(supportChatId, "Пробный период закончился у " + user.getName() + " " + user.getSurname());
        }

        for (User user : refusedUsers) {
            sendMessage(user.getTelegramId(), "Уважаемый " + user.getName() + " " + user.getSurname() +
                    " Вы НЕ прошли пробный период! Пожалуйста сдайте собаку в приют!");
            sendMessage(supportChatId, "Отказ подтвержден у " + user.getName() + " " + user.getSurname());
        }

        for (User user : prolongedUsers) {
            sendMessage(user.getTelegramId(), "Уважаемый " + user.getName() + " " + user.getSurname() +
                    " Вы НЕ прошли пробный период! Пожалуйста сдайте собаку в приют!");
            sendMessage(supportChatId, "Пробный период закончился у " + user.getName() + " " + user.getSurname());
        }
    }


    @Scheduled(cron = "0 0 8-20/4 * * *")
// every 4 hours from 8 to 20
    void checkingDailyReportFromUsers() {

        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        List<Animal> animals = animalService.findAllByUserIdNotNullAndState(Animal.AnimalStateEnum.IN_TEST);
        List<User> usersWithoutDailyReports = new ArrayList<>();

        for (Animal animal : animals) {
            User user = animal.getUser();

            List<Report> reports = reportService.findByAnimalId(animal.getId());
            if (reports.isEmpty() || reports.get(reports.size() - 1).getDate().isBefore(twoDaysAgo)) {
                usersWithoutDailyReports.add(user);
            }
        }
        for (User user : usersWithoutDailyReports) {
            sendMessage(supportChatId,
                    "Последний отчет был написан более двух дней у :" + user.getName() + " " + user.getSurname());
            sendMessage(user.getTelegramId(),
                    "Последний отчет был написан более двух дней! Пожалуйста, сдайте отчет.");
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
