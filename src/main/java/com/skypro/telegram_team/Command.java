package com.skypro.telegram_team;

/**
 * Класс, перечисляющий команды, при вызове которых в телеграмботе будет ответ
 */
public enum Command {
    START("/start"),
    HOW_TO_GET_INFO_ABOUT_SHELTER("Узнать информацию о приюте"),
    HOW_TO_GET_DOG_FROM_SHELTER("Как взять собаку из приюта?"),
    SEND_DOG_REPORT("Прислать отчет о питомце"),
    CALL_VOLUNTEER("Позвать волонтера", "callback_2"),
    WRITE_DOWN_CONTACT_INFORMATION("Записать контактные данные для связи", "callback_1"),
    LEARN_RULES_OF_DATING_DOG("1", "callback_3"),
    GET_LIST_OF_DOCUMENTS_TO_GET_DOG("2", "callback_4"),
    GET_LIST_OF_RECOMMENDATIONS_FOR_TRANSPORTING_DOG("3", "callback_5"),
    GET_LIST_OF_HOME_IMPROVEMENT_TIPS_FOR_PUPPY("4", "callback_6"),
    GET_LIST_OF_HOME_IMPROVEMENT_TIPS_FOR_ADULT_DOG("5", "callback_7"),
    GET_LIST_OF_HOME_IMPROVEMENT_FOR_DOG_WITH_DISABILITY("6", "callback_8"),
    GET_ADVICE_FROM_CYNOLOGIST_ON_INITIAL_COMMUNICATION_WITH_DOG("7", "callback_9"),
    GET_RECOMMENDATIONS_ON_PROVEN_CYNOLOGISTS_FOR_FURTHER_REFERRAL("8", "callback_10"),
    GET_LIST_REASONS_FOR_REFUSING_TO_PICK_UP_DOG("9", "callback_11"),
    GET_DAILY_REPORT_FORM("Форма ежедневного отчета", "callback_12");


    private final String commandName;
    private String callBack;

    Command(String commandName, String callBack) {
        this.commandName = commandName;
        this.callBack = callBack;
    }

    Command(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getCallBack() {
        return callBack;
    }
}
