package com.skypro.telegram_team.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InvalidDataExceptionTest {
    @Test
    public void testInvalidDataException() {
        String errorMessage = "Invalid data provided";
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        // Проверяем, что сообщение об ошибке верное
        assertEquals(errorMessage, ex.getReason());

        // Проверяем, что статус-код верный
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    public void testInvalidDataExceptionWithAllParams() {
        String errorMessage = "Invalid data provided";
        Exception cause = new Exception("Something went wrong");
        boolean enableSuppression = true;
        boolean writableStackTrace = true;
        InvalidDataException ex = new InvalidDataException(errorMessage, cause, enableSuppression, writableStackTrace);
        assertNotNull(ex);
        assertEquals(errorMessage, ex.getMessage());
        assertEquals(cause, ex.getCause());
        assertEquals(enableSuppression, ex.getSuppressed().length == 0);
        assertEquals(writableStackTrace, ex.getStackTrace().length > 0);
    }


}
