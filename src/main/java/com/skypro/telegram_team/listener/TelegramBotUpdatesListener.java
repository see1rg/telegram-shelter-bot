package com.skypro.telegram_team.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.skypro.telegram_team.KeyboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {

    @Value("${telegram.bot.token}")
    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    /**
     * метод получает приходящие апдэйты с помощью long polling
     * @param updates
     * @return
     */
    @Override
    public int process(List<Update> updates) {
        KeyboardService keyboardService = new KeyboardService();
        keyboardService.getResponse(updates, telegramBot);
        return CONFIRMED_UPDATES_ALL;
    }
}



