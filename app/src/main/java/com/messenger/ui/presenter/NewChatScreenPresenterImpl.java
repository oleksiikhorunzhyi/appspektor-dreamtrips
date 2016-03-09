package com.messenger.ui.presenter;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import com.messenger.delegate.StartChatDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.view.add_member.ChatMembersScreen;
import com.messenger.ui.view.chat.ChatPath;
import com.worldventures.dreamtrips.R;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import rx.functions.Action1;

public class NewChatScreenPresenterImpl extends ChatMembersScreenPresenterImpl {

    @Inject
    UsersDAO usersDAO;
    @Inject
    StartChatDelegate startChatDelegate;

    public NewChatScreenPresenterImpl(Context context) {
        super(context);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public void attachView(ChatMembersScreen view) {
        super.attachView(view);
        getView().setTitle(R.string.new_chat_title);
        contactUsers();
    }

    private void contactUsers() {
        cursorObservable = usersDAO.getFriends(user.getId())
                .compose(bindViewIoToMainComposer());

        connectToContactsCursor();
    }

    @Override
    public void onSelectedUsersStateChanged(List<DataUser> selectedContacts) {
        super.onSelectedUsersStateChanged(selectedContacts);
        if (selectedContacts.size() <= 1) {
            slideOutConversationNameEditText();
        } else {
            slideInConversationNameEditText();
        }
    }

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                List<DataUser> selectedUsers = getViewState().getSelectedContacts();

                if (selectedUsers == null || selectedUsers.isEmpty()) {
                    Toast.makeText(getContext(), R.string.new_chat_toast_no_users_selected_error,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (!isConnectionPresent() && selectedUsers.size() != 1) {
                    showAbsentConnectionMessage(getContext());
                    return true;
                }

                Action1<DataConversation> action1 = conversation -> {
                    History.Builder history = Flow.get(getContext()).getHistory().buildUpon();
                    history.pop();
                    history.push(new ChatPath(conversation.getId()));
                    Flow.get(getContext()).setHistory(history.build(), Flow.Direction.FORWARD);
                };

                if (selectedUsers.size() == 1) {
                    startChatDelegate.startSingleChat(selectedUsers.get(0), action1);
                } else {
                    startChatDelegate.startNewGroupChat(user.getId(), selectedUsers, getView().getConversationName(), action1);
                }

                return true;
        }
        return false;
    }

}
