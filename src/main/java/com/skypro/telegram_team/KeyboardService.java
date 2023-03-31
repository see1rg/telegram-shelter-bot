package com.skypro.telegram_team;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.skypro.telegram_team.listener.TelegramBotUpdatesListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeyboardService {
    Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    public void getResponse(List<Update> updates, TelegramBot telegramBot) {
        try {
            updates.forEach(update -> {
                logger.info("Update processing: {} ", update);
                if (update.message() != null) {
                    if ("/start".equals(update.message().text())) {
                        Keyboard keyboard = new ReplyKeyboardMarkup
                                (new KeyboardButton("Узнать информацию о приюте"))
                                .addRow("Как взять собаку из приюта?")
                                .addRow("Прислать отчет о питомце")
                                .addRow("Позвать волонтера")
                                .resizeKeyboard(true);
                        SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                                "ok");
                        sendMessage.replyMarkup(keyboard);
                        SendResponse sendResponse = telegramBot.execute(sendMessage);
                    } else if ("Узнать информацию о приюте".equals(update.message().text())) {
                        InlineKeyboardMarkup markup = new InlineKeyboardMarkup
                                (new InlineKeyboardButton("Записать контактные данные для связи")
                                        .callbackData("контактные данные для связи"))
                                .addRow(new InlineKeyboardButton("Позвать волонтера")
                                        .callbackData("вызов волонтера"));
                        SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                                "*Должна выводиться какая-то информация о приюте*");
                        sendMessage.replyMarkup(markup).parseMode(ParseMode.Markdown);
                        SendResponse sendResponse = telegramBot.execute(sendMessage);
                    } else if ("Как взять собаку из приюта?".equals(update.message().text())) {
                        InlineKeyboardMarkup markup = new InlineKeyboardMarkup
                                (new InlineKeyboardButton("1")
                                        .callbackData("1")
                                        , (new InlineKeyboardButton("2")
                                        .callbackData("2")))
                                .addRow(new InlineKeyboardButton("3")
                                                .callbackData("3")
                                        , (new InlineKeyboardButton("4")
                                                .callbackData("4")))
                                .addRow(new InlineKeyboardButton("5")
                                                .callbackData("5")
                                        , (new InlineKeyboardButton("6")
                                                .callbackData("6")))
                                .addRow(new InlineKeyboardButton("7")
                                                .callbackData("7")
                                        , (new InlineKeyboardButton("8")
                                                .callbackData("8")))
                                .addRow(new InlineKeyboardButton("9")
                                                .callbackData("9")
                                        , (new InlineKeyboardButton("10")
                                                .callbackData("10")))
                                .addRow(new InlineKeyboardButton("11")
                                        .callbackData("11"));
                        SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                                "1.Узнать правила знакомства с собакой до того, как можно забрать ее из приюта\n" +
                                        "2.Получить список документов, необходимых для того, чтобы взять собаку из приюта\n" +
                                        "3.Получить список рекомендаций по транспортировке животного\n" +
                                        "4.Получить список рекомендаций по обустройству дома для щенка\n" +
                                        "5.Получить список рекомендаций по обустройству дома для взрослой собаки\n" +
                                        "6.Получить список рекомендаций по обустройству дома для собаки с ограниченными возможностями (зрение, передвижение)\n" +
                                        "7.Получить советы кинолога по первичному общению с собакой\n" +
                                        "8.Получить рекомендации по проверенным кинологам для дальнейшего обращения к ним\n" +
                                        "9.Получить список причин, почему могут отказать и не дать забрать собаку из приюта\n" +
                                        "10.Записать контактные данные для связи\n" +
                                        "11.Позвать волонтера\n");
                        sendMessage.replyMarkup(markup);
                        SendResponse sendResponse = telegramBot.execute(sendMessage);
                    } else if ("Прислать отчет о питомце".equals(update.message().text())) {
                        InlineKeyboardMarkup markup = new InlineKeyboardMarkup
                                (new InlineKeyboardButton("Получить форму ежедневного отчета").callbackData("форма отчета"))
                                .addRow(new InlineKeyboardButton("Позвать волонтера").callbackData("вызов волонтера"));
                        SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                                "ок");
                        sendMessage.replyMarkup(markup);
                        SendResponse sendResponse = telegramBot.execute(sendMessage);
                    } else if ("Позвать волонтера".equals(update.message().text())) {
                        SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                                "ожидайте");
                        SendResponse sendResponse = telegramBot.execute(sendMessage);
                    }
                } else if ("контактные данные для связи".equals(update.callbackQuery().data())) {
                    SendMessage sendMessage = new SendMessage(update.callbackQuery().message().chat().id(),
                            "Введите данные о себе в формате: ФИО, номер телефона");
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                } else if ("вызов волонтера".equals(update.callbackQuery().data())) {
                    SendMessage sendMessage = new SendMessage(update.callbackQuery().message().chat().id(),
                            "ожидайте");
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                } else if ("форма отчета".equals(update.callbackQuery().data())) {
                    SendMessage sendMessage = new SendMessage(update.callbackQuery().message().chat().id(),
                            "В ежедневный отчет входит следующая информация:\n" +
                                    "1.Фото животного.\n" +
                                    "2.Рацион животного.\n" +
                                    "3.Общее самочувствие и привыкание к новому месту.\n" +
                                    "4.Изменение в поведении: отказ от старых привычек, приобретение новых.");
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


