package com.messenger.ui.presenter;

import com.messenger.entities.DataUser;
import com.messenger.ui.view.edit_member.EditChatMembersScreen;
import com.messenger.ui.viewstate.EditChatMembersViewState;

public interface EditChatMembersScreenPresenter extends MessengerPresenter<EditChatMembersScreen, EditChatMembersViewState> {

   void onDeleteUserFromChat(DataUser user);

   void onDeleteUserFromChatConfirmed(DataUser user);

   void onUserClicked(DataUser user);
}
