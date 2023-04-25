package com.skypro.telegram_team.keyboards;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import com.skypro.telegram_team.listener.TelegramBotUpdatesListener;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.skypro.telegram_team.keyboards.Command.*;

/**
 * Класс для работы с апдэйтами и клавиатурой в телеграмботе
 */
@Service
public class KeyboardService {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    /**
     * Метод для прослушивания апдэйтов и ответа на них с использованием клавиатуры
     * использует методы TelegramBot {@link TelegramBot#execute(BaseRequest)}
     * {@link SendMessage#replyMarkup(Keyboard)}
     *
     * @param updates список объектов типа Update, содержащих информацию о новых сообщениях от пользователей
     * @param telegramBot объект типа TelegramBot, используемый для отправки сообщений в Telegram
     */
    public void getResponse(List<Update> updates, TelegramBot telegramBot) {
        try {
            updates.forEach(update -> {
                logger.info("Update processing: {} ", update);
                if (update.message() != null) {
                    if (START.getCommandName().equals(update.message().text())) {
                        Keyboard keyboard = new ReplyKeyboardMarkup
                                (new KeyboardButton(GET_SHELTER_INFO.getCommandName()))
                                .addRow(GET_SHELTER_INFO.getCommandName())
                                .addRow(SEND_DOG_REPORT.getCommandName())
                                .addRow(CALL_VOLUNTEER.getCommandName())
                                .resizeKeyboard(true);
                        String dogEmoji = EmojiParser.parseToUnicode(":dog:");
                        SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                                dogEmoji);
                        sendMessage.replyMarkup(keyboard);
                        SendResponse sendResponse = telegramBot.execute(sendMessage);
                    } else if (GET_SHELTER_INFO.getCommandName().equals(update.message().text())) {
                        InlineKeyboardMarkup markup = new InlineKeyboardMarkup
                                (new InlineKeyboardButton(WRITE_DOWN_CONTACT_INFORMATION.getCommandName())
                                        .callbackData(WRITE_DOWN_CONTACT_INFORMATION.getCallBack()))
                                .addRow(new InlineKeyboardButton(CALL_VOLUNTEER.getCommandName())
                                        .callbackData(CALL_VOLUNTEER.getCallBack()));
                        SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                                "Должна выводиться какая-то информация о приюте");
                        sendMessage.replyMarkup(markup);
                        SendResponse sendResponse = telegramBot.execute(sendMessage);
                    } else if (GET_DOG.getCommandName().equals(update.message().text())) {
                        InlineKeyboardMarkup markup = new InlineKeyboardMarkup
                                (new InlineKeyboardButton(LEARN_RULES_OF_DATING_DOG.getCommandName())
                                        .callbackData(LEARN_RULES_OF_DATING_DOG.getCallBack())
                                        , new InlineKeyboardButton(GET_LIST_OF_DOCUMENTS.getCommandName())
                                        .callbackData(GET_LIST_OF_DOCUMENTS.getCallBack()),
                                        new InlineKeyboardButton(GET_RECOMMENDATIONS_FOR_TRANSPORTING.getCommandName())
                                                .callbackData(GET_RECOMMENDATIONS_FOR_TRANSPORTING.getCallBack()))
                                .addRow(new InlineKeyboardButton(GET_TIPS_FOR_PUPPY.getCommandName())
                                                .callbackData(GET_TIPS_FOR_PUPPY.getCallBack()),
                                        new InlineKeyboardButton(GET_TIPS_FOR_ADULT_DOG.getCommandName())
                                                .callbackData(GET_TIPS_FOR_ADULT_DOG.getCallBack())
                                        , new InlineKeyboardButton(GET_TIPS_FOR_DOG_WITH_DISABILITY.getCommandName())
                                                .callbackData(GET_TIPS_FOR_DOG_WITH_DISABILITY.getCallBack()))
                                .addRow(new InlineKeyboardButton(GET_ADVICE_FROM_CYNOLOGIST.getCommandName())
                                                .callbackData(GET_ADVICE_FROM_CYNOLOGIST.getCallBack())
                                        , (new InlineKeyboardButton(GET_RECOMMENDATIONS_ON_PROVEN_CYNOLOGISTS.getCommandName())
                                                .callbackData(GET_RECOMMENDATIONS_ON_PROVEN_CYNOLOGISTS.getCallBack())),
                                        new InlineKeyboardButton(GET_REASONS_FOR_REFUSING.getCommandName())
                                                .callbackData(GET_REASONS_FOR_REFUSING.getCallBack()))
                                .addRow(new InlineKeyboardButton(WRITE_DOWN_CONTACT_INFORMATION.getCommandName())
                                        .callbackData(WRITE_DOWN_CONTACT_INFORMATION.getCallBack()))
                                .addRow(new InlineKeyboardButton(CALL_VOLUNTEER.getCommandName())
                                        .callbackData(CALL_VOLUNTEER.getCallBack()));
                        SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                                "1.Узнать правила знакомства с собакой до того, как можно забрать ее из приюта\n" +
                                        "2.Получить список документов, необходимых для того, чтобы взять собаку из приюта\n" +
                                        "3.Получить список рекомендаций по транспортировке животного\n" +
                                        "4.Получить список рекомендаций по обустройству дома для щенка\n" +
                                        "5.Получить список рекомендаций по обустройству дома для взрослой собаки\n" +
                                        "6.Получить список рекомендаций по обустройству дома для собаки с ограниченными возможностями (зрение, передвижение)\n" +
                                        "7.Получить советы кинолога по первичному общению с собакой\n" +
                                        "8.Получить рекомендации по проверенным кинологам для дальнейшего обращения к ним\n" +
                                        "9.Получить список причин, почему могут отказать и не дать забрать собаку из приюта\n");
                        sendMessage.replyMarkup(markup);
                        SendResponse sendResponse = telegramBot.execute(sendMessage);
                    } else if (Command.SEND_DOG_REPORT.getCommandName().equals(update.message().text())) {
                        InlineKeyboardMarkup markup = new InlineKeyboardMarkup
                                (new InlineKeyboardButton(GET_DAILY_REPORT.getCommandName())
                                        .callbackData(GET_DAILY_REPORT.getCallBack()))
                                .addRow(new InlineKeyboardButton(CALL_VOLUNTEER.getCommandName())
                                        .callbackData(CALL_VOLUNTEER.getCallBack()));
                        String dogEmoji = EmojiParser.parseToUnicode(":dog:");
                        SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                                dogEmoji + "Выбери нужное");
                        sendMessage.replyMarkup(markup);
                        SendResponse sendResponse = telegramBot.execute(sendMessage);
                    } else if (Command.CALL_VOLUNTEER.getCommandName().equals(update.message().text())) {
                        SendMessage sendMessage = new SendMessage(update.message().chat().id(),
                                "ожидайте");
                        SendResponse sendResponse = telegramBot.execute(sendMessage);
                    }
                } else if (Command.WRITE_DOWN_CONTACT_INFORMATION.getCallBack().equals(update.callbackQuery().data())) {
                    SendMessage sendMessage = new SendMessage(update.callbackQuery().message().chat().id(),
                            "Введите данные о себе в формате: ФИО, номер телефона");
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                } else if (Command.CALL_VOLUNTEER.getCallBack().equals(update.callbackQuery().data())) {
                    SendMessage sendMessage = new SendMessage(update.callbackQuery().message().chat().id(),
                            "ожидайте");
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                } else if (GET_DAILY_REPORT.getCallBack().equals(update.callbackQuery().data())) {
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


