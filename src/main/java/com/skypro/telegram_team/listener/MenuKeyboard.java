package com.skypro.telegram_team.listener;

import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

import java.util.Arrays;

public enum MenuKeyboard {
    START_KEYBOARD(true,
            Menu.SET_SHELTER),
    MAIN_KEYBOARD(false,
            Menu.GET_INFO,
            Menu.GET_ANIMAL,
            Menu.SEND_REPORT,
            Menu.SET_USER_DATA,
            Menu.ASK_VOLUNTEER);

    private final Keyboard keyboard;

    MenuKeyboard(boolean oneTime, Menu... menus) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(new KeyboardButton(menus[0].getText()));
        Arrays.stream(menus)
                .skip(1)
                .forEach(m -> markup.addRow(m.getText()));
        markup.resizeKeyboard(true).oneTimeKeyboard(oneTime);
        this.keyboard = markup;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }
}
