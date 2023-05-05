package com.skypro.telegram_team;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TelegramTeamApplication.class)
class TelegramTeamApplicationTests {

    @Autowired
    private TelegramTeamApplication app;

    @Test
    void contextLoads() {
        assertNotNull(app);
    }
}
