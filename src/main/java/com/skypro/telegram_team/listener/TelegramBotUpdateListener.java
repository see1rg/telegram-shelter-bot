package com.skypro.telegram_team.listener;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TelegramBotUpdateListener {
    private final TelegramBotMessageListener messageListener;
    private final TelegramBotCallbackListener callbackListener;

    public TelegramBotUpdateListener(TelegramBotMessageListener messageListener,
                                     TelegramBotCallbackListener callbackListener) {
        this.messageListener = messageListener;
        this.callbackListener = callbackListener;
    }

    /**
     * Обработка отдельного Update
     *
     * @param update отдельный update для обработки
     * @return сообщения для отправки пользователю
     */
    public List<SendMessage> processUpdate(Update update) {
        List<SendMessage> sendMessages;
        if (update.callbackQuery() == null) {
            sendMessages = processMessage(update.message());
        } else {
            sendMessages = processCallback(update.callbackQuery());
        }
        return sendMessages;
    }

    /**
     * Обработка сообщений в последовательности:
     * 1. текстовые сообщения от главного меню
     * 2. сообщения от пользователя
     * 3. ответные сообщения
     *
     * @param message сообщение от пользователя
     * @return сообщения для отправки пользователю
     */
    private List<SendMessage> processMessage(Message message) {
        return messageListener.processMessage(message);
    }

    /**
     * Обработка сообщений с callback
     * (при нажатии на меню inline keyboard)
     *
     * @param callbackQuery команды inline keyboard
     * @return сообщения для отправки пользователю
     */
    private List<SendMessage> processCallback(CallbackQuery callbackQuery) {
        return callbackListener.processCallback(callbackQuery);
    }
}