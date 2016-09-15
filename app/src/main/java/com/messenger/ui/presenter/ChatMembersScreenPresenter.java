package com.messenger.ui.presenter;

import com.messenger.ui.model.SelectableDataUser;
import com.messenger.ui.view.add_member.ChatMembersScreen;
import com.messenger.ui.viewstate.ChatMembersScreenViewState;

public interface ChatMembersScreenPresenter extends MessengerPresenter<ChatMembersScreen, ChatMembersScreenViewState> {

   void onItemSelectChange(SelectableDataUser item);
}
