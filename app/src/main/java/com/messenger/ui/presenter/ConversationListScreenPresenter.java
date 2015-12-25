package com.messenger.ui.presenter;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.view.ConversationListScreen;
import com.messenger.ui.viewstate.ConversationListViewState;

public interface ConversationListScreenPresenter extends ActivityAwareViewStateMvpPresenter<ConversationListScreen,
        ConversationListViewState> {
    User getUser();

    void onConversationSelected(Conversation conversation);

    void onDeleteButtonPressed(Conversation conversation);

    void onDeletionConfirmed(Conversation conversation);

    void onMoreOptionsButtonPressed(Conversation conversation);

    void onMarkAsUnreadButtonPressed(Conversation conversation);

    void onTurnOffNotificationsButtonPressed(Conversation conversation);

    void onConversationsDropdownSelected(boolean showOnlyGroupConversations);

    void onConversationsSearchFilterSelected(String searchFilter);
}

