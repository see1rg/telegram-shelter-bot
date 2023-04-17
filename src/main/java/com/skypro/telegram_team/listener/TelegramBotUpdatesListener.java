package com.skypro.telegram_team.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.skypro.telegram_team.keyboards.KeyboardServiceExt;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final KeyboardServiceExt keyboardService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, KeyboardServiceExt keyboardService) {
        this.telegramBot = telegramBot;
        this.keyboardService = keyboardService;
    }

    /**
     * Инициализирует компонент, устанавливая этот экземпляр в качестве слушателя обновлений телеграм-бота.
     */
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /**
     * Обрабатывает список обновлений, используя сервис клавиатур.
     * @param updates Список обновлений, которые необходимо обработать.
     * @return Код подтверждения для всех обновлений.
     */
    @Override
    public int process(List<Update> updates) {
        keyboardService.processUpdates(updates);
        return CONFIRMED_UPDATES_ALL;
    }
}



