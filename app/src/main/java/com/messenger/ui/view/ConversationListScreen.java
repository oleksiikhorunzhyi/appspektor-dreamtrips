package com.messenger.ui.view;

import com.messenger.model.ChatConversation;

import java.util.List;

public interface ConversationListScreen extends ActivityAwareScreen {
    void showLoading();
    void showContent();
    void showError(Throwable e);
    void setConversationList(List<ChatConversation> chatConversationList);
}
