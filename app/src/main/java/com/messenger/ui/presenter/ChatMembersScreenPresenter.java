package com.messenger.ui.presenter;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.view.ChatMembersScreen;
import com.messenger.ui.viewstate.NewChatLayoutViewState;

import java.util.List;

public interface ChatMembersScreenPresenter extends MessengerPresenter<ChatMembersScreen,
        NewChatLayoutViewState> {
    void onSelectedUsersStateChanged(List<User> selectedUsers);

    void onTextChangedInChosenContactsEditText(String text);

    void openUserProfile(User user);
}
