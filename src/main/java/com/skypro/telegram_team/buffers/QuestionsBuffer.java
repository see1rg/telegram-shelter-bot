package com.skypro.telegram_team.buffers;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Буфер для хранения в памяти вопросов от пользователя к волонтеру
 * При ответе на вопрос он должен удаляться
 */
@Component
public class QuestionsBuffer {
    private final List<Question> questions = new ArrayList<>();

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void delQuestion(Question question) {
        questions.remove(question);
    }

    public Optional<Question> getQuestionById(Integer id) {
        return questions.stream()
                .filter(q -> q.getId().equals(id))
                .findFirst();
    }

    public Optional<Question> getQuestionByUserChat(Long chatId) {
        return questions.stream()
                .filter(q -> q.getUserChatId().equals(chatId))
                .findFirst();
    }

    public Optional<Question> getQuestionByVolunteerChat(Long chatId) {
        return questions.stream()
                .filter(q -> q.getVolunteerChatId().equals(chatId))
                .findFirst();
    }
}
