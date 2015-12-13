package com.messenger.ui.view;

import android.graphics.Bitmap;

import com.messenger.model.ChatUser;

import java.util.List;

public interface NewChatScreen extends ActivityAwareScreen {
    void showLoading();
    void showContent();
    void showError(Throwable e);
    void setContacts(List<ChatUser> chatContacts);
    void setSelectedContacts(List<ChatUser> selectedContacts);
    void setSelectedUsersHeaderText(CharSequence text);
    void setConversationIcon(Bitmap bitmap);
    String getConversationName();
}
