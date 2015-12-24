package com.messenger.ui.view;

import android.database.Cursor;
import android.support.annotation.StringRes;

import com.messenger.messengerservers.entities.User;

import java.util.List;

public interface EditChatMembersScreen extends ActivityAwareScreen {
    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setTitle(String title);

    void setTitle(@StringRes int title);

    void setMembers(Cursor cursor);

    void setMembers(Cursor cursor, String query, String queryColumn);

    void showDeletionConfirmationDialog(User user);
}
