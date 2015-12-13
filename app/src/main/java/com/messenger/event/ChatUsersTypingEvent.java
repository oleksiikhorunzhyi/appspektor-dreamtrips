package com.messenger.event;


import com.messenger.model.ChatUser;

import java.util.List;

public class ChatUsersTypingEvent {
    // add conversation id here
    public List<ChatUser> typingUsers;

    public ChatUsersTypingEvent(List<ChatUser> typingUsers) {
        this.typingUsers = typingUsers;
    }
}
