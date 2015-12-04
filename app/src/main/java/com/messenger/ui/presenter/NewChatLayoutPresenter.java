package com.messenger.ui.presenter;

import java.util.List;

import com.messenger.model.ChatUser;
import com.messenger.ui.view.NewChatScreen;

public interface NewChatLayoutPresenter extends ActivityAwareViewStateMvpPresenter<NewChatScreen> {
    void connect();
    void loadChatContacts();
    void onSelectedUsersStateChanged(List<ChatUser> selectedUsers);
    void onHandleTakePictureIntent();
}
