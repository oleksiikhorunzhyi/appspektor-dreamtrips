package com.messenger.ui.view;

import com.messenger.model.ChatConversation;

public interface ChatScreen extends ActivityAwareScreen {
    void showLoading();
    void showContent();
    void showError(Throwable e);
    void setChatConversation(ChatConversation chatConversation);
}
