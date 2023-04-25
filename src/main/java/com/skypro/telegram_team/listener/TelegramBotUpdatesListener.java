package com.skypro.telegram_team.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.skypro.telegram_team.keyboards.KeyboardServiceExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBot telegramBot;
    private final ApplicationContext context;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, ApplicationContext context) {
        this.telegramBot = telegramBot;
        this.context = context;
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
     *
     * @param updates Список обновлений, которые необходимо обработать.
     * @return Код подтверждения для всех обновлений.
     */
    @Override
    public int process(List<Update> updates) {
        processUpdates(updates);
        return CONFIRMED_UPDATES_ALL;
    }

    /**
     * Обрабатывает список обновлений
     *
     * @param updates Список обновлений, которые необходимо обработать.
     */
    private void processUpdates(List<Update> updates) {
        updates.forEach(update -> {
            try {
                logger.info("Process update: {}", update);
                processUpdate(update);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
    }

    /**
     * Обрабатывает одно обновление из списка
     *
     * @param update Обновление, которое необходимо обработать
     */
    private void processUpdate(Update update) {
        KeyboardServiceExt keyboardService = context.getBean(KeyboardServiceExt.class);
        keyboardService.processUpdate(update);
    }
}



