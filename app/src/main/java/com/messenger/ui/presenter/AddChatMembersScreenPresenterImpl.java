package com.messenger.ui.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Toast;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.model.Participant;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.model.SelectableDataUser;
import com.messenger.ui.view.add_member.ChatMembersScreen;
import com.messenger.ui.view.chat.ChatPath;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AddChatMembersScreenPresenterImpl extends ChatMembersScreenPresenterImpl {

    private static final int REQUIRED_SELECTED_USERS_FOR_SINGLE_CHAT_TO_SHOW_CHAT_NAME = 1;

    @Inject
    ConversationsDAO conversationsDAO;
    @Inject
    ParticipantsDAO participantsDAO;

    private String conversationId;
    private Observable<DataConversation> conversationStream;

    public AddChatMembersScreenPresenterImpl(Context context, Injector injector, String conversationId) {
        super(context, injector);
        this.conversationId = conversationId;
        conversationStream = conversationsDAO.getConversation(conversationId).first().replay().autoConnect();
    }

    @Override
    public void attachView(ChatMembersScreen view) {
        super.attachView(view);
        getView().setTitle(R.string.chat_add_new_members_title);
    }

    @Override
    protected Observable<List<DataUser>> createContactListObservable() {
        return participantsDAO
                .getNewParticipantsCandidates(conversationId)
                .subscribeOn(Schedulers.io());
    }

    private void tryCreateChat(MenuItem doneButtonItem) {
        List<DataUser> newChatUsers = selectedUsers;
        if (newChatUsers == null || newChatUsers.isEmpty()) {
            Toast.makeText(getContext(), R.string.new_chat_toast_no_users_selected_error,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isConnectionPresent()) {
            showAbsentConnectionMessage(getContext());
            return;
        }

        doneButtonItem.setEnabled(false);

        // TODO: 1/28/16 improve logic with getViewState().getSelectedContacts();
        conversationStream
                .flatMap(conversation -> modifyConversation(conversation, newChatUsers, getView().getConversationName()))
                        .doOnNext(conversationPair -> saveModifiedConversation(conversationPair.first, newChatUsers, conversationPair.second))
                        .compose(bindViewIoToMainComposer())
                        .map(conversationPair -> conversationPair.first)
                        .subscribe(newConversation -> {
                            History.Builder history = Flow.get(getContext()).getHistory().buildUpon();
                            history.pop();
                            history.pop();
                            history.push(new ChatPath(newConversation.getId()));
                            Flow.get(getContext()).setHistory(history.build(), Flow.Direction.FORWARD);
                        }, e -> {
                            doneButtonItem.setEnabled(true);
                            Timber.e(e, "Could not add chat member");
                        });
    }

    private Observable<Pair<DataConversation, String>> modifyConversation (DataConversation conversation, List<DataUser> newChatUsers, String newSubject) {
        return participantsDAO.getParticipantsEntities(conversation.getId())
                .take(1)
                .flatMap(currentUsers ->
                                chatDelegate.modifyConversation(conversation, currentUsers, newChatUsers, newSubject)
                                        .map(newConversation -> new Pair<>(newConversation, conversation.getType()))
                );
    }

    private void saveModifiedConversation(DataConversation newConversation,  List<DataUser> newChatUsers, String previousType) {
        List<DataParticipant> relationships = Queryable.from(newChatUsers).map(user ->
                        new DataParticipant(newConversation.getId(), user.getId(), Participant.Affiliation.MEMBER))
                .toList();

        // we are participants too and if conversation is group then we're owner otherwise we're member
        if (TextUtils.equals(previousType, ConversationType.CHAT)) {
            relationships.add(new DataParticipant(newConversation.getId(), user.getId(), Participant.Affiliation.OWNER));
        }

        participantsDAO.save(relationships);
        conversationsDAO.save(newConversation);
    }
    ///////////////////////////////////////////////////////////////////////////
    // Menu
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                tryCreateChat(item);
                return true;
        }
        return false;
    }

    @Override
    public void onItemSelectChange(SelectableDataUser item) {
        super.onItemSelectChange(item);
        conversationStream.take(1).compose(bindViewIoToMainComposer()).subscribe(conversation -> {
            boolean isSingleChat = ConversationHelper.isSingleChat(conversation);
            setConversationNameInputFieldVisible(isSingleChat &&
                    selectedUsers.size() >= REQUIRED_SELECTED_USERS_FOR_SINGLE_CHAT_TO_SHOW_CHAT_NAME);
        });
    }
}
