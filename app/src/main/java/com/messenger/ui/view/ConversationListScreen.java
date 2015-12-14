package com.messenger.ui.view;

import android.database.Cursor;

import com.messenger.model.ChatConversation;

import java.util.List;

public interface ConversationListScreen extends ActivityAwareScreen {
    void showLoading();
    void showContent();
    void showError(Throwable e);
    void showInputUserDialog();
    void showAllConversation(Cursor cursor);
    void showGroupConversation(Cursor cursor);
}
