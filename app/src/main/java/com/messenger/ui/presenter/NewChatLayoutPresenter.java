package com.messenger.ui.presenter;

import com.messenger.model.ChatUser;
import com.messenger.ui.view.NewChatScreen;
import com.messenger.ui.viewstate.NewChatLayoutViewState;

import java.util.List;

public interface NewChatLayoutPresenter extends ActivityAwareViewStateMvpPresenter<NewChatScreen,
        NewChatLayoutViewState> {
    void loadChatContacts();
    void onSelectedUsersStateChanged(List<ChatUser> selectedUsers);
    void onHandleTakePictureIntent();
}
