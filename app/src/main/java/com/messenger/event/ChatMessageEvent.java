package com.messenger.event;

import com.messenger.model.ChatMessage;

public class ChatMessageEvent {

    public ChatMessageEvent(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public ChatMessage chatMessage;
}
