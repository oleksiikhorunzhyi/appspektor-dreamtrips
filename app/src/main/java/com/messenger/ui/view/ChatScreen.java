package com.messenger.ui.view;

import com.messenger.messengerservers.entities.Message;
import com.messenger.model.ChatConversation;

public interface ChatScreen extends ActivityAwareScreen {
    void showLoading();
    void showContent();
    void showError(Throwable e);
    void setChatConversation(ChatConversation chatConversation);
    void onReceiveMessage(Message message);
    void onSendMessage(Message message);
}
