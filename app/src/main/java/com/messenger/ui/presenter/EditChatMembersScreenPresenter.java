package com.messenger.ui.presenter;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.view.EditChatMembersScreen;
import com.messenger.ui.viewstate.EditChatMembersViewState;

public interface EditChatMembersScreenPresenter extends ActivityAwareViewStateMvpPresenter<EditChatMembersScreen,
        EditChatMembersViewState> {
    void onDeleteUserFromChat(User user);
    void onSearchFilterSelected(String search);
}
