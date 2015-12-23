package com.messenger.ui.view;

import android.database.Cursor;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;

import java.util.List;

public interface ChatScreen extends ActivityAwareScreen {
    void showLoading();
    void showContent();
    void showError(Throwable e);
    void setTitle(Conversation conversation, List<User> users);
    void onConversationCursorLoaded(Cursor cursor, Conversation conversation, boolean pendingScroll);
}
