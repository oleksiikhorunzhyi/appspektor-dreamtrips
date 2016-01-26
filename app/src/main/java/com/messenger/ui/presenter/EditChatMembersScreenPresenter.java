package com.messenger.ui.presenter;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.view.edit_member.EditChatMembersScreen;
import com.messenger.ui.viewstate.EditChatMembersViewState;

public interface EditChatMembersScreenPresenter extends MessengerPresenter<EditChatMembersScreen,
        EditChatMembersViewState> {

    void onSearchFilterSelected(String search);

    void onDeleteUserFromChat(User user);

    void onDeleteUserFromChatConfirmed(User user);

    void onUserClicked(User user);

    void requireAdapterInfo();
}
