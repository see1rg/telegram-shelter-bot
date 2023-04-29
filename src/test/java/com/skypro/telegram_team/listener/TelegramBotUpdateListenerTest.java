package com.skypro.telegram_team.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramBotUpdateListenerTest {
    @Mock
    private TelegramBotMessageListener messageListener;
    @Mock
    private TelegramBotCallbackListener callbackListener;

    @InjectMocks
    private TelegramBotUpdateListener out;

    @BeforeEach
    void setUp() {
        out = new TelegramBotUpdateListener(messageListener, callbackListener);
    }

    @Test
    void processUpdate_message() throws IOException {
        //Given
        Update update = generateUpdate("text");
        List<SendMessage> expected = Collections.singletonList(
                new SendMessage(update.message().chat().id(), "message"));
        //When
        when(messageListener.processMessage(update.message())).thenReturn(expected);
        out.processUpdate(update);
        //Then
        Assertions.assertThat(expected.get(0).getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(expected.get(0).getParameters().get("text")).isEqualTo("message");
    }

    @Test
    void processUpdate_callback() throws IOException {
        //Given
        Update update = generateUpdateWithCallback("data");
        List<SendMessage> expected = Collections.singletonList(
                new SendMessage(update.message().chat().id(), "message"));
        //When
        when(callbackListener.processCallback(update.callbackQuery())).thenReturn(expected);
        out.processUpdate(update);
        //Then
        Assertions.assertThat(expected.get(0).getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        Assertions.assertThat(expected.get(0).getParameters().get("text")).isEqualTo("message");
    }

    private Update generateUpdate(String text) throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.listener", "update.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));

        return BotUtils.fromJson(json.replace("%text%", text), Update.class);
    }

    private Update generateUpdateWithCallback(String callbackData) throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.listener", "updateWithCallback.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));

        return BotUtils.fromJson(json.replace("%data%", callbackData), Update.class);
    }
}