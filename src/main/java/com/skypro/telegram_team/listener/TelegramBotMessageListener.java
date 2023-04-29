package com.skypro.telegram_team.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.skypro.telegram_team.listener.buffers.Question;
import com.skypro.telegram_team.listener.buffers.QuestionsBuffer;
import com.skypro.telegram_team.listener.buffers.Request;
import com.skypro.telegram_team.listener.buffers.RequestsBuffer;
import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.models.Shelter;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.services.ReportService;
import com.skypro.telegram_team.services.ShelterService;
import com.skypro.telegram_team.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TelegramBotMessageListener {
    /**
     * Шаблон сообщения от пользователя к волонтеру
     * id сообщения должно быть вначале, используется для отправки ответа
     */
    private final static String USER_VOLUNTEER_MSG_TEMPL = "(^|\\s)([0-9]+)";
    private final Logger logger = LoggerFactory.getLogger(TelegramBotMessageListener.class);
    private final TelegramBot telegramBot;
    private final QuestionsBuffer questionsBuffer;
    private final RequestsBuffer requestsBuffer;
    private final ShelterService shelterService;
    private final UserService userService;
    private final ReportService reportService;

    public TelegramBotMessageListener(TelegramBot telegramBot,
                                      QuestionsBuffer questionsBuffer, RequestsBuffer requestsBuffer,
                                      ShelterService shelterService, UserService userService, ReportService reportService) {
        this.telegramBot = telegramBot;
        this.questionsBuffer = questionsBuffer;
        this.requestsBuffer = requestsBuffer;
        this.shelterService = shelterService;
        this.userService = userService;
        this.reportService = reportService;
    }

    /**
     * Обработка сообщений в последовательности:
     * 1. текстовые сообщения главного меню
     * 2. сообщения от пользователя
     * 3. ответные сообщения
     *
     * @param message сообщение от пользователя
     * @return сообщения для отправки пользователю
     */
    public List<SendMessage> processMessage(Message message) {
        //Сохраним пользователя при первом обращении
        addUserIfNotExist(message);

        var sendMessages = processMenuMessage(message);
        if (!sendMessages.isEmpty()) {
            return sendMessages;
        }
        sendMessages = processDataMessage(message);
        if (!sendMessages.isEmpty()) {
            return sendMessages;
        }
        return processReplyMessage(message);
    }

    /**
     * Обработка текстовых команд меню
     *
     * @param message сообщение от пользователя
     * @return сообщения для отправки пользователю
     */
    private List<SendMessage> processMenuMessage(Message message) {
        //Меню
        if (message.text() == null || Menu.fromText(message.text()).isEmpty()) {
            return Collections.emptyList();
        }
        Menu menu = Menu.fromText(message.text()).get();
        switch (menu) {
            case START -> {
                //Старт
                return Collections.singletonList(
                        new SendMessage(message.chat().id(), "Привет! Для продолжения работы выберите приют.")
                                .replyMarkup(MenuKeyboard.START_KEYBOARD.getKeyboard()));
            }
            case SET_SHELTER -> {
                //Выбрать приют
                return Collections.singletonList(
                        new SendMessage(message.chat().id(), "Выберите приют")
                                .replyMarkup(getSheltersMarkup()));
            }
            case GET_INFO -> {
                //Инфо о приюте
                return Collections.singletonList(
                        new SendMessage(message.chat().id(), "Информация о приюте")
                                .replyMarkup(InlineKeyboard.SHELTER_INFO.getMarkup()));
            }
            case GET_ANIMAL -> {
                //Как взять животное
                return Collections.singletonList(
                        new SendMessage(message.chat().id(), "Как взять животное")
                                .replyMarkup(InlineKeyboard.ANIMAL_INFO.getMarkup()));
            }
            case ASK_VOLUNTEER -> {
                //Вопрос волонтеру
                return Collections.singletonList(
                        new SendMessage(message.chat().id(), "Кого спросить?")
                                .replyMarkup(getVolunteersMarkup()));
            }
            case SET_USER_DATA -> {
                //Записать данные пользователя
                return Collections.singletonList(
                        new SendMessage(message.chat().id(), "Какие данные записать?")
                                .replyMarkup(InlineKeyboard.USER_DATA.getMarkup()));
            }
            case SEND_REPORT -> {
                //Отправить отчет
                return Collections.singletonList(
                        new SendMessage(message.chat().id(), "Какие данные отправить?")
                                .replyMarkup(InlineKeyboard.REPORT_DATA.getMarkup()));
            }
        }
        return Collections.emptyList();
    }

    /**
     * Обработка сообщений с данными от пользователя
     *
     * @param message сообщение от пользователя
     * @return сообщения для отправки пользователю
     */
    private List<SendMessage> processDataMessage(Message message) {
        //Данные от пользователя
        Long userChatId = message.chat().id();
        if (questionsBuffer.getQuestionByUserChat(userChatId).isPresent()) {
            //Записать сообщение волонтеру
            Question question = questionsBuffer.getQuestionByUserChat(userChatId).get();
            return sendQuestionToVolunteer(question, message);
        } else if (requestsBuffer.getRequest(userChatId).isPresent()) {
            //Есть запрос на данные
            Request request = requestsBuffer.getRequest(userChatId).get();
            if (request.isUserPhoneRequested() || request.isUserEmailRequested()) {
                //Записать данные пользователя
                requestsBuffer.delRequest(request);
                return Collections.singletonList(updateRequestedUserData(request, message));
            }
            if (request.isReportPhotoRequested() || request.isReportDietRequested() ||
                    request.isReportBehaviorRequested() || request.isReportWellBeingRequest()) {
                //Записать данные дневного отчета
                requestsBuffer.delRequest(request);
                return Collections.singletonList(updateRequestedReportData(request, message));
            }
        }
        return Collections.emptyList();
    }

    /**
     * Обработка reply-сообщений
     * (используется при ответе волонтера на сообщение)
     *
     * @param message сообщение с reply
     * @return сообщения для отправки пользователю
     */
    private List<SendMessage> processReplyMessage(Message message) {
        if (message.replyToMessage() != null) {
            //Номер сообщения из пользовательского сообщения
            var messageId = getMessageId(message.replyToMessage().text());
            if (messageId != 0 && questionsBuffer.getQuestionById(messageId).isPresent()) {
                Question question = questionsBuffer.getQuestionById(messageId).get();
                question.setAnswer("Ответ волонтера: \n" + message.text());
                var sendMessage = new SendMessage(question.getUserChatId(), question.getAnswer());
                questionsBuffer.delQuestion(question);
                return Collections.singletonList(sendMessage);
            }
        }
        return Collections.emptyList();
    }

    /**
     * Inline keyboard меню со списком приютов
     *
     * @return меню приютов
     */
    private InlineKeyboardMarkup getSheltersMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        findShelters().forEach((key, value) -> markup.addRow(new InlineKeyboardButton(value)
                .callbackData(Callback.SAVE_SHELTER + key)));
        return markup;
    }

    /**
     * Поиск приютов
     *
     * @return Map, где
     * ключ - Id приюта,
     * значение - название приюта
     */
    private Map<String, String> findShelters() {
        return shelterService.findAll().stream()
                .collect(Collectors.toMap(
                        shelter -> Long.toString(shelter.getId()),
                        Shelter::getName));
    }

    /**
     * Inline keyboard меню со списком волонтеров
     *
     * @return меню приютов
     */
    private InlineKeyboardMarkup getVolunteersMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        findVolunteers().forEach((key, value) -> markup.addRow(new InlineKeyboardButton(value)
                .callbackData(Callback.ASK_VOLUNTEER + key)));
        markup.addRow(new InlineKeyboardButton(Callback.ASK_ANY_VOLUNTEER.getText())
                .callbackData(Callback.ASK_ANY_VOLUNTEER.name()));
        return markup;
    }

    /**
     * Поиск волонтеров
     *
     * @return Map, где
     * ключ - chatId волонтера,
     * значение - имя волонтера
     */
    private Map<String, String> findVolunteers() {
        return userService.findVolunteers().stream()
                .collect(Collectors.toMap(
                        user -> Long.toString(user.getTelegramId()),
                        User::getName));
    }

    /**
     * Отправить сообщение волонтеру
     * В сообщении вначале указываем id сообщения пользователя
     *
     * @param question вопрос волонтеру
     * @param message  сообщение от пользователя с вопросом
     * @return сообщения для отправки пользователю
     */
    private List<SendMessage> sendQuestionToVolunteer(Question question, Message message) {
        if (question.getQuestion() == null) {
            question.setId(message.messageId());
            question.setQuestion(String.format("%d: Сообщение от пользователя, для ответа используйте reply:\n %s",
                    message.messageId(), message.text()));
            return List.of(
                    new SendMessage(question.getVolunteerChatId(), question.getQuestion()),
                    new SendMessage(question.getUserChatId(), "Сообщение отправлено волонтеру")
            );
        } else {
            return List.of(new SendMessage(question.getUserChatId(), "Волонтер еще не ответил"));
        }
    }

    /**
     * Обновить данные пользователя
     * Если пользователя нет, то создать
     *
     * @param request запрос данных пользователя
     * @param message сообщение с данными пользователя
     * @return ответное сообщение пользователю
     */
    private SendMessage updateRequestedUserData(Request request, Message message) {
        User user = userService.findByTelegramId(message.chat().id());
        if (request.isUserPhoneRequested()) {
            user.setPhone(message.text());
        }
        if (request.isUserEmailRequested()) {
            user.setEmail(message.text());
        }
        return updateUser(user);
    }

    /**
     * Обновить данные пользователя
     *
     * @param user данные пользователя для обновления
     * @return ответное сообщение пользователю
     */
    private SendMessage updateUser(User user) {
        try {
            userService.update(user, user.getId());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new SendMessage(user.getTelegramId(), String.format("Возникла ошибка: %s", e.getMessage()));
        }
        return new SendMessage(user.getTelegramId(), "Данные пользователя записаны");
    }

    /**
     * Создать пользователя если еще нет в БД
     * поиск по telegramId
     *
     * @param message сообщение от пользователя
     */
    private void addUserIfNotExist(Message message) {
        Long telegramId = message.chat().id();
        User user = userService.findByTelegramId(telegramId);
        if (user.getId() == 0L) {
            user.setTelegramId(telegramId);
            if (message.chat().firstName() != null) {
                user.setName(message.chat().firstName());
            } else {
                user.setName(message.chat().username());
            }
            if (message.chat().lastName() != null) {
                user.setSurname(message.chat().lastName());
            } else {
                user.setSurname(message.chat().username());
            }
            user.setState(User.OwnerStateEnum.SEARCH);
            userService.create(user);
        }
    }

    /**
     * Обновить дневной отчет
     * Если отчет за текущий день не найден, то создать
     *
     * @param request запрос данных пользователя
     * @param message сообщение с данными пользователя
     * @return ответное сообщение пользователю
     */
    private SendMessage updateRequestedReportData(Request request, Message message) {
        Long userChatId = message.chat().id();
        try {
            Report report = addReportIfNotExist(userService.findByTelegramId(message.chat().id()));
            if (request.isReportDietRequested()) {
                report.setDiet(message.text());
            }
            if (request.isReportBehaviorRequested()) {
                report.setChangeBehavior(message.text());
            }
            if (request.isReportPhotoRequested()) {
                if (message.photo() != null) {
                    report.setPhoto(getPhotoContent(message.photo()));
                } else {
                    return new SendMessage(userChatId, "Пришлите фото");
                }
            }
            if (request.isReportWellBeingRequest()) {
                report.setWellBeing(message.text());
            }
            reportService.update(report, report.getId());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new SendMessage(userChatId, String.format("Возникла ошибка: %s", e.getMessage()));
        }
        return new SendMessage(userChatId, "Данные отчета записаны");
    }

    /**
     * Создать отчет если еще нет в БД
     * считаем что один отчет в день от пользователя
     *
     * @param user пользователь
     * @return найденный или созданный отчет
     */
    private Report addReportIfNotExist(User user) {
        Report report = reportService.findFirstByUserIdAndDate(user.getId(), LocalDateTime.now());
        if (report.getId() == 0L) {
            report.setUser(user);
            report.setAnimal(user.getAnimal());
            report.setDate(LocalDateTime.now());
            reportService.create(report);
        }
        return report;
    }

    /**
     * Контент фото, максимальный размер фото
     *
     * @param photoSize размеры фото
     * @return контент
     */
    private byte[] getPhotoContent(PhotoSize[] photoSize) {
        logger.info("upload report photo");
        try {
            GetFileResponse getFileResponse = telegramBot.execute(new GetFile(photoSize[photoSize.length - 1].fileId()));
            return telegramBot.getFileContent(getFileResponse.file());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * Поиск номера сообщения для ответа пользователю волонтером
     *
     * @param message сообщение для поиска номера
     * @return id сообщения
     */
    private int getMessageId(String message) {
        Pattern pattern = Pattern.compile(USER_VOLUNTEER_MSG_TEMPL);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(2));
        }
        return 0;
    }
}