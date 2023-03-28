package com.skypro.telegram_team;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramTeamApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramTeamApplication.class, args);
    }

}
