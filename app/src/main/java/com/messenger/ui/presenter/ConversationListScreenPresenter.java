package com.messenger.ui.presenter;

import com.messenger.model.ChatConversation;
import com.messenger.ui.view.ConversationListScreen;
import com.messenger.ui.viewstate.ConversationListViewState;

public interface ConversationListScreenPresenter extends ActivityAwareViewStateMvpPresenter<ConversationListScreen,
        ConversationListViewState> {
    void loadConversationList();
    void onConversationSelected(ChatConversation chatConversation);
}

