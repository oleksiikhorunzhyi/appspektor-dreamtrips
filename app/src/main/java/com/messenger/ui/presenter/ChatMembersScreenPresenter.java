package com.messenger.ui.presenter;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.view.add_member.ChatMembersScreen;
import com.messenger.ui.viewstate.ChatMembersScreenViewState;

import java.util.List;

public interface ChatMembersScreenPresenter extends MessengerPresenter<ChatMembersScreen,
        ChatMembersScreenViewState> {
    void onSelectedUsersStateChanged(List<User> selectedUsers);

    void onTextChangedInChosenContactsEditText(String text);

    void openUserProfile(User user);
}
