package com.skypro.telegram_team.listener;

public enum Callback {
    INF_SCHEDULE("Расписание"),
    INF_ADDRESS("Адрес"),
    INF_SCHEME("Схема проезда"),
    INF_SAFETY("Техника безопасности"),
    HOW_RULES("Правила знакомства с животным"),
    HOW_DOCS("Список документов"),
    HOW_MOVE("Рекомендации по транспортировке"),
    HOW_ARRANGE("Рекомендации по обустройству"),
    HOW_ARRANGE_PUPPY("Рекомендации по обустройству для щенка/котенка"),
    HOW_ARRANGE_CRIPPLE("Рекомендации по обустройству для животного-инвалида"),
    HOW_EXPERT_FIRST("Советы эксперта по первому общению"),
    HOW_EXPERT_NEXT("Советы эксперта по дальнейшему общению"),
    HOW_REJECT_REASONS("Причины отказа"),
    ASK_VOLUNTEER(""),
    ASK_ANY_VOLUNTEER("Любого"),
    SAVE_USER_PHONE("Указать телефон"),
    SAVE_USER_EMAIL("Указать почту"),
    SAVE_SHELTER(""),
    SEND_PHOTO("Фото"),
    SEND_DIET("Питание"),
    SEND_BEHAVIOR("Поведение"),
    SEND_WELL_BEING("Самочувствие");

    private final String text;

    Callback(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
