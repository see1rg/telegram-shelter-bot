package com.skypro.telegram_team;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.skypro.telegram_team.buffers.Question;
import com.skypro.telegram_team.buffers.QuestionsBuffer;
import com.skypro.telegram_team.buffers.Request;
import com.skypro.telegram_team.buffers.RequestsBuffer;
import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.services.ReportService;
import com.skypro.telegram_team.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
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
        GET_INFO("О приюте"),
        GET_ANIMAL("Как взять собаку"),
        SEND_REPORT("Отправить отчет"),
        SET_USER_DATA("Записать контактные данные"),
        ASK_VOLUNTEER("Спросить волонтера");
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
        INF_SCHEDULE("Расписание"),
        INF_ADDRESS("Адрес"),
        INF_SCHEME("Схема проезда"),
        INF_SAFETY("Техника безопасности"),
        HOW_RULES("Правила знакомства с собакой"),
        HOW_DOCS("Список документов"),
        HOW_MOVE("Рекомендации по транспортировке"),
        HOW_ARRANGE("Рекомендации по обустройству"),
        HOW_ARRANGE_PUPPY("Рекомендации по обустройству для щенка"),
        HOW_ARRANGE_CRIPPLE("Рекомендации по обустройству для собаки-инвалида"),
        HOW_EXPERT_FIRST("Советы кинолога по первому общению"),
        HOW_EXPERT_NEXT("Советы кинолога по дальнейшему общению"),
        HOW_REJECT_REASONS("Причины отказа"),
        ASK_VOLUNTEER(""),
        ASK_ANY_VOLUNTEER("Любого"),
        SAVE_USER_PHONE("Указать телефон"),
        SAVE_USER_EMAIL("Указать почту"),
        SEND_PHOTO("Фото"),
        SEND_DIET("Питание"),
        SEND_BEHAVIOR("Поведение"),
        SEND_WELL_BEING("Самочувствие");

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
    private final ReportService reportService;
    private final QuestionsBuffer questionsBuffer;
    private final RequestsBuffer requestsBuffer;

    public KeyboardServiceExt(TelegramBot telegramBot,
                              UserService userService, ReportService reportService,
                              QuestionsBuffer questionsBuffer, RequestsBuffer requestsBuffer) {
        this.telegramBot = telegramBot;
        this.userService = userService;
        this.reportService = reportService;
        this.questionsBuffer = questionsBuffer;
        this.requestsBuffer = requestsBuffer;
    }

    /**
     * Обработка всех Updates
     *
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
     *
     * @param update
     */
    private void processUpdate(Update update) {
        Optional<SendMessage> sendMessage;
        if (update.callbackQuery() != null) {
            //Update c callback
            sendMessage = processCallback(update.callbackQuery());
        } else {
            //Update c message
            sendMessage = processMessage(update.message());
        }
        sendMessage.ifPresent(telegramBot::execute);
    }

    private Optional<SendMessage> processMessage(Message message) {
        Optional<SendMessage> sendMessage;
        sendMessage = processTextMessage(message);
        if (sendMessage.isPresent())
            return sendMessage;
        sendMessage = processDataMessage(message);
        if (sendMessage.isPresent())
            return sendMessage;
        return processReplyMessage(message);
    }

    /**
     * Обработка команд меню
     *
     * @param message
     */
    private Optional<SendMessage> processTextMessage(Message message) {
        //Меню
        SendMessage sendMessage = null;
        if (message.text() != null) {
            if (Menu.START.getText().equals(message.text())) {
                //Старт
                Keyboard keyboard = new ReplyKeyboardMarkup
                        (new KeyboardButton(Menu.GET_INFO.getText()))
                        .addRow(Menu.GET_ANIMAL.getText())
                        .addRow(Menu.SEND_REPORT.getText())
                        .addRow(Menu.SET_USER_DATA.getText())
                        .addRow(Menu.ASK_VOLUNTEER.getText())
                        .resizeKeyboard(true)
                        .oneTimeKeyboard(false);
                sendMessage = new SendMessage(message.chat().id(), "Привет!");
                sendMessage.replyMarkup(keyboard);
            } else if (Menu.GET_INFO.getText().equals(message.text())) {
                //Инфо о приюте
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup
                        (new InlineKeyboardButton(Commands.INF_SCHEDULE.getText()).callbackData(Commands.INF_SCHEDULE.name()),
                                (new InlineKeyboardButton(Commands.INF_ADDRESS.getText()).callbackData(Commands.INF_ADDRESS.name())),
                                (new InlineKeyboardButton(Commands.INF_SCHEME.getText()).callbackData(Commands.INF_SCHEME.name())))
                        .addRow(new InlineKeyboardButton(Commands.INF_SAFETY.getText()).callbackData(Commands.INF_SAFETY.name()));
                sendMessage = new SendMessage(message.chat().id(), "Информация о приюте");
                sendMessage.replyMarkup(markup);
            } else if (Menu.GET_ANIMAL.getText().equals(message.text())) {
                //Как взять собаку
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup
                        (new InlineKeyboardButton(Commands.HOW_RULES.getText()).callbackData(Commands.HOW_RULES.name()))
                        .addRow(new InlineKeyboardButton(Commands.HOW_DOCS.getText()).callbackData(Commands.HOW_DOCS.name()))
                        .addRow(new InlineKeyboardButton(Commands.HOW_MOVE.getText()).callbackData(Commands.HOW_MOVE.name()))
                        .addRow(new InlineKeyboardButton(Commands.HOW_ARRANGE.getText()).callbackData(Commands.HOW_ARRANGE.name()))
                        .addRow(new InlineKeyboardButton(Commands.HOW_ARRANGE_PUPPY.getText()).callbackData(Commands.HOW_ARRANGE_PUPPY.name()))
                        .addRow(new InlineKeyboardButton(Commands.HOW_ARRANGE_CRIPPLE.getText()).callbackData(Commands.HOW_ARRANGE_CRIPPLE.name()))
                        .addRow(new InlineKeyboardButton(Commands.HOW_EXPERT_FIRST.getText()).callbackData(Commands.HOW_EXPERT_FIRST.name()))
                        .addRow(new InlineKeyboardButton(Commands.HOW_EXPERT_NEXT.getText()).callbackData(Commands.HOW_EXPERT_NEXT.name()))
                        .addRow(new InlineKeyboardButton(Commands.HOW_REJECT_REASONS.getText()).callbackData(Commands.HOW_REJECT_REASONS.name()));
                sendMessage = new SendMessage(message.chat().id(), "Как взять собаку");
                sendMessage.replyMarkup(markup);
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
                                (new InlineKeyboardButton(Commands.SEND_DIET.getText()).callbackData(Commands.SEND_DIET.name())))
                        .addRow((new InlineKeyboardButton(Commands.SEND_BEHAVIOR.getText()).callbackData(Commands.SEND_BEHAVIOR.name())),
                                (new InlineKeyboardButton(Commands.SEND_WELL_BEING.getText()).callbackData(Commands.SEND_WELL_BEING.name())));
                sendMessage = new SendMessage(message.chat().id(), "Какие данные отправить?");
                sendMessage.replyMarkup(markup);
            }
        }
        return Optional.ofNullable(sendMessage);
    }

    /**
     * Обработка сообщений с callback
     * (при нажатии на меню inline keyboard)
     *
     * @param callbackQuery
     */
    private Optional<SendMessage> processCallback(CallbackQuery callbackQuery) {
        //callback команды
        Long userChatId = callbackQuery.message().chat().id();
        SendMessage sendMessage = null;
        if (callbackQuery.data().equals(Commands.INF_ADDRESS.name())) {
            //Адрес
            sendMessage = new SendMessage(userChatId, "Адрес такой то...");
        } else if (callbackQuery.data().equals(Commands.INF_SCHEDULE.name())) {
            //Расписание
            sendMessage = new SendMessage(userChatId, "Расписание такое...");
        } else if (callbackQuery.data().equals(Commands.INF_SCHEME.name())) {
            //Схема проезда
            sendMessage = new SendMessage(userChatId, "Проехать так то...");
        } else if (callbackQuery.data().equals(Commands.INF_SAFETY.name())) {
            //Техника безопасности
            sendMessage = new SendMessage(userChatId, "Будьте осторожны...");
        } else if(callbackQuery.data().equals(Commands.HOW_RULES.name())) {
            //Правила знакомства с собакой
            sendMessage = new SendMessage(userChatId, "Правила...");
        } else if(callbackQuery.data().equals(Commands.HOW_DOCS.name())) {
            //Список документов
            sendMessage = new SendMessage(userChatId, "Документы...");
        } else if(callbackQuery.data().equals(Commands.HOW_MOVE.name())) {
            //Рекомендации по транспортировке
            sendMessage = new SendMessage(userChatId, "Рекомендации...");
        } else if(callbackQuery.data().equals(Commands.HOW_ARRANGE.name())) {
            //Рекомендации по обустройству
            sendMessage = new SendMessage(userChatId, "Рекомендации...");
        } else if(callbackQuery.data().equals(Commands.HOW_ARRANGE_PUPPY.name())) {
            //Рекомендации по обустройству для щенка
            sendMessage = new SendMessage(userChatId, "Рекомендации...");
        } else if(callbackQuery.data().equals(Commands.HOW_ARRANGE_CRIPPLE.name())) {
            //Рекомендации по обустройству для собаки-инвалида
            sendMessage = new SendMessage(userChatId, "Рекомендации...");
        } else if(callbackQuery.data().equals(Commands.HOW_EXPERT_FIRST.name())) {
            //Советы кинолога по первому общению
            sendMessage = new SendMessage(userChatId, "Советы кинолога...");
        } else if(callbackQuery.data().equals(Commands.HOW_EXPERT_NEXT.name())) {
            //Советы кинолога по дальнейшему общению
            sendMessage = new SendMessage(userChatId, "Советы кинолога...");
        } else if(callbackQuery.data().equals(Commands.HOW_REJECT_REASONS.name())) {
            //Причины отказа
            sendMessage = new SendMessage(userChatId, "Первая причина это ты...");
        } else if (callbackQuery.data().startsWith(Commands.ASK_VOLUNTEER.name())) {
            //Конкретный волонтера (чат выбранного волонтера в callback data)
            Long volunteerChatId = Long.parseLong(callbackQuery.data().substring(Commands.ASK_VOLUNTEER.name().length()));
            questionsBuffer.addQuestion(new Question(userChatId, volunteerChatId));
            sendMessage = new SendMessage(userChatId, "Напишите вопрос");
        } else if (callbackQuery.data().startsWith(Commands.ASK_ANY_VOLUNTEER.name())) {
            //Любой волонтер (будет найден первый попавшийся)
            if (findAnyVolunteer().isPresent()) {
                var volunteer = findAnyVolunteer().get();
                Long volunteerChatId = volunteer.getTelegramId();
                questionsBuffer.addQuestion(new Question(userChatId, volunteerChatId));
                sendMessage = new SendMessage(userChatId, "Напишите вопрос");
            } else {
                sendMessage = new SendMessage(userChatId, "Нет свободных волонтеров");
            }
        } else if (callbackQuery.data().equals(Commands.SAVE_USER_PHONE.name())) {
            //Телефон
            Request request = new Request(userChatId);
            request.setUserPhoneRequested(true);
            requestsBuffer.addRequest(request);
            sendMessage = new SendMessage(userChatId, "Напишите телефон");
        } else if (callbackQuery.data().equals(Commands.SAVE_USER_EMAIL.name())) {
            //Почта
            Request request = new Request(userChatId);
            request.setUserEmailRequested(true);
            requestsBuffer.addRequest(request);
            sendMessage = new SendMessage(userChatId, "Напишите почту");
        } else if (callbackQuery.data().equals(Commands.SEND_PHOTO.name())) {
            //Фото для отчета
            Request request = new Request(userChatId);
            request.setReportPhotoRequested(true);
            requestsBuffer.addRequest(request);
            sendMessage = new SendMessage(userChatId, "Отправьте фото");
        } else if (callbackQuery.data().equals(Commands.SEND_DIET.name())) {
            //Диета для отчета
            Request request = new Request(userChatId);
            request.setReportDietRequested(true);
            requestsBuffer.addRequest(request);
            sendMessage = new SendMessage(userChatId, "Опишите диету");
        } else if (callbackQuery.data().equals(Commands.SEND_BEHAVIOR.name())) {
            //Поведение для отчета
            Request request = new Request(userChatId);
            request.setReportBehaviorRequested(true);
            requestsBuffer.addRequest(request);
            sendMessage = new SendMessage(userChatId, "Опишите поведение");
        } else if (callbackQuery.data().equals(Commands.SEND_WELL_BEING.name())) {
            //Самочувствие для отчета
            Request request = new Request(userChatId);
            request.setReportWellBeingRequest(true);
            requestsBuffer.addRequest(request);
            sendMessage = new SendMessage(userChatId, "Опишите самочувствие");
        }
        return Optional.ofNullable(sendMessage);
    }

    /**
     * Обработка сообщений с данными от пользователя
     *
     * @param message
     */
    private Optional<SendMessage> processDataMessage(Message message) {
        //Данные от пользователя
        SendMessage sendMessage = null;
        Long userChatId = message.chat().id();
        if (questionsBuffer.getQuestionByUserChat(userChatId).isPresent()) {
            //Записать сообщение волонтеру
            Question question = questionsBuffer.getQuestionByUserChat(userChatId).get();
            if (question.getQuestion() == null) {
                question.setId(message.messageId());
                question.setQuestion(String.format("%d: Сообщение от пользователя, для ответа используйте reply:\n %s", message.messageId(), message.text()));
                telegramBot.execute(new SendMessage(question.getVolunteerChatId(), question.getQuestion()));
                sendMessage = new SendMessage(question.getUserChatId(), "Сообщение отправлено волонтеру");
            } else {
                sendMessage = new SendMessage(question.getUserChatId(), "Волонтер еще не ответил");
            }
        } else if (requestsBuffer.getRequest(userChatId).isPresent()) {
            //Есть запрос на данные
            Request request = requestsBuffer.getRequest(userChatId).get();
            if (request.isUserPhoneRequested() || request.isUserEmailRequested()) {
                //Записать данные пользователя (если пользователя нет, то добавляем)
                User user = addUserIfNotExist(userChatId, message.chat().username());
                if (request.isUserPhoneRequested())
                    user.setPhone(message.text());
                if (request.isUserEmailRequested())
                    user.setEmail(message.text());
                userService.update(user, user.getId());
                sendMessage = new SendMessage(userChatId, "Данные пользователя записаны");
            }
            if (request.isReportPhotoRequested() || request.isReportDietRequested() ||
                    request.isReportBehaviorRequested() || request.isReportWellBeingRequest()) {
                //Записать данные отчета (если отчет за текущий день не найден, то добавляем)
                Report report = addReportIfNotExist(addUserIfNotExist(userChatId, message.chat().username()));
                if (request.isReportDietRequested())
                    report.setDiet(message.text());
                if (request.isReportBehaviorRequested())
                    report.setChangeBehavior(message.text());
                if (request.isReportPhotoRequested())
                    report.setPhoto(new byte['z']); //подумать как преобразовать message.photo()
                if (request.isReportWellBeingRequest())
                    report.setWellBeing(message.text());
                reportService.update(report, report.getId());
                sendMessage = new SendMessage(userChatId, "Данные отчета записаны");
            }
            requestsBuffer.delRequest(request);
        }
        return Optional.ofNullable(sendMessage);
    }

    /**
     * Обработка reply-сообщений
     * (используется при ответе волонтера на сообщение)
     *
     * @param message
     */
    private Optional<SendMessage> processReplyMessage(Message message) {
        SendMessage sendMessage = null;
        if (message.replyToMessage() != null) {
            //Номер сообщения из пользовательского сообщения
            var messageId = getMessageId(message.replyToMessage().text());
            if (messageId != 0 && questionsBuffer.getQuestionById(messageId).isPresent()) {
                Question question = questionsBuffer.getQuestionById(messageId).get();
                question.setAnswer("Ответ волонтера: \n" + message.text());
                sendMessage = new SendMessage(question.getUserChatId(), question.getAnswer());
                questionsBuffer.delQuestion(question);
            }
        }
        return Optional.ofNullable(sendMessage);
    }

    /**
     * Поиск номера сообщения для ответа пользователю волонтером
     *
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

    //метод для тестов, нужно заменить методом из ReportService
    private Report findReportByUserAndDate(User user, Date date) {
        return reportService.findAll().stream()
                //поиск по дате пока не работает
                //.filter(r -> r.getUserId() == user.getId() && r.getDate().equals(date))
                .filter(r -> r.getUserId() == user.getId())
                .findFirst().orElse(new Report());
    }

    //метод для тестов, подумать куда его переместить
    private User addUserIfNotExist(Long telegramId, String username) {
        User user = findByTelegramId(telegramId);
        if (user.getId() == 0L) {
            user.setTelegramId(telegramId);
            user.setName(username);
            userService.save(user);
        }
        return user;
    }

    //метод для тестов, подумать куда его переместить
    private Report addReportIfNotExist(User user) {
        //Ищем отчет на текущую дату, считаем что один отчет в день от пользователя
        Report report = findReportByUserAndDate(user, new Date());
        if (report.getId() == 0L) {
            //Сейчас все поля обязательны, заполняем значениями по-умолчанию
            //Далее нужно убрать обязательность с некоторых полей!!!
            report.setUserId(user.getId());
            report.setAnimalId(1);//(user.getAnimal().getId);
            report.setStatus("new");
            report.setDescription("default");
            report.setDate(new Date());
            report.setPhoto(new byte['x']);
            report.setDiet("default");
            report.setWellBeing("default");
            report.setChangeBehavior("default");
            reportService.save(report);
        }
        return report;
    }
}
