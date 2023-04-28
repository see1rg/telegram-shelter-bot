package com.skypro.telegram_team.keyboards;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.pengrad.telegrambot.response.SendResponse;
import com.skypro.telegram_team.exceptions.InvalidDataException;
import com.skypro.telegram_team.keyboards.buffers.Question;
import com.skypro.telegram_team.keyboards.buffers.QuestionsBuffer;
import com.skypro.telegram_team.keyboards.buffers.Request;
import com.skypro.telegram_team.keyboards.buffers.RequestsBuffer;
import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Report;
import com.skypro.telegram_team.models.Shelter;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeyboardServiceExtTest {
    @Mock
    private TelegramBot telegramBot;
    @Mock
    private UserService userService;
    @Mock
    private ReportService reportService;
    @Mock
    private ShelterService shelterService;
    @Mock
    private RequestsBuffer requestsBuffer;
    @Mock
    private QuestionsBuffer questionsBuffer;

    @InjectMocks
    private KeyboardServiceExt out;

    @BeforeEach
    public void setUp() {
        out = new KeyboardServiceExt(telegramBot, userService, reportService, shelterService, questionsBuffer, requestsBuffer);
        when(userService.findByTelegramId(any())).thenReturn(mockUser());
        when(telegramBot.execute(any())).thenReturn(generateResponseOk());
    }

    //Тесты меню
    @ParameterizedTest
    @MethodSource("provideParamsForMenuTests")
    void processUpdateMenuItems(String menuText, String message) throws Exception {
        //Given
        Update update = generateUpdate(menuText);
        //When
        out.processUpdate(update);
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(message);
        Assertions.assertThat(actual.getParameters().get("reply_markup")).isNotNull();
    }

    static Stream<Arguments> provideParamsForMenuTests() {
        return Stream.of(Arguments.of(KeyboardServiceExt.Menu.START.getText(), "Привет! Для продолжения работы выберите приют."),
                Arguments.of(KeyboardServiceExt.Menu.SET_SHELTER.getText(), "Выберите приют"),
                Arguments.of(KeyboardServiceExt.Menu.GET_INFO.getText(), "Информация о приюте"),
                Arguments.of(KeyboardServiceExt.Menu.GET_ANIMAL.getText(), "Как взять животное"),
                Arguments.of(KeyboardServiceExt.Menu.SEND_REPORT.getText(), "Какие данные отправить?"),
                Arguments.of(KeyboardServiceExt.Menu.SET_USER_DATA.getText(), "Какие данные записать?"),
                Arguments.of(KeyboardServiceExt.Menu.ASK_VOLUNTEER.getText(), "Кого спросить?")
        );
    }

    //Тесты команд inline keyboard
    @ParameterizedTest
    @MethodSource("provideParamsForCallbackTests")
    void processUpdateCallbacks(String command, String message) throws Exception {
        //Given
        Update update = generateUpdateWithCallback(command);
        //When
        out.processUpdate(update);
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.callbackQuery().message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(message);
    }

    @ParameterizedTest
    @MethodSource("provideParamsForVolunteersTests")
    void processUpdateCallbacksForVolunteer(String command, String message, boolean searchVolunteer, boolean noFree) throws Exception {
        //Given
        Update update = generateUpdateWithCallback(command);
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
        out.processUpdate(update);
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.callbackQuery().message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo(message);
    }

    static Stream<Arguments> provideParamsForCallbackTests() {
        return Stream.of(
                Arguments.of(KeyboardServiceExt.Command.INF_ADDRESS.name(), mockShelter().getAddress()),
                Arguments.of(KeyboardServiceExt.Command.INF_SCHEDULE.name(), mockShelter().getSchedule()),
                Arguments.of(KeyboardServiceExt.Command.INF_SCHEME.name(), mockShelter().getScheme()),
                Arguments.of(KeyboardServiceExt.Command.INF_SAFETY.name(), mockShelter().getSafety()),
                Arguments.of(KeyboardServiceExt.Command.HOW_RULES.name(), mockShelter().getRules()),
                Arguments.of(KeyboardServiceExt.Command.HOW_DOCS.name(), mockShelter().getDocs()),
                Arguments.of(KeyboardServiceExt.Command.HOW_MOVE.name(), mockShelter().getMovement()),
                Arguments.of(KeyboardServiceExt.Command.HOW_ARRANGE.name(), mockShelter().getArrangements()),
                Arguments.of(KeyboardServiceExt.Command.HOW_ARRANGE_PUPPY.name(), mockShelter().getArrangementsForPuppy()),
                Arguments.of(KeyboardServiceExt.Command.HOW_ARRANGE_CRIPPLE.name(), mockShelter().getArrangementsForCripple()),
                Arguments.of(KeyboardServiceExt.Command.HOW_EXPERT_FIRST.name(), mockShelter().getExpertAdvicesFirst()),
                Arguments.of(KeyboardServiceExt.Command.HOW_EXPERT_NEXT.name(), mockShelter().getExpertAdvicesNext()),
                Arguments.of(KeyboardServiceExt.Command.HOW_REJECT_REASONS.name(), mockShelter().getRejectReasons()),
                Arguments.of(KeyboardServiceExt.Command.SAVE_USER_PHONE.name(), "Напишите телефон"),
                Arguments.of(KeyboardServiceExt.Command.SAVE_USER_EMAIL.name(), "Напишите почту"),
                Arguments.of(KeyboardServiceExt.Command.SEND_PHOTO.name(), "Отправьте фото"),
                Arguments.of(KeyboardServiceExt.Command.SEND_DIET.name(), "Опишите диету"),
                Arguments.of(KeyboardServiceExt.Command.SEND_BEHAVIOR.name(), "Опишите поведение"),
                Arguments.of(KeyboardServiceExt.Command.SEND_WELL_BEING.name(), "Опишите самочувствие")
        );
    }

    static Stream<Arguments> provideParamsForVolunteersTests() {
        return Stream.of(Arguments.of(KeyboardServiceExt.Command.ASK_VOLUNTEER.name() + "123", "Напишите вопрос", false, false),
                Arguments.of(KeyboardServiceExt.Command.ASK_ANY_VOLUNTEER.name(), "Напишите вопрос", true, false),
                Arguments.of(KeyboardServiceExt.Command.ASK_ANY_VOLUNTEER.name(), "Нет свободных волонтеров", true, true)
        );
    }

    @Test
    void processUpdateSaveShelter() throws Exception {
        //Given
        Update update = generateUpdateWithCallback(KeyboardServiceExt.Command.SAVE_SHELTER.name() + "1");
        //When
        when(shelterService.findById(any())).thenReturn(mockShelter());
        when(userService.update(any(), any())).thenReturn(mockUser());
        out.processUpdate(update);
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.callbackQuery().message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo("Приют выбран");
        Assertions.assertThat(actual.getParameters().get("reply_markup")).isNotNull();
    }

    @Test
    void processUpdateSaveShelterNull() throws Exception {
        //Given
        Update update = generateUpdateWithCallback(KeyboardServiceExt.Command.SAVE_SHELTER.name() + "1");
        //When
        when(shelterService.findById(any())).thenReturn(null);
        when(userService.update(any(), any())).thenReturn(mockUser());
        out.processUpdate(update);
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.callbackQuery().message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo("Приют не выбран");
    }

    //Тесты сохранения данных
    @Test
    void processUpdateUserSavePhone() throws Exception {
        //Given
        Update update = generateUpdate("+7");
        Request request = new Request(update.message().chat().id());
        request.setUserPhoneRequested(true);
        User user = new User();
        user.setTelegramId(update.message().chat().id());
        user.setId(1L);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(userService.findByTelegramId(any())).thenReturn(user);
        when(userService.update(any(), any())).thenReturn(user);
        out.processUpdate(update);
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo("Данные пользователя записаны");
    }

    @Test
    void processUpdateUserSaveWithException() throws Exception {
        //Given
        Update update = generateUpdate("11@ru");
        Request request = new Request(update.message().chat().id());
        request.setUserEmailRequested(true);
        User user = new User();
        user.setTelegramId(update.message().chat().id());
        user.setId(1L);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(userService.findByTelegramId(any())).thenReturn(user);
        when(userService.update(any(), any())).thenThrow(new InvalidDataException("error"));
        out.processUpdate(update);
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo("Возникла ошибка: error");
    }

    @Test
    void processUpdateReportSaveDiet() throws Exception {
        //Given
        Update update = generateUpdate("ok");
        Request request = new Request(update.message().chat().id());
        request.setReportDietRequested(true);
        User user = new User();
        user.setTelegramId(update.message().chat().id());
        user.setId(1L);
        Report report = new Report();
        report.setId(1L);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(userService.findByTelegramId(any())).thenReturn(user);
        when(reportService.findFirstByUserIdAndDate(any(), any())).thenReturn(report);
        when(reportService.update(any(), any())).thenReturn(report);
        out.processUpdate(update);
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo("Данные отчета записаны");
    }

    @Test
    void processUpdateReportSavePhoto() throws Exception {
        //Given
        Update update = generateUpdateWithPhoto();
        Request request = new Request(update.message().chat().id());
        request.setReportPhotoRequested(true);
        User user = new User();
        user.setTelegramId(update.message().chat().id());
        user.setId(1L);
        Report report = new Report();
        report.setId(1L);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(userService.findByTelegramId(any())).thenReturn(user);
        when(reportService.findFirstByUserIdAndDate(any(), any())).thenReturn(report);
        when(reportService.update(any(), any())).thenReturn(report);

        GetFileResponse getFileResponse = mock(GetFileResponse.class);
        File file = mock(File.class);
        when(getFileResponse.file()).thenReturn(file);
        AtomicReference<SendMessage> atomicReference = new AtomicReference<>();
        when(telegramBot.execute(any())).thenAnswer(invocationOnMock -> {
           Object sendRequest = invocationOnMock.getArgument(0);
           if (sendRequest instanceof GetFile){
                return getFileResponse;
           } else if (sendRequest instanceof SendMessage){
               atomicReference.set((SendMessage) sendRequest);
               return null;
           }
           return null;
        });

        out.processUpdate(update);
        //Then
        Assertions.assertThat(atomicReference.get().getParameters().get("text")).isEqualTo("Данные отчета записаны");
    }

    @Test
    void processUpdateReportSaveWithException() throws Exception {
        //Given
        Update update = generateUpdate("ok");
        Request request = new Request(update.message().chat().id());
        request.setReportBehaviorRequested(true);
        User user = new User();
        user.setTelegramId(update.message().chat().id());
        user.setId(1L);
        Report report = new Report();
        report.setId(1L);
        //When
        when(requestsBuffer.getRequest(any())).thenReturn(Optional.of(request));
        when(userService.findByTelegramId(any())).thenReturn(user);
        when(reportService.findFirstByUserIdAndDate(any(), any())).thenReturn(report);
        when(reportService.update(any(), any())).thenThrow(new InvalidDataException("error"));
        out.processUpdate(update);
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo("Возникла ошибка: error");
    }

    //Тесты сообщений для/от волонтера
    @Test
    void processUpdateSendQuestionToVolunteer() throws Exception {
        //Given
        Update update = generateUpdate("question");
        Question question = new Question(update.message().chat().id(), 12L);
        //When
        when(questionsBuffer.getQuestionByUserChat(update.message().chat().id())).thenReturn(Optional.of(question));
        out.processUpdate(update);
        //Then
        List<SendMessage> actual = getActualSendMessages();
        Assertions.assertThat(actual.size()).isEqualTo(2);
        Assertions.assertThat(actual.get(0).getParameters().get("chat_id")).isEqualTo(12L);
        Assertions.assertThat(actual.get(0).getParameters().get("text")).isEqualTo("1: Сообщение от пользователя, для ответа используйте reply:\n question");
        Assertions.assertThat(actual.get(1).getParameters().get("chat_id")).isEqualTo(11L);
        Assertions.assertThat(actual.get(1).getParameters().get("text")).isEqualTo("Сообщение отправлено волонтеру");
    }

    @Test
    void processUpdateSendReplyFromVolunteer() throws Exception {
        //Given
        String replyMessage = "1: Сообщение от пользователя: вопрос";
        Update update = generateUpdateWithReply(replyMessage);
        Question question = new Question(11L, 12L);
        question.setId(1);
        //When
        when(questionsBuffer.getQuestionById(any())).thenReturn(Optional.of(question));
        out.processUpdate(update);
        //Then
        SendMessage actual = getActualSendMessage();
        Assertions.assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(actual.getParameters().get("text")).isEqualTo("Ответ волонтера: \n" + "ответ");
    }

    private Update generateUpdate(String text) throws IOException {
        //Так почему-то не работает
        //String json = Files.readString(Path.of(Objects.requireNonNull(KeyboardServiceExtTest.class.getResource("update.json")).toURI()));
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.keyboards", "update.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));

        return BotUtils.fromJson(json.replace("%text%", text), Update.class);
    }

    private Update generateUpdateWithCallback(String callbackData) throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.keyboards", "updateWithCallback.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));

        return BotUtils.fromJson(json.replace("%data%", callbackData), Update.class);
    }

    private Update generateUpdateWithReply(String replyText) throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.keyboards", "updateWithReplyMessage.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));
        json = json.replace("%replyText%", replyText).replace("%text%", "ответ");
        return BotUtils.fromJson(json, Update.class);
    }

    private Update generateUpdateWithPhoto() throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.keyboards", "updateWithPhoto.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));
        return BotUtils.fromJson(json, Update.class);
    }

    private SendResponse generateResponseOk() {
        return BotUtils.fromJson("""
                { "ok": true }""", SendResponse.class);
    }

    private SendMessage getActualSendMessage() {
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        return argumentCaptor.getValue();
    }

    private List<SendMessage> getActualSendMessages() {
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot, times(2)).execute(argumentCaptor.capture());
        return argumentCaptor.getAllValues();
    }

    private static User mockUser() {
        User user = new User();
        user.setId(1L);
        user.setTelegramId(11L);
        user.setName("name");
        user.setSurname("surname");
        user.setShelter(mockShelter());
        return user;
    }

    private static Shelter mockShelter() {
        Shelter shelter = new Shelter();
        shelter.setId(1L);
        shelter.setType(Animal.TypeAnimal.DOG);
        shelter.setAddress("address");
        shelter.setSchedule("schedule");
        shelter.setScheme("scheme");
        shelter.setSafety("safety");
        shelter.setDocs("docs");
        shelter.setRules("rules");
        shelter.setArrangements("arrangements");
        shelter.setArrangementsForPuppy("arrangementsForPuppy");
        shelter.setArrangementsForCripple("arrangementsForCripple");
        shelter.setMovement("move");
        shelter.setExpertAdvicesFirst("advice");
        shelter.setExpertAdvicesNext("advice");
        shelter.setRejectReasons("reason");
        return shelter;
    }
}