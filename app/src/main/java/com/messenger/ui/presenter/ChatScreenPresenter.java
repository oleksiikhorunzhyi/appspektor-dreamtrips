package com.messenger.ui.presenter;

import com.messenger.model.ChatConversation;
import com.messenger.ui.view.ChatScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;

public interface ChatScreenPresenter extends ActivityAwareViewStateMvpPresenter<ChatScreen, ChatLayoutViewState> {
    public static final String EXTRA_CHAT_CONVERSATION = "chat_conversation";

    void loadChatConversation();
    boolean onNewMessageFromUi(String message);
    void setChatConversation(ChatConversation chatConversation);
}

