package com.messenger.ui.presenter;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.activity.NewChatMembersActivity;
import com.messenger.ui.view.NewChatMembersScreen;
import com.messenger.util.RxContentResolver;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.worldventures.dreamtrips.R;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AddChatMembersScreenPresenterImpl extends BaseNewChatMembersScreenPresenter {

    private Conversation conversation;
    private List<User> originalParticipants;

    private Subscription participantsSubscriber;

    public AddChatMembersScreenPresenterImpl(Activity activity) {
        super(activity);
        String conversationId = activity.getIntent()
                .getStringExtra(NewChatMembersActivity.EXTRA_CONVERSATION_ID);
        conversation = new Select()
                .from(Conversation.class)
                .byIds(conversationId)
                .querySingle();
    }

    @Override
    public void attachView(NewChatMembersScreen view) {
        super.attachView(view);
        getView().setTitle(R.string.chat_add_new_members_title);
    }

    private void initExistingMembersSubscription() {
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
                .subscribe(users -> {
                    originalParticipants = users;
                    // init contacts subscription excluding existing participants
                    // and refresh UI
                    initPotentialMembersSubscription();
                }, throwable -> Timber.i(throwable, "DbFlow"));
    }

    private void initPotentialMembersSubscription() {
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
        String[] selectionArgs = idsToExclude.toArray(new String[idsToExclude.size()]);

        RxContentResolver.Query q = new RxContentResolver.Query.Builder(null)
                .withSelection("SELECT * FROM Users WHERE " + where.toString())
                .withSelectionArgs(selectionArgs)
                .withSortOrder("ORDER BY " + User.COLUMN_NAME + " COLLATE NOCASE ASC").build();
        contactSubscription = contentResolver.query(q, User.CONTENT_URI,
                ParticipantsRelationship.CONTENT_URI)
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> showContacts(users));
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

    @Override
    public void onVisibilityChanged(int visibility) {
        super.onVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            initExistingMembersSubscription();
        } else {
            contactSubscription.unsubscribe();
            participantsSubscriber.unsubscribe();
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

                Conversation newConversation = chatDelegate.modifyConversation(conversation, originalParticipants,
                        newChatUsers, getView().getConversationName());

                Queryable.from(newChatUsers).forEachR(u ->
                        new ParticipantsRelationship(newConversation.getId(), u).save());
                ContentUtils.insert(Conversation.CONTENT_URI, newConversation);

                ChatActivity.startChat(activity, newConversation);
                return true;
        }
        return false;
    }
}
