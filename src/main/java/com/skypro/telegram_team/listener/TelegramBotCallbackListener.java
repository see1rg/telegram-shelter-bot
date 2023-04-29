package com.skypro.telegram_team.listener;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import com.skypro.telegram_team.listener.buffers.Question;
import com.skypro.telegram_team.listener.buffers.QuestionsBuffer;
import com.skypro.telegram_team.listener.buffers.Request;
import com.skypro.telegram_team.listener.buffers.RequestsBuffer;
import com.skypro.telegram_team.models.Shelter;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.services.ShelterService;
import com.skypro.telegram_team.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class TelegramBotCallbackListener {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotCallbackListener.class);
    private final UserService userService;
    private final ShelterService shelterService;
    private final QuestionsBuffer questionsBuffer;
    private final RequestsBuffer requestsBuffer;

    public TelegramBotCallbackListener(UserService userService, ShelterService shelterService,
                                       QuestionsBuffer questionsBuffer, RequestsBuffer requestsBuffer) {
        this.userService = userService;
        this.shelterService = shelterService;
        this.questionsBuffer = questionsBuffer;
        this.requestsBuffer = requestsBuffer;
    }

    /**
     * Обработка сообщений с callback
     * (при нажатии на меню inline keyboard)
     *
     * @param callbackQuery команды inline keyboard
     * @return сообщения для отправки пользователю
     */
    public List<SendMessage> processCallback(CallbackQuery callbackQuery) {
        //callback команды
        Long userChatId = callbackQuery.message().chat().id();
        User user = userService.findByTelegramId(userChatId);

        if (callbackQuery.data().startsWith(Callback.SAVE_SHELTER.name())) {
            //Присвоить приют
            Long shelterId = Long.parseLong(callbackQuery.data().substring(Callback.SAVE_SHELTER.name().length()));
            return Collections.singletonList(assignUserToShelter(shelterId, user));
        }

        if (user.getShelter() != null) {
            Request request = new Request(userChatId);
            if (callbackQuery.data().equals(Callback.INF_ADDRESS.name())) {
                //Адрес
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getAddress()));
            } else if (callbackQuery.data().equals(Callback.INF_SCHEDULE.name())) {
                //Расписание
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getSchedule()));
            } else if (callbackQuery.data().equals(Callback.INF_SCHEME.name())) {
                //Схема проезда
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getScheme()));
            } else if (callbackQuery.data().equals(Callback.INF_SAFETY.name())) {
                //Техника безопасности
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getSafety()));
            } else if (callbackQuery.data().equals(Callback.HOW_RULES.name())) {
                //Правила знакомства с собакой
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getRules()));
            } else if (callbackQuery.data().equals(Callback.HOW_DOCS.name())) {
                //Список документов
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getDocs()));
            } else if (callbackQuery.data().equals(Callback.HOW_MOVE.name())) {
                //Рекомендации по транспортировке
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getMovement()));
            } else if (callbackQuery.data().equals(Callback.HOW_ARRANGE.name())) {
                //Рекомендации по обустройству
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getArrangements()));
            } else if (callbackQuery.data().equals(Callback.HOW_ARRANGE_PUPPY.name())) {
                //Рекомендации по обустройству для щенка
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getArrangementsForPuppy()));
            } else if (callbackQuery.data().equals(Callback.HOW_ARRANGE_CRIPPLE.name())) {
                //Рекомендации по обустройству для собаки-инвалида
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getArrangementsForCripple()));
            } else if (callbackQuery.data().equals(Callback.HOW_EXPERT_FIRST.name())) {
                //Советы кинолога по первому общению
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getExpertAdvicesFirst()));
            } else if (callbackQuery.data().equals(Callback.HOW_EXPERT_NEXT.name())) {
                //Советы кинолога по дальнейшему общению
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getExpertAdvicesNext()));
            } else if (callbackQuery.data().equals(Callback.HOW_REJECT_REASONS.name())) {
                //Причины отказа
                return Collections.singletonList(new SendMessage(userChatId, user.getShelter().getRejectReasons()));
                //...
            } else if (callbackQuery.data().startsWith(Callback.ASK_VOLUNTEER.name())) {
                //Конкретный волонтера (чат выбранного волонтера в callback data)
                Long volunteerChatId = Long.parseLong(callbackQuery.data().substring(Callback.ASK_VOLUNTEER.name().length()));
                questionsBuffer.addQuestion(new Question(userChatId, volunteerChatId));
                return Collections.singletonList(new SendMessage(userChatId, "Напишите вопрос"));
            } else if (callbackQuery.data().startsWith(Callback.ASK_ANY_VOLUNTEER.name())) {
                //Любой волонтер (будет найден первый попавшийся)
                if (userService.findAnyVolunteer().isPresent()) {
                    var volunteer = userService.findAnyVolunteer().get();
                    Long volunteerChatId = volunteer.getTelegramId();
                    questionsBuffer.addQuestion(new Question(userChatId, volunteerChatId));
                    return Collections.singletonList(new SendMessage(userChatId, "Напишите вопрос"));
                } else {
                    return Collections.singletonList(new SendMessage(userChatId, "Нет свободных волонтеров"));
                }
                //...
            } else if (callbackQuery.data().equals(Callback.SAVE_USER_PHONE.name())) {
                //Телефон
                request.setUserPhoneRequested(true);
                requestsBuffer.addRequest(request);
                return Collections.singletonList(new SendMessage(userChatId, "Напишите телефон"));
            } else if (callbackQuery.data().equals(Callback.SAVE_USER_EMAIL.name())) {
                //Почта
                request.setUserEmailRequested(true);
                requestsBuffer.addRequest(request);
                return Collections.singletonList(new SendMessage(userChatId, "Напишите почту"));
            } else if (callbackQuery.data().equals(Callback.SEND_PHOTO.name())) {
                //Фото для отчета
                request.setReportPhotoRequested(true);
                requestsBuffer.addRequest(request);
                return Collections.singletonList(new SendMessage(userChatId, "Отправьте фото"));
            } else if (callbackQuery.data().equals(Callback.SEND_DIET.name())) {
                //Диета для отчета
                request.setReportDietRequested(true);
                requestsBuffer.addRequest(request);
                return Collections.singletonList(new SendMessage(userChatId, "Опишите диету"));
            } else if (callbackQuery.data().equals(Callback.SEND_BEHAVIOR.name())) {
                //Поведение для отчета
                request.setReportBehaviorRequested(true);
                requestsBuffer.addRequest(request);
                return Collections.singletonList(new SendMessage(userChatId, "Опишите поведение"));
            } else if (callbackQuery.data().equals(Callback.SEND_WELL_BEING.name())) {
                //Самочувствие для отчета
                request.setReportWellBeingRequest(true);
                requestsBuffer.addRequest(request);
                return Collections.singletonList(new SendMessage(userChatId, "Опишите самочувствие"));
            }
        } else {
            return Collections.singletonList(new SendMessage(userChatId, "Приют не выбран"));
        }
        return Collections.emptyList();
    }

    /**
     * Сохранить выбранный приют
     *
     * @param shelterId id приюта
     * @param user      пользователь
     * @return сообщение для отправки пользователю
     */
    private SendMessage assignUserToShelter(Long shelterId, User user) {
        Shelter shelter = shelterService.findById(shelterId);
        user.setShelter(shelter);
        var sendMessage = updateUser(user);
        sendMessage = new SendMessage(user.getTelegramId(), "Приют выбран");
        sendMessage.replyMarkup(MenuKeyboard.MAIN_KEYBOARD.getKeyboard());
        return sendMessage;
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
}