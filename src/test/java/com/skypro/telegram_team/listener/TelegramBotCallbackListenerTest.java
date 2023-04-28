package com.skypro.telegram_team.listener;

import com.pengrad.telegrambot.model.Update;
import com.skypro.telegram_team.listener.buffers.QuestionsBuffer;
import com.skypro.telegram_team.listener.buffers.RequestsBuffer;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.services.ShelterService;
import com.skypro.telegram_team.services.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramBotCallbackListenerTest {
    @Mock
    private UserService userService;
    @Mock
    private ShelterService shelterService;
    @Mock
    private QuestionsBuffer questionsBuffer;
    @Mock
    private RequestsBuffer requestsBuffer;

    @InjectMocks
    private TelegramBotCallbackListener out;

    @BeforeEach
    void setUp() {
        out = new TelegramBotCallbackListener(userService, shelterService, questionsBuffer, requestsBuffer);
        when(userService.findByTelegramId(any())).thenReturn(TelegramBotListenerUtil.mockUser());
    }

    @ParameterizedTest
    @MethodSource("provideParamsForCallbackTests")
    void processCallback_Callbacks(String command, String message) throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdateWithCallback(command);
        //When
        var actual = out.processCallback(update.callbackQuery());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(update.callbackQuery().message().chat().id());
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo(message);
    }

    static Stream<Arguments> provideParamsForCallbackTests() {
        return Stream.of(
                Arguments.of(Callback.INF_ADDRESS.name(), TelegramBotListenerUtil.mockShelter().getAddress()),
                Arguments.of(Callback.INF_SCHEDULE.name(), TelegramBotListenerUtil.mockShelter().getSchedule()),
                Arguments.of(Callback.INF_SCHEME.name(), TelegramBotListenerUtil.mockShelter().getScheme()),
                Arguments.of(Callback.INF_SAFETY.name(), TelegramBotListenerUtil.mockShelter().getSafety()),
                Arguments.of(Callback.HOW_RULES.name(), TelegramBotListenerUtil.mockShelter().getRules()),
                Arguments.of(Callback.HOW_DOCS.name(), TelegramBotListenerUtil.mockShelter().getDocs()),
                Arguments.of(Callback.HOW_MOVE.name(), TelegramBotListenerUtil.mockShelter().getMovement()),
                Arguments.of(Callback.HOW_ARRANGE.name(), TelegramBotListenerUtil.mockShelter().getArrangements()),
                Arguments.of(Callback.HOW_ARRANGE_PUPPY.name(), TelegramBotListenerUtil.mockShelter().getArrangementsForPuppy()),
                Arguments.of(Callback.HOW_ARRANGE_CRIPPLE.name(), TelegramBotListenerUtil.mockShelter().getArrangementsForCripple()),
                Arguments.of(Callback.HOW_EXPERT_FIRST.name(), TelegramBotListenerUtil.mockShelter().getExpertAdvicesFirst()),
                Arguments.of(Callback.HOW_EXPERT_NEXT.name(), TelegramBotListenerUtil.mockShelter().getExpertAdvicesNext()),
                Arguments.of(Callback.HOW_REJECT_REASONS.name(), TelegramBotListenerUtil.mockShelter().getRejectReasons()),
                Arguments.of(Callback.SAVE_USER_PHONE.name(), "Напишите телефон"),
                Arguments.of(Callback.SAVE_USER_EMAIL.name(), "Напишите почту"),
                Arguments.of(Callback.SEND_PHOTO.name(), "Отправьте фото"),
                Arguments.of(Callback.SEND_DIET.name(), "Опишите диету"),
                Arguments.of(Callback.SEND_BEHAVIOR.name(), "Опишите поведение"),
                Arguments.of(Callback.SEND_WELL_BEING.name(), "Опишите самочувствие")
        );
    }

    @ParameterizedTest
    @MethodSource("provideParamsForVolunteersTests")
    void processCallback_CallbacksForVolunteer(String command, String message, boolean searchVolunteer, boolean noFree) throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdateWithCallback(command);
        //When
        if (searchVolunteer) {
            if (!noFree) {
                User volunteer = new User();
                volunteer.setTelegramId(123);
                when(userService.findAnyVolunteer()).thenReturn(Optional.of(volunteer));
            } else {
                when(userService.findAnyVolunteer()).thenReturn(Optional.empty());
            }
        }
        var actual = out.processCallback(update.callbackQuery());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(update.callbackQuery().message().chat().id());
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo(message);
    }

    static Stream<Arguments> provideParamsForVolunteersTests() {
        return Stream.of(Arguments.of(Callback.ASK_VOLUNTEER.name() + "123", "Напишите вопрос", false, false),
                Arguments.of(Callback.ASK_ANY_VOLUNTEER.name(), "Напишите вопрос", true, false),
                Arguments.of(Callback.ASK_ANY_VOLUNTEER.name(), "Нет свободных волонтеров", true, true)
        );
    }

    @Test
    void processCallback_SaveShelter() throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdateWithCallback(Callback.SAVE_SHELTER.name() + "1");
        //When
        when(shelterService.findById(any())).thenReturn(TelegramBotListenerUtil.mockShelter());
        when(userService.update(any(), any())).thenReturn(TelegramBotListenerUtil.mockUser());
        var actual = out.processCallback(update.callbackQuery());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(update.callbackQuery().message().chat().id());
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("Приют выбран");
        Assertions.assertThat(actual.get(0).getParameters().get("reply_markup")).isNotNull();
    }
}