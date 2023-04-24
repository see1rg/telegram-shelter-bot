package com.skypro.telegram_team.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.skypro.telegram_team.keyboards.KeyboardServiceExt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramBotUpdatesListenerTest {
    @Mock
    private TelegramBot telegramBot;
    @Mock
    private ApplicationContext context;
    @Mock
    private KeyboardServiceExt keyboardService;

    @InjectMocks
    TelegramBotUpdatesListener out;

    @BeforeEach
    void setUp() {
        out = new TelegramBotUpdatesListener(telegramBot, context);
        when(context.getBean(KeyboardServiceExt.class)).thenReturn(keyboardService);
    }

    @Test
    void process() throws Exception {
        //Given
        Update update = generateUpdate("/start");
        //When
        out.process(Collections.singletonList(update));
        //Then
        Mockito.verify(keyboardService, Mockito.times(1)).processUpdate(update);
    }

    private Update generateUpdate(String text) throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.keyboards", "update.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));

        return BotUtils.fromJson(json.replace("%text%", text), Update.class);
    }
}