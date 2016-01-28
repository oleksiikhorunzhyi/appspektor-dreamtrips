package com.messenger.ui.view.conversation;

import android.database.Cursor;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.ui.view.layout.MessengerScreen;

public interface ConversationListScreen extends MessengerScreen {
    void showLoading();
    void showContent();
    void showError(Throwable e);
    void setSelectedConversationId(String conversationId);
    void showConversations(Cursor cursor);
    void showConversations(Cursor cursor, String searchFilter);
    void showConversationDeletionConfirmationDialog(Conversation conversation);
    void showConversationMoreActionsDialog(Conversation conversation);
}
