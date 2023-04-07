package com.skypro.telegram_team;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.skypro.telegram_team.buffers.Question;
import com.skypro.telegram_team.buffers.QuestionsBuffer;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Обработка команд клавиатуры
 */
@Component
public class KeyboardServiceExt {
    /**
     * Шаблон сообщения от пользователя к волонтеру
     * id сообщения должно быть вначале, используется для отправки ответа
     */
    private final static String USER_VOLUNTEER_MSG_TEMPL = "(^|\\s)([0-9]+)";

    /**
     * Пункты меню
     */
    private enum Menu {
        START("/start"),
        GET_INFO("Узнать информацию"),
        ASK_VOLUNTEER("Спросить волонтера"),
        SEND_REPORT("Отправить отчет"),
        SET_USER_DATA("Записать контактные данные");
        private final String text;

        Menu(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * Команды для inline keyboard
     */
    private enum Commands {
        ASK_VOLUNTEER(""),
        ASK_ANY_VOLUNTEER("Любого"),
        SAVE_USER_PHONE("Указать телефон"),
        SAVE_USER_EMAIL("Указать почту"),
        SEND_PHOTO("Фото"),
        SEND_DIET("Питание"),
        SEND_BEHAVIOR("Поведение");

        private final String text;

        Commands(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private final Logger logger = LoggerFactory.getLogger(KeyboardServiceExt.class);

    private final TelegramBot telegramBot;
    private final UserService userService;
    private final QuestionsBuffer questionsBuffer;

    public KeyboardServiceExt(TelegramBot telegramBot, UserService userService, QuestionsBuffer questionsBuffer) {
        this.telegramBot = telegramBot;
        this.userService = userService;
        this.questionsBuffer = questionsBuffer;
    }

    /**
     * Обработка всех Updates
     * @param updates
     */
    public void processUpdates(List<Update> updates) {
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
     * Обработка отдельного Update
     * @param update
     */
    private void processUpdate(Update update) {
        if (update.callbackQuery() == null) {
            if (update.message().replyToMessage() == null) {
                processMessage(update.message());
            } else {
                processReplyMessage(update.message());
            }
        } else {
            processCallback(update.callbackQuery());
        }
    }

    /**
     * Обработка команд меню и сообщений пользователя
     * @param message
     */
    private void processMessage(Message message) {
        Long userChatId = message.chat().id();
        SendMessage sendMessage = null;

        //Меню
        if (Menu.START.getText().equals(message.text())) {
            //Старт
            Keyboard keyboard = new ReplyKeyboardMarkup
                    (new KeyboardButton(Menu.GET_INFO.getText()))
                    .addRow(Menu.SEND_REPORT.getText())
                    .addRow(Menu.SET_USER_DATA.getText())
                    .addRow(Menu.ASK_VOLUNTEER.getText())
                    .resizeKeyboard(true)
                    .oneTimeKeyboard(false);
            sendMessage = new SendMessage(message.chat().id(), "Привет!");
            sendMessage.replyMarkup(keyboard);
        } else if (Menu.GET_INFO.getText().equals(message.text())) {
            //Инфо о приюте
            sendMessage = new SendMessage(message.chat().id(), "Самый лучший приют!");
        } else if (Menu.ASK_VOLUNTEER.getText().equals(message.text())) {
            //Вопрос волонтеру
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            findVolunteers().entrySet().stream()
                    .forEach((entry) -> {
                        markup.addRow(new InlineKeyboardButton(entry.getValue())
                                .callbackData(Commands.ASK_VOLUNTEER + entry.getKey()));
                    });
            markup.addRow(new InlineKeyboardButton(Commands.ASK_ANY_VOLUNTEER.getText())
                    .callbackData(Commands.ASK_ANY_VOLUNTEER.name()));
            sendMessage = new SendMessage(message.chat().id(), "Кого спросить?");
            sendMessage.replyMarkup(markup);
        } else if (Menu.SET_USER_DATA.getText().equals(message.text())) {
            //Записать данные пользователя
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup
                    (new InlineKeyboardButton(Commands.SAVE_USER_PHONE.getText()).callbackData(Commands.SAVE_USER_PHONE.name()),
                            (new InlineKeyboardButton(Commands.SAVE_USER_EMAIL.getText()).callbackData(Commands.SAVE_USER_EMAIL.name())));
            sendMessage = new SendMessage(message.chat().id(), "Какие данные записать?");
            sendMessage.replyMarkup(markup);
        } else if (Menu.SEND_REPORT.getText().equals(message.text())) {
            //Отправить отчет
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup
                    (new InlineKeyboardButton(Commands.SEND_PHOTO.getText()).callbackData(Commands.SEND_PHOTO.name()),
                            (new InlineKeyboardButton(Commands.SEND_DIET.getText()).callbackData(Commands.SEND_DIET.name())),
                            (new InlineKeyboardButton(Commands.SEND_BEHAVIOR.getText()).callbackData(Commands.SEND_BEHAVIOR.name())));
            sendMessage = new SendMessage(message.chat().id(), "Какие данные отправить?");
            sendMessage.replyMarkup(markup);
        } else {
            //Прочее
            sendMessage = new SendMessage(message.chat().id(), "Нечего ответить...");
        }

        //Сообщения от пользователя
        if (questionsBuffer.getQuestionByUserChat(userChatId).isPresent()) {
            //Написать сообщение волонтеру
            Question question = questionsBuffer.getQuestionByUserChat(userChatId).get();
            if (question.getQuestion() == null) {
                question.setId(message.messageId());
                question.setQuestion(String.format("%d: Сообщение от пользователя, для ответа используйте reply:\n %s", message.messageId(), message.text()));
                telegramBot.execute(new SendMessage(question.getVolunteerChatId(), question.getQuestion()));
                sendMessage = new SendMessage(question.getUserChatId(), "Сообщение отправлено волонтеру");
            } else {
                sendMessage = new SendMessage(question.getUserChatId(), "Волонтер еще не ответил");
            }
        }
        //...здесь можно ловить сообщения на обновление данных пользователя
        //...а так же сообщения для отчета

        telegramBot.execute(sendMessage);
    }

    /**
     * Обработка сообщений с callback
     * (при нажатии на меню inline keyboard)
     * @param callbackQuery
     */
    private void processCallback(CallbackQuery callbackQuery) {
        SendMessage sendMessage = null;
        if (callbackQuery.data().startsWith(Commands.ASK_VOLUNTEER.name())) {
            //Конкретный волонтера (чат выбранного волонтера в callback data)
            Long userChatId = callbackQuery.message().chat().id();
            Long volunteerChatId = Long.parseLong(callbackQuery.data().substring(Commands.ASK_VOLUNTEER.name().length()));
            questionsBuffer.addQuestion(new Question(userChatId, volunteerChatId));
            sendMessage = new SendMessage(callbackQuery.message().chat().id(), "Напишите вопрос");
        } else if (callbackQuery.data().startsWith(Commands.ASK_ANY_VOLUNTEER.name())) {
            //Любой волонтер (будет найден первый попавшийся)
            if (findAnyVolunteer().isPresent()) {
                var volunteer = findAnyVolunteer().get();
                Long userChatId = callbackQuery.message().chat().id();
                Long volunteerChatId = volunteer.getTelegramId();
                questionsBuffer.addQuestion(new Question(userChatId, volunteerChatId));
                sendMessage = new SendMessage(callbackQuery.message().chat().id(), "Напишите вопрос");
            } else {
                sendMessage = new SendMessage(callbackQuery.message().chat().id(), "Нет свободных волонтеров");
            }
        } else if (callbackQuery.data().equals(Commands.SAVE_USER_PHONE.name())) {
            //Телефон
            //... здесь можно добавить заполнение буфера по аналогии с волонтерами
            sendMessage = new SendMessage(callbackQuery.message().chat().id(), "Напишите телефон");
        } else if (callbackQuery.data().equals(Commands.SAVE_USER_EMAIL.name())) {
            //Почта
            //... здесь можно добавить заполнение буфера по аналогии с волонтерами
            sendMessage = new SendMessage(callbackQuery.message().chat().id(), "Напишите почту");
        } else if (callbackQuery.data().equals(Commands.SEND_PHOTO.name())) {
            //Фото для отчета
            //... здесь можно добавить заполнение буфера по аналогии с волонтерами
            sendMessage = new SendMessage(callbackQuery.message().chat().id(), "Отправьте фото");
        } else if (callbackQuery.data().equals(Commands.SEND_DIET.name())) {
            //Диета для отчета
            //... здесь можно добавить заполнение буфера по аналогии с волонтерами
            sendMessage = new SendMessage(callbackQuery.message().chat().id(), "Опишите диету");
        } else if (callbackQuery.data().equals(Commands.SEND_BEHAVIOR.name())) {
            //Поведение для отчета
            //... здесь можно добавить заполнение буфера по аналогии с волонтерами
            sendMessage = new SendMessage(callbackQuery.message().chat().id(), "Опишите поведение");
        } else {
            sendMessage = new SendMessage(callbackQuery.message().chat().id(), "Нечего ответить...");
        }
        telegramBot.execute(sendMessage);
    }

    /**
     * Обработка reply-сообщений
     * (используется при ответе волонтера на сообщение)
     * @param message
     */
    private void processReplyMessage(Message message) {
        if (message.text() == null) {
            return;
        }
        //Номер сообщения из пользовательского сообщения
        var messageId = getMessageId(message.replyToMessage().text());
        if (messageId != 0 && questionsBuffer.getQuestionById(messageId).isPresent()) {
            Question question = questionsBuffer.getQuestionById(messageId).get();
            question.setAnswer("Ответ волонтера: \n" + message.text());
            SendMessage replayMessage = new SendMessage(question.getUserChatId(), question.getAnswer());
            telegramBot.execute(replayMessage);
            questionsBuffer.delQuestion(question);
        }
    }

    /**
     * Поиск номера сообщения для ответа пользователю волонтером
     * @param message
     * @return
     */
    private int getMessageId(String message) {
        Pattern pattern = Pattern.compile(USER_VOLUNTEER_MSG_TEMPL);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(2));
        }
        return 0;
    }

    //метод для тестов, нужно заменить методом из UserService
    private Map<String, String> findVolunteers() {
        return userService.findAll().stream()
                .filter(User::isVolunteer)
                .collect(Collectors.toMap(
                        user -> Long.toString(user.getTelegramId()),
                        user -> user.getName()));
    }

    //метод для тестов, нужно заменить методом из UserService
    private Optional<User> findAnyVolunteer() {
        return userService.findAll().stream()
                .filter(User::isVolunteer)
                .findAny();
    }

    //метод для тестов, нужно заменить методом из UserService
    private User findByTelegramId(Long telegramId) {
        return userService.findAll().stream()
                .filter(u -> u.getTelegramId() == telegramId)
                .findFirst().orElse(new User());
    }

    //метод для тестов, можно удалить
    private void addUserIfNotExist(Long telegramId, String username) {
        User user = findByTelegramId(telegramId);
        if (user.getId() == 0L) {
            user.setTelegramId(telegramId);
            user.setName(username);
            userService.save(user);
        }
    }
}
