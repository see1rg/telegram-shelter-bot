package com.skypro.telegram_team.listener;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.SendResponse;
import com.skypro.telegram_team.models.Animal;
import com.skypro.telegram_team.models.Shelter;
import com.skypro.telegram_team.models.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TelegramBotListenerUtil {
    public static Update generateUpdate(String text) throws IOException {
        //Так почему-то не работает
        //String json = Files.readString(Path.of(Objects.requireNonNull(KeyboardServiceExtTest.class.getResource("update.json")).toURI()));
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.listener", "update.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));
        return BotUtils.fromJson(json.replace("%text%", text), Update.class);
    }

    public static Update generateUpdateWithCallback(String callbackData) throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.listener", "updateWithCallback.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));

        return BotUtils.fromJson(json.replace("%data%", callbackData), Update.class);
    }

    public static Update generateUpdateWithReply(String replyText) throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.listener", "updateWithReplyMessage.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));
        json = json.replace("%replyText%", replyText).replace("%text%", "ответ");
        return BotUtils.fromJson(json, Update.class);
    }

    public static Update generateUpdateWithPhoto() throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources",
                "com.skypro.telegram_team.listener", "updateWithPhoto.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String json = Files.readString(Path.of(absolutePath));
        return BotUtils.fromJson(json, Update.class);
    }

    public static SendResponse generateResponseOk() {
        return BotUtils.fromJson("""
                { "ok": true }""", SendResponse.class);
    }

    public static User mockUser() {
        User user = new User();
        user.setId(1L);
        user.setTelegramId(11L);
        user.setName("name");
        user.setSurname("surname");
        user.setShelter(mockShelter());
        return user;
    }

    public static User mockVolunteer() {
        User user = new User();
        user.setId(2L);
        user.setTelegramId(12L);
        user.setName("name");
        user.setSurname("surname");
        user.setShelter(mockShelter());
        user.setVolunteer(true);
        return user;
    }

    public static Shelter mockShelter() {
        Shelter shelter = new Shelter();
        shelter.setId(1L);
        shelter.setType(Animal.TypeAnimal.DOG);
        shelter.setName("Dogs");
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
