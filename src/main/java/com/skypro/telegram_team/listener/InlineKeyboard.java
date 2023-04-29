package com.skypro.telegram_team.listener;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.Arrays;

public enum InlineKeyboard {
    SHELTER_INFO(
            Callback.INF_ADDRESS,
            Callback.INF_SCHEDULE,
            Callback.INF_SCHEME,
            Callback.INF_SAFETY
    ),
    ANIMAL_INFO(
            Callback.HOW_RULES,
            Callback.HOW_DOCS,
            Callback.HOW_MOVE,
            Callback.HOW_ARRANGE,
            Callback.HOW_ARRANGE_PUPPY,
            Callback.HOW_ARRANGE_CRIPPLE,
            Callback.HOW_EXPERT_FIRST,
            Callback.HOW_EXPERT_NEXT,
            Callback.HOW_REJECT_REASONS
    ),
    USER_DATA(
            Callback.SAVE_USER_PHONE,
            Callback.SAVE_USER_EMAIL
    ),
    REPORT_DATA(
            Callback.SEND_BEHAVIOR,
            Callback.SEND_DIET,
            Callback.SEND_WELL_BEING,
            Callback.SEND_PHOTO
    );

    private InlineKeyboardMarkup markup;

    InlineKeyboard(Callback... callbacks) {
        markup = new InlineKeyboardMarkup
                (new InlineKeyboardButton(callbacks[0].getText()).callbackData(callbacks[0].name()));
        Arrays.stream(callbacks)
                .skip(1)
                .forEach(m -> markup.addRow(new InlineKeyboardButton(m.getText()).callbackData(m.name())));
    }

    public InlineKeyboardMarkup getMarkup() {
        return markup;
    }
}
