package com.messenger.ui.presenter;

import com.messenger.entities.DataUser;
import com.messenger.ui.model.SelectableDataUser;
import com.messenger.ui.view.add_member.ChatMembersScreen;
import com.messenger.ui.viewstate.ChatMembersScreenViewState;

import java.util.List;

public interface ChatMembersScreenPresenter
        extends MessengerPresenter<ChatMembersScreen, ChatMembersScreenViewState> {

    void openUserProfile(DataUser user);

    void onItemSelectChange(SelectableDataUser item);
}
