package com.messenger.api.exception;

import io.techery.janet.JanetException;

public class UiMessageException extends JanetException {

    private String uiMessage;

    public UiMessageException(String uiMessage, Throwable cause) {
        super(cause);
        this.uiMessage = uiMessage;
    }

    public String getUiMessage() {
        return uiMessage;
    }
}
