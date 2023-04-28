package com.skypro.telegram_team.listener.buffers;

import com.skypro.telegram_team.listener.buffers.Question;
import com.skypro.telegram_team.listener.buffers.QuestionsBuffer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuestionsBufferTest {
    private QuestionsBuffer out;
    private Question expected;

    @BeforeEach
    void setUp() {
        out = new QuestionsBuffer();
        expected = new Question(11L, 12L);
        expected.setId(1);
        expected.setQuestion("question");
        expected.setAnswer("answer");
    }

    @Test
    void addQuestion() {
        //When
        out.addQuestion(expected);
        //Then
        Assertions.assertThat(out.getQuestionById(1).isPresent()).isTrue();
        Assertions.assertThat(out.getQuestionById(1).get().getUserChatId()).isEqualTo(11L);
        Assertions.assertThat(out.getQuestionById(1).get().getVolunteerChatId()).isEqualTo(12L);
    }

    @Test
    void delQuestion() {
        //Given
        out.addQuestion(expected);
        //When
        out.delQuestion(expected);
        //Then
        Assertions.assertThat(out.getQuestionById(1).isPresent()).isFalse();
    }

    @Test
    void getQuestionById() {
        //Given
        out.addQuestion(expected);
        //When
        var actual = out.getQuestionById(1);
        //Then
        Assertions.assertThat(actual.isPresent()).isTrue();
        Assertions.assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    void getQuestionByUserChat() {
        //Given
        out.addQuestion(expected);
        //When
        var actual = out.getQuestionByUserChat(11L);
        //Then
        Assertions.assertThat(actual.isPresent()).isTrue();
        Assertions.assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    void getQuestionByVolunteerChat() {
        //Given
        out.addQuestion(expected);
        //When
        var actual = out.getQuestionByVolunteerChat(12L);
        //Then
        Assertions.assertThat(actual.isPresent()).isTrue();
        Assertions.assertThat(actual.get()).isEqualTo(expected);
    }
}