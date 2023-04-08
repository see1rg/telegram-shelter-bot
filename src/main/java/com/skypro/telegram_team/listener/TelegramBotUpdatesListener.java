package com.skypro.telegram_team.listener;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.skypro.telegram_team.KeyboardServiceExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;
    private final KeyboardServiceExt keyboardService;

    @Autowired
    public TelegramBotUpdatesListener(TelegramBot telegramBot, KeyboardServiceExt keyboardService) {
        this.telegramBot = telegramBot;
        this.keyboardService = keyboardService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        //KeyboardService keyboardService = new KeyboardService();
        //keyboardService.getResponse(updates, telegramBot);
        keyboardService.processUpdates(updates);
        return CONFIRMED_UPDATES_ALL;
    }
}



