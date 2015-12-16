package com.messenger.ui.view;

import android.database.Cursor;

import com.messenger.model.ChatConversation;

public interface ChatScreen extends ActivityAwareScreen {
    void showLoading();
    void showContent();
    void showError(Throwable e);
    void setSubject(String subject);
    void onConversationCursorLoaded(Cursor cursor);
}
