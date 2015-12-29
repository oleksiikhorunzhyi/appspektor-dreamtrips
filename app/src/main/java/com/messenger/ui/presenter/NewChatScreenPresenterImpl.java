package com.messenger.ui.presenter;

import android.app.Activity;
import android.view.MenuItem;
import android.widget.Toast;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.view.NewChatMembersScreen;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.worldventures.dreamtrips.R;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users WHERE " + User.COLUMN_ID + "<>?")
                .withSelectionArgs(new String[]{user.getId()})
                .withSortOrder("ORDER BY " + User.COLUMN_NAME + " COLLATE NOCASE ASC").build();
        contactSubscription = contentResolver.query(q, User.CONTENT_URI,
                ParticipantsRelationship.CONTENT_URI)
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindVisibility())
                .subscribe(users -> showContacts(users));
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

                Conversation conversation = chatDelegate.createNewConversation(selectedUsers, getView().getConversationName());
                //
                Queryable.from(selectedUsers).forEachR(u -> new ParticipantsRelationship(conversation.getId(), u).save());
                ContentUtils.insert(Conversation.CONTENT_URI, conversation);

                ChatActivity.startChat(activity, conversation);
                return true;
        }
        return false;
    }
}
