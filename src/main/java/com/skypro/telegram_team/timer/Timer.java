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
    @Value("${telegram.bot.support.chat}")
    private long supportChatId;

    @Scheduled(cron = "0 0 14 * * ?")
// 14:00 MSK
    void trialPeriod() {


        String text = "Пробный период закончился!";
//        sendMessage(chatId, text);
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
