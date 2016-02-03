package com.messenger.ui.view.edit_member;

import android.database.Cursor;

import com.messenger.entities.DataUser;
import com.messenger.ui.view.layout.MessengerScreen;

public interface EditChatMembersScreen extends MessengerScreen {

    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setTitle(String title);

    void setAdapterWithInfo(DataUser user, boolean isOwner);

    void setMembers(Cursor cursor);

    void setMembers(Cursor cursor, String query, String queryColumn);

    void invalidateAllSwipedLayouts();

    void showDeletionConfirmationDialog(DataUser user);
}
