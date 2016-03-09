package com.messenger.ui.view.edit_member;

import android.database.Cursor;

import com.messenger.entities.DataUser;
import com.messenger.ui.view.layout.MessengerScreen;

import java.util.List;

public interface EditChatMembersScreen extends MessengerScreen {

    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setTitle(String title);

    void setAdapterWithInfo(DataUser user, boolean isOwner);

    void setMembers(DataUser admin, List usersWithHeaders, String query);

    void invalidateAllSwipedLayouts();

    void showDeletionConfirmationDialog(DataUser user);
}
