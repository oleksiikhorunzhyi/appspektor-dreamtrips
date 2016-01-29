package com.messenger.ui.view.add_member;

import android.database.Cursor;
import android.support.annotation.StringRes;

import com.messenger.entities.User;
import com.messenger.ui.view.layout.MessengerScreen;

import java.util.List;

public interface ChatMembersScreen extends MessengerScreen {
    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setTitle(String title);

    void setTitle(@StringRes int title);

    void setContacts(Cursor cursor);

    void setContacts(Cursor cursor, String query, String queryColumn);

    void setSelectedContacts(List<User> selectedContacts);

    void setSelectedUsersHeaderText(CharSequence text);

    void setConversationNameEditTextVisibility(int visibility);

    void slideInConversationNameEditText();

    void slideOutConversationNameEditText();

    String getConversationName();
}
