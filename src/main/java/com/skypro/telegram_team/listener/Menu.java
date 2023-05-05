package com.skypro.telegram_team.listener;

import java.util.Arrays;
import java.util.Optional;

public enum Menu {
    START("/start"),
    GET_INFO("О приюте"),
    GET_ANIMAL("Как взять животное"),
    SEND_REPORT("Отправить отчет"),
    SET_USER_DATA("Записать контактные данные"),
    SET_SHELTER("Выбрать приют"),
    ASK_VOLUNTEER("Спросить волонтера");

    public static Optional<Menu> fromText(String text) {
        return Arrays.stream(Menu.values())
                .filter(m -> m.text.equals(text))
                .findFirst();
    }

    private final String text;

    Menu(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}