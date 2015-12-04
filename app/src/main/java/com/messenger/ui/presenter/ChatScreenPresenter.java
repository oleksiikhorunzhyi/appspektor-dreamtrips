package com.messenger.ui.presenter;

import com.messenger.model.ChatConversation;
import com.messenger.ui.view.ChatScreen;

public interface ChatScreenPresenter extends ActivityAwareViewStateMvpPresenter<ChatScreen> {
    void loadChatConversation();
    boolean onNewMessageFromUi(String message);
    void setChatConversation(ChatConversation chatConversation);
}

