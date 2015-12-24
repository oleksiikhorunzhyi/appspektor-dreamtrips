package com.messenger.ui.presenter;

import android.app.Activity;
import android.content.Intent;
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
import com.messenger.ui.activity.NewChatMembersActivity;
import com.messenger.ui.view.NewChatMembersScreen;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddChatMembersScreenPresenterImpl extends BaseNewChatMembersScreenPresenter {

    private Conversation conversation;
    private List<User> originalParticipants;

    private RxContentResolver contentResolver;
    private Subscription participantsSubscriber;

    public AddChatMembersScreenPresenterImpl(Activity activity) {
        super(activity);
        String conversationId = activity.getIntent()
                .getStringExtra(NewChatMembersActivity.EXTRA_CONVERSATION_ID);
        conversation = new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();

        contentResolver = new RxContentResolver(activity.getContentResolver(), query -> {
            return FlowManager.getDatabaseForTable(User.class).getWritableDatabase()
                    .rawQuery(query.selection, query.selectionArgs);
        });
    }

    @Override
    public void attachView(NewChatMembersScreen view) {
        super.attachView(view);
        getView().setTitle(R.string.chat_add_new_members_title);
        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users u " +
                                "JOIN ParticipantsRelationship p " +
                                "ON p.userId = u._id " +
                                "WHERE p.conversationId = ?"
                ).withSelectionArgs(new String[]{conversation.getId()}).build();
        participantsSubscriber = contentResolver.query(q, User.CONTENT_URI,
                ParticipantsRelationship.CONTENT_URI)
                .throttleLast(500, TimeUnit.MILLISECONDS)
                .map(c -> SqlUtils.convertToList(User.class, c))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bindView((View) getView()))
                .subscribe(users -> {
                    originalParticipants = users;
                    initContactsLoaders();
                });
    }

    @Override
    public void onSelectedUsersStateChanged(List<User> selectedContacts) {
        super.onSelectedUsersStateChanged(selectedContacts);
        // show conversation name edit text only for single chats that will turn to become
        // group chats
        if (conversation.getType().equals(Conversation.Type.CHAT)) {
            if (selectedContacts.size() < 1) {
                getView().slideOutConversationNameEditText();
            } else {
                getView().slideInConversationNameEditText();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                List<User> newChatUsers = getViewState().getSelectedContacts();
                if (newChatUsers == null || newChatUsers.isEmpty()) {
                    Toast.makeText(activity, R.string.new_chat_toast_no_users_selected_error,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                // in case it was single chat we must delete old conversion first
                // and create new one
                if (conversation.getType().equals(Conversation.Type.CHAT)) {
                    conversation = new Conversation.Builder()
                            .ownerId(user.getId())
                            .type(Conversation.Type.GROUP)
                            .id(UUID.randomUUID().toString())
                            .build();
                    // since we create new group chat
                    // make sure to invite original participant (addressee) from old single chat
                    newChatUsers.addAll(originalParticipants);
                    newChatUsers.add(user);
                }
                Queryable.from(newChatUsers).forEachR(u ->
                        new ParticipantsRelationship(conversation.getId(), u).save());
                ContentUtils.insert(Conversation.CONTENT_URI, conversation);
                //noinspection all
                saveChatModifications(conversation, newChatUsers, getView().getConversationName());

                Intent data = new Intent();
                data.putExtra(NewChatMembersActivity.EXTRA_CONVERSATION_ID, conversation.getId());
                activity.setResult(Activity.RESULT_OK, data);
                activity.finish();
                return true;
        }
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Filter current user from contacts list and all participants
        // Since currentuser is missing currently in single chat participants and not missing
        // in group chat participants apply workoaround.
        HashSet<String> idsToExclude = new HashSet<>();
        idsToExclude.add(user.getId());
        for (User user : originalParticipants) {
            idsToExclude.add(user.getId());
        }
        StringBuilder where = new StringBuilder();
        for (int i = 0; i < idsToExclude.size(); i++) {
            where.append(User.COLUMN_ID + "<>?");
            if (i != idsToExclude.size() - 1) {
                where.append(" AND ");
            }
        }
        return new CursorLoader(activity, User.CONTENT_URI,
                null, where.toString(), idsToExclude.toArray(new String[idsToExclude.size()]),
                User.COLUMN_NAME + " COLLATE NOCASE ASC");
    }
}
