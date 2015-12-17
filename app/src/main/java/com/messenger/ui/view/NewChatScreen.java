package com.messenger.ui.view;

import android.database.Cursor;
import android.graphics.Bitmap;

import com.messenger.messengerservers.entities.User;
import com.messenger.model.ChatUser;

import java.util.List;

public interface NewChatScreen extends ActivityAwareScreen {
    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setContacts(Cursor cursor);

    void setSelectedContacts(List<User> selectedContacts);

    void setSelectedUsersHeaderText(CharSequence text);

    void setConversationIcon(Bitmap bitmap);

    String getConversationName();
}
