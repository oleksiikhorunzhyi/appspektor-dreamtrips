package com.messenger.util;

public class OpenedConversationTracker {

    private String openedConversationId;

    public void setOpenedConversation(String openedConversationId) {
        this.openedConversationId = openedConversationId;
    }

    public String getOpenedConversationId() {
        return openedConversationId;
    }
}
