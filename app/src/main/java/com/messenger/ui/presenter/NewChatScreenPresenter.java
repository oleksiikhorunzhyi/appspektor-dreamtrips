package com.messenger.ui.presenter;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.view.NewChatMembersScreen;
import com.messenger.ui.viewstate.NewChatLayoutViewState;

import java.util.List;

public interface NewChatScreenPresenter extends MessengerPresenter<NewChatMembersScreen,
        NewChatLayoutViewState> {
    void onSelectedUsersStateChanged(List<User> selectedUsers);

    void onTextChangedInChosenContactsEditText(String text);

    void openUserProfile(User user);
}
