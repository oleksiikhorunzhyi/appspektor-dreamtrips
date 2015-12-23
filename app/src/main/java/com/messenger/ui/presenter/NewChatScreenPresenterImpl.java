package com.messenger.ui.presenter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.view.NewChatMembersScreen;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.worldventures.dreamtrips.R;

import java.util.List;
import java.util.UUID;

public class NewChatScreenPresenterImpl extends BaseNewChatMembersScreenPresenter {

    public NewChatScreenPresenterImpl(Activity activity) {
        super(activity);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public void attachView(NewChatMembersScreen view) {
        super.attachView(view);
        getView().setTitle(R.string.new_chat_title);
    }

    @Override
    public void loadChatContacts() {
        super.loadChatContacts();
        initContactsLoaders();
    }

    @Override
    public void onSelectedUsersStateChanged(List<User> selectedContacts) {
        super.onSelectedUsersStateChanged(selectedContacts);
        if (selectedContacts.size() <= 1) {
            getView().slideOutConversationNameEditText();
        } else {
            getView().slideInConversationNameEditText();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                List<User> selectedUsers = getViewState().getSelectedContacts();

                if (selectedUsers == null || selectedUsers.isEmpty()) {
                    Toast.makeText(activity, R.string.new_chat_toast_no_users_selected_error,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                Conversation conversation;
                if (selectedUsers.size() == 1) {
                    conversation = new Conversation.Builder()
                            .type(Conversation.Type.CHAT)
                            .id(ThreadCreatorHelper.obtainThreadSingleChat(user, selectedUsers.get(0)))
                            .build();
                } else {
                    conversation = new Conversation.Builder()
                            .type(Conversation.Type.GROUP)
                            .id(UUID.randomUUID().toString())
                            .build();
                }
                //
                Queryable.from(selectedUsers).forEachR(u -> new ParticipantsRelationship(conversation.getId(), u).save());
                ContentUtils.insert(Conversation.CONTENT_URI, conversation);
                //
                if (selectedUsers.size() == 1) {
                    ChatActivity.startSingleChat(activity, conversation.getId());
                } else {
                    inviteUsersToGroupChat(conversation, selectedUsers);
                    ChatActivity.startGroupChat(activity, conversation.getId());
                }
                activity.finish();
                return true;
        }
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(activity, User.CONTENT_URI,
                null, User.COLUMN_ID + "<>?", new String[]{user.getId()},
                User.COLUMN_NAME + " COLLATE NOCASE ASC");
    }
}
