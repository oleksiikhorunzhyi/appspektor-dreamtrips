package com.messenger.delegate.chat.flagging;

public class FlagMessageException extends Exception {
    private String messageId;
    private String reason;

    public FlagMessageException(String messageId, String reason) {
        this.messageId = messageId;
        this.reason = reason;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getReason() {
        return reason;
    }
}
