package com.skypro.telegram_team.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.skypro.telegram_team.KeyboardService;
import com.skypro.telegram_team.KeyboardServiceExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;

    @Autowired
    public TelegramBotUpdatesListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Autowired
    private KeyboardService keyboardService;

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
        keyboardService.getResponse(updates, telegramBot);
        return CONFIRMED_UPDATES_ALL;
    }
}



