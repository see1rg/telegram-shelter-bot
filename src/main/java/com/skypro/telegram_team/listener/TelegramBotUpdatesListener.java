package com.skypro.telegram_team.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {
   final private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Value("${telegram.bot.token}")
    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                logger.info("Processing update: {}", update);
                if ("/start".equals(update.message().text())) {
                    Keyboard keyboard = new ReplyKeyboardMarkup
                            (new KeyboardButton("Узнать информацию о приюте"))
                            .addRow("Как взять собаку из приюта?")
                            .addRow("Прислать отчет о питомце")
                            .addRow("Позвать волонтера")
                            .oneTimeKeyboard(true);
                    SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                            "ok");
                    sendMessage.replyMarkup(keyboard);
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                } else if ("Узнать информацию о приюте".equals(update.message().text())) {
                    Keyboard keyboard = new ReplyKeyboardMarkup(
                            new KeyboardButton("Записать контактные данные для связи"))
                            .addRow("Позвать волонтера");
                    SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                            "Привет. Какая-то информация о приюте");
                    sendMessage.replyMarkup(keyboard);
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                } else if ("Записать контактные данные для связи".equals(update.message().text())) {
                    SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                            "Введите данные о себе в формате: ФИО, номер телефона");
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                } else if ("Как взять собаку из приюта?".equals(update.message().text())) {
                    Keyboard keyboard = new ReplyKeyboardMarkup
                            (new KeyboardButton("Узнать правила знакомства с собакой до того, как можно забрать ее из приюта"))
                            .addRow("Получить список документов, необходимых для того, чтобы взять собаку из приюта.")
                            .addRow("Получить список рекомендаций по транспортировке животного")
                            .addRow("Получить список рекомендаций по обустройству дома для щенка")
                            .addRow("Получить список рекомендаций по обустройству дома для взрослой собаки")
                            .addRow("Получить список рекомендаций по обустройству дома для собаки с ограниченными возможностями (зрение, передвижение)")
                            .addRow("Получить советы кинолога по первичному общению с собакой")
                            .addRow("Получить рекомендации по проверенным кинологам для дальнейшего обращения к ним")
                            .addRow("Получить список причин, почему могут отказать и не дать забрать собаку из приюта")
                            .addRow("Записать контактные данные для связи")
                            .addRow("Позвать волонтера")
                            .oneTimeKeyboard(true);
                    SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                            "ok");
                    sendMessage.replyMarkup(keyboard);
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                } else if ("Прислать отчет о питомце".equals(update.message().text())) {
                    Keyboard keyboard = new ReplyKeyboardMarkup
                            (new KeyboardButton("Получить форму ежедневного отчета"))
                            .addRow("Позвать волонтера")
                            .oneTimeKeyboard(true);
                    SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                            "ok");
                    sendMessage.replyMarkup(keyboard);
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                } else if ("Получить форму ежедневного отчета".equals(update.message().text())) {
                    SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                            """
                                    В ежедневный отчет входит следующая информация:
                                    - Фото животного.
                                    - Рацион животного.
                                    - Общее самочувствие и привыкание к новому месту.
                                    - Изменение в поведении: отказ от старых привычек, приобретение новых.""");
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CONFIRMED_UPDATES_ALL;
    }
}



