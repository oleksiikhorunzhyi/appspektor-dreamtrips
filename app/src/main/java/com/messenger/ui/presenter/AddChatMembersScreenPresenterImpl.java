package com.messenger.ui.presenter;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.storege.utils.ConversationsDAO;
import com.messenger.storege.utils.ParticipantsDAO;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.activity.NewChatMembersActivity;
import com.messenger.ui.view.NewChatMembersScreen;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.worldventures.dreamtrips.R;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AddChatMembersScreenPresenterImpl extends BaseNewChatMembersScreenPresenter {

    private Conversation conversation;
    private List<User> originalParticipants;
    private ParticipantsDAO participantsDAO;

    public AddChatMembersScreenPresenterImpl(Activity activity) {
        super(activity);
        String conversationId = activity.getIntent()
                .getStringExtra(NewChatMembersActivity.EXTRA_CONVERSATION_ID);
        participantsDAO = new ParticipantsDAO(activity.getApplication());
        // TODO: 1/4/16 need UI refactoring, that use async loading
        conversation = ConversationsDAO.getConversationById(conversationId);
    }

    @Override
    public void attachView(NewChatMembersScreen view) {
        super.attachView(view);
        getView().setTitle(R.string.chat_add_new_members_title);
    }

    private void initExistingMembersSubscription() {
        participantsDAO.getParticipants(conversation.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindVisibility())
                .doOnNext(cursor -> originalParticipants = SqlUtils.convertToList(User.class, cursor))
                .subscribe(this::showContacts, throwable -> Timber.i(throwable, "Load participants error"));
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
