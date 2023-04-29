package com.skypro.telegram_team.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.skypro.telegram_team.exceptions.InvalidDataException;
import com.skypro.telegram_team.listener.buffers.Question;
import com.skypro.telegram_team.listener.buffers.QuestionsBuffer;
import com.skypro.telegram_team.listener.buffers.Request;
import com.skypro.telegram_team.listener.buffers.RequestsBuffer;
import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.models.User;
import com.skypro.telegram_team.services.ReportService;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramBotMessageListenerTest {
    @Mock
    private TelegramBot telegramBot;
    @Mock
    private QuestionsBuffer questionsBuffer;
    @Mock
    private RequestsBuffer requestsBuffer;
    @Mock
    private ShelterService shelterService;
    @Mock
    private UserService userService;
    @Mock
    private ReportService reportService;

    @InjectMocks
    private TelegramBotMessageListener out;

    @BeforeEach
    void setUp() {
        out = new TelegramBotMessageListener(telegramBot, questionsBuffer, requestsBuffer, shelterService, userService, reportService);
        when(userService.findByTelegramId(any())).thenReturn(TelegramBotListenerUtil.mockUser());
    }

    @ParameterizedTest
    @MethodSource("provideParamsForMenuTests")
    void processMessage_MenuItems(String menuText, String message) throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdate(menuText);
        //When
        var actual = out.processMessage(update.message());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo(message);
        Assertions.assertThat(actual.get(0).getParameters().get("reply_markup")).isNotNull();
    }

    static Stream<Arguments> provideParamsForMenuTests() {
        return Stream.of(
                Arguments.of(Menu.START.getText(), "Привет! Для продолжения работы выберите приют."),
                Arguments.of(Menu.SET_SHELTER.getText(), "Выберите приют"),
                Arguments.of(Menu.GET_INFO.getText(), "Информация о приюте"),
                Arguments.of(Menu.GET_ANIMAL.getText(), "Как взять животное"),
                Arguments.of(Menu.SEND_REPORT.getText(), "Какие данные отправить?"),
                Arguments.of(Menu.SET_USER_DATA.getText(), "Какие данные записать?"),
                Arguments.of(Menu.ASK_VOLUNTEER.getText(), "Кого спросить?")
        );
    }

    @Test
    void processMessage_UserSavePhone() throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdate("+79511338877");
        Request request = new Request(update.message().chat().id());
        request.setUserPhoneRequested(true);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(userService.update(any(), any())).thenReturn(TelegramBotListenerUtil.mockUser());
        var actual = out.processMessage(update.message());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("Данные пользователя записаны");
    }

    @Test
    void processMessage_UserSaveWithException() throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdate("11@ru");
        Request request = new Request(update.message().chat().id());
        request.setUserEmailRequested(true);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(userService.update(any(), any())).thenThrow(new InvalidDataException("error"));
        var actual = out.processMessage(update.message());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("Возникла ошибка: error");
    }

    @Test
    void processMessage_ReportSaveDiet() throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdate("ok");
        Request request = new Request(update.message().chat().id());
        request.setReportDietRequested(true);
        Report report = new Report();
        report.setId(1L);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(reportService.findFirstByUserIdAndDate(any(), any())).thenReturn(report);
        when(reportService.update(any(), any())).thenReturn(report);
        var actual = out.processMessage(update.message());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("Данные отчета записаны");
    }

    @Test
    void processMessage_ReportSavePhoto() throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdateWithPhoto();
        Request request = new Request(update.message().chat().id());
        request.setReportPhotoRequested(true);
        Report report = new Report();
        report.setId(1L);
        //When
        when(telegramBot.execute(any())).thenReturn(TelegramBotListenerUtil.generateResponseOk());
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(reportService.findFirstByUserIdAndDate(any(), any())).thenReturn(report);
        when(reportService.update(any(), any())).thenReturn(report);
        GetFileResponse getFileResponse = mock(GetFileResponse.class);
        File file = mock(File.class);
        when(getFileResponse.file()).thenReturn(file);
        AtomicReference<SendMessage> atomicReference = new AtomicReference<>();
        when(telegramBot.execute(any())).thenAnswer(invocationOnMock -> {
            Object sendRequest = invocationOnMock.getArgument(0);
            if (sendRequest instanceof GetFile) {
                return getFileResponse;
            } else if (sendRequest instanceof SendMessage) {
                atomicReference.set((SendMessage) sendRequest);
                return null;
            }
            return null;
        });
        var actual = out.processMessage(update.message());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("Данные отчета записаны");
    }

    @Test
    void processMessage_UserFirstReport() throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdate("ok");
        Request request = new Request(update.message().chat().id());
        request.setReportBehaviorRequested(true);
        Report report = new Report();
        report.setId(1L);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(reportService.findFirstByUserIdAndDate(any(), any())).thenReturn(new Report());
        when(reportService.create(any())).thenReturn(report);
        var actual = out.processMessage(update.message());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("Данные отчета записаны");
    }

    @Test
    void processMessage_ReportSaveWithException() throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdate("ok");
        Request request = new Request(update.message().chat().id());
        request.setReportBehaviorRequested(true);
        Report report = new Report();
        report.setId(1L);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(reportService.findFirstByUserIdAndDate(any(), any())).thenReturn(report);
        when(reportService.update(any(), any())).thenThrow(new InvalidDataException("error"));
        var actual = out.processMessage(update.message());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("Возникла ошибка: error");
    }

    @Test
    void processMessage_SendQuestionToVolunteer() throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdate("question");
        Question question = new Question(update.message().chat().id(), 12L);
        //When
        when(questionsBuffer.getQuestionByUserChat(update.message().chat().id())).thenReturn(Optional.of(question));
        var actual = out.processMessage(update.message());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.size()).isEqualTo(2);
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(12L);
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("1: Сообщение от пользователя, для ответа используйте reply:\n question");
        Assertions.assertThat(actual.get(1).getParameters().get("chat_id")).isEqualTo(11L);
        Assertions.assertThat(actual.get(1).getParameters().get("text")).isEqualTo("Сообщение отправлено волонтеру");
    }

    @Test
    void processMessage_SendReplyFromVolunteer() throws Exception {
        //Given
        String replyMessage = "1: Сообщение от пользователя: вопрос";
        Update update = TelegramBotListenerUtil.generateUpdateWithReply(replyMessage);
        Question question = new Question(11L, 12L);
        question.setId(1);
        //When
        when(questionsBuffer.getQuestionById(any())).thenReturn(Optional.of(question));
        var actual = out.processMessage(update.message());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("Ответ волонтера: \n" + "ответ");
    }

    @Test
    void processMessage_UserFirstMessage() throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdate("/start");
        //When
        when(userService.findByTelegramId(any())).thenReturn(new User());
        when(userService.create(any())).thenReturn(TelegramBotListenerUtil.mockUser());
        var actual = out.processMessage(update.message());
        //Then
        Assertions.assertThat(actual.isEmpty()).isFalse();
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("Привет! Для продолжения работы выберите приют.");
    }

    @Test
    void processMessage_WithoutResponse() throws Exception {
        //Given
        Update update = TelegramBotListenerUtil.generateUpdate("zzz");
        //When
        var actual = out.processMessage(update.message());
        //Then
        Assertions.assertThat(actual.isEmpty()).isTrue();
    }
}