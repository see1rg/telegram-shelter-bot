package com.skypro.telegram_team.listener.buffers;

import java.util.Objects;

/**
 * Запрос пользователя на обновление данных
 * пользователя или дневного отчета
 */
public class Request {
    //Чат
    private final Long chatId;
    //Запросы данных пользователя
    private boolean userPhoneRequested;
    private boolean userEmailRequested;
    //Запросы данных для отчета
    private boolean reportPhotoRequested;
    private boolean reportDietRequested;
    private boolean reportBehaviorRequested;
    private boolean reportWellBeingRequest;

    public Request(Long chatId) {
        this.chatId = chatId;
    }

    public void setUserPhoneRequested(boolean userPhoneRequested) {
        this.userPhoneRequested = userPhoneRequested;
    }

    public void setUserEmailRequested(boolean userEmailRequested) {
        this.userEmailRequested = userEmailRequested;
    }

    public void setReportPhotoRequested(boolean reportPhotoRequested) {
        this.reportPhotoRequested = reportPhotoRequested;
    }

    public void setReportDietRequested(boolean reportDietRequested) {
        this.reportDietRequested = reportDietRequested;
    }

    public void setReportBehaviorRequested(boolean reportBehaviorRequested) {
        this.reportBehaviorRequested = reportBehaviorRequested;
    }

    public void setReportWellBeingRequest(boolean reportWellBeingRequest) {
        this.reportWellBeingRequest = reportWellBeingRequest;
    }

    public Long getChatId() {
        return chatId;
    }

    public boolean isUserPhoneRequested() {
        return userPhoneRequested;
    }

    public boolean isUserEmailRequested() {
        return userEmailRequested;
    }

    public boolean isReportPhotoRequested() {
        return reportPhotoRequested;
    }

    public boolean isReportDietRequested() {
        return reportDietRequested;
    }

    public boolean isReportBehaviorRequested() {
        return reportBehaviorRequested;
    }

    public boolean isReportWellBeingRequest() {
        return reportWellBeingRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return chatId.equals(request.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId);
    }
}
