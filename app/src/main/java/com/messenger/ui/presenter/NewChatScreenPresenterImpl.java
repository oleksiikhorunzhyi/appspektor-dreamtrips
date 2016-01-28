package com.messenger.ui.presenter;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Participant;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.view.add_member.ChatMembersScreen;
import com.messenger.ui.view.chat.ChatPath;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.worldventures.dreamtrips.R;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;

public class NewChatScreenPresenterImpl extends ChatMembersScreenPresenterImpl {

    @Inject
    UsersDAO usersDAO;

    private final ConversationHelper conversationHelper;

    public NewChatScreenPresenterImpl(Context context) {
        super(context);
        conversationHelper = new ConversationHelper();
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
        cursorObservable = usersDAO.getFriends()
                .compose(bindViewIoToMainComposer());

        cursorObservable.subscribe(this::showContacts);
    }

    @Override
    public void onSelectedUsersStateChanged(List<User> selectedContacts) {
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
                List<User> selectedUsers = getViewState().getSelectedContacts();

                if (selectedUsers == null || selectedUsers.isEmpty()) {
                    Toast.makeText(getContext(), R.string.new_chat_toast_no_users_selected_error,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (!isConnectionPresent() && selectedUsers.size() != 1) {
                    showAbsentConnectionMessage(getContext());
                    return true;
                }

                Conversation conversation = chatDelegate.createNewConversation(selectedUsers, getView().getConversationName());
                // we are participants too and if conversation is group then we're owner otherwise we're member
                if (!conversationHelper.isGroup(conversation)) {
                    new ParticipantsRelationship(conversation.getId(), user, Participant.Affiliation.MEMBER).save();
                } else {
                    new ParticipantsRelationship(conversation.getId(), user, Participant.Affiliation.OWNER).save();
                    conversation.setOwnerId(user.getId());
                }

                Queryable.from(selectedUsers).forEachR(u -> new ParticipantsRelationship(conversation.getId(), u, Participant.Affiliation.MEMBER).save());
                ContentUtils.insert(Conversation.CONTENT_URI, conversation);
                //
                History.Builder history = Flow.get(getContext()).getHistory().buildUpon();
                history.pop();
                history.push(new ChatPath(conversation.getId()));
                Flow.get(getContext()).setHistory(history.build(), Flow.Direction.FORWARD);
                return true;
        }
        return false;
    }
}
