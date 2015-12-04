package com.messenger.model;

import android.os.Parcelable;

import java.util.List;

public interface ChatConversation extends Parcelable {
    ChatUser getConversationOwner();
    void setConversationOwner(ChatUser chatUser);
    List<ChatMessage> getMessages();
    void setChatMessages(List<ChatMessage> chatMessages);
    List<ChatUser> getChatUsers();
    void setChatUsers(List<ChatUser> chatUsers);
    String getConversationName();
    void setConversationName(String conversationName);
}
