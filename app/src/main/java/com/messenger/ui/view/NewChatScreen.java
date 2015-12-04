package com.messenger.ui.view;

import android.graphics.Bitmap;

import java.util.List;

import com.messenger.model.ChatContacts;
import com.messenger.model.ChatUser;

public interface NewChatScreen extends ActivityAwareScreen {
    void showLoading();

    void showContent();

    void showError(Throwable e);

    void setContacts(ChatContacts chatContacts);

    void setSelectedContacts(List<ChatUser> selectedContacts);

    void setSelectedUsersHeaderText(CharSequence text);

    void setConversationIcon(Bitmap bitmap);

    String getConversationName();
}
