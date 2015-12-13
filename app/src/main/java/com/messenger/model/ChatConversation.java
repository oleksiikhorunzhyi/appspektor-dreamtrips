package com.messenger.model;

import android.os.Parcelable;

import java.util.List;

public interface ChatConversation extends Parcelable {
    ChatUser getConversationOwner();

    void setConversationOwner(ChatUser chatUser);

    List<ChatMessage> getMessages();

    void setChatMessages(List<ChatMessage> chatMessages);

    int getUnreadMessagesCount();
    
    List<ChatUser> getChatUsers();

    void setChatUsers(List<ChatUser> chatUsers);

    boolean isGroupConversation();
    
    List<ChatUser> getTypingUsers();
    
    void setTypingUsers(List<ChatUser> typingUsers);
    
    List<ChatUser> getOnlineUsers();
    
    String getConversationName();

    void setConversationName(String conversationName);
}
