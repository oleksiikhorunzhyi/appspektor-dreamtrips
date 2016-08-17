package com.messenger.ui.view.conversation;

import android.database.Cursor;

import com.messenger.entities.DataConversation;
import com.messenger.ui.view.layout.MessengerScreen;

public interface ConversationListScreen extends MessengerScreen {
   void showLoading();
   void showContent();
   void showError(Throwable e);
   void setSelectedConversationId(String conversationId);
   void showConversations(Cursor cursor);
   void showConversationDeletionConfirmationDialog(DataConversation conversation);
   void showConversationMoreActionsDialog(DataConversation conversation);
}
