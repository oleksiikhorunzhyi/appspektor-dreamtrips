package com.messenger.ui.view.edit_member;

import android.database.Cursor;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.view.layout.MessengerScreen;

public interface EditChatMembersScreen extends MessengerScreen {
    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setTitle(String title);

    void setAdapterWithInfo(User user, boolean isOwner);

    void setMembers(Cursor cursor);

    void setMembers(Cursor cursor, String query, String queryColumn);

    void showDeletionConfirmationDialog(User user);
}
