package com.messenger.ui.presenter;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.view.NewChatScreen;
import com.messenger.ui.viewstate.NewChatLayoutViewState;

import java.util.List;

public interface NewChatLayoutPresenter extends ActivityAwareViewStateMvpPresenter<NewChatScreen,
        NewChatLayoutViewState> {
    void onSelectedUsersStateChanged(List<User> selectedUsers);
    void onTextChangedInChosenContactsEditText(String text);
}
