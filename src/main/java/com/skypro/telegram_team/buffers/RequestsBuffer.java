package com.skypro.telegram_team.buffers;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Буфер для хранения:
 * - флагов на запрос данных пользователя
 * - флагов на запрос данных отчета
 */
@Component
public class RequestsBuffer {
    private final Map<Long, Request> requests = new HashMap<>();

    public void addRequest(Request request) {
        requests.put(request.getChatId(), request);
    }

    public void delRequest(Request request) {
        requests.remove(request.getChatId());
    }

    public Optional<Request> getRequest(Long chatId) {
        return Optional.ofNullable(requests.get(chatId));
    }
}
