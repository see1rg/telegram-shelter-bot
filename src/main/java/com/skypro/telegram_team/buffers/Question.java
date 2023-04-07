package com.skypro.telegram_team.buffers;

import lombok.Data;

/**
 * Вопрос от пользователя волонтеру
 */
@Data
public class Question {
    private Integer id;
    private Long userChatId;
    private Long volunteerChatId;
    private String question;
    private String answer;

    public Question(Long userChatId, Long volunteerChatId) {
        this(userChatId, volunteerChatId, null, null);
    }

    public Question(Long userChatId, Long volunteerChatId, String question, String answer) {
        this.userChatId = userChatId;
        this.volunteerChatId = volunteerChatId;
        this.question = question;
        this.answer = answer;
    }
}
