package com.messenger.ui.presenter;

import android.content.Context;
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
import com.messenger.ui.view.add_member.ChatMembersScreen;
import com.messenger.ui.view.chat.ChatPath;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.messenger.messengerservers.constant.ConversationType.CHAT;

public class AddChatMembersScreenPresenterImpl extends ChatMembersScreenPresenterImpl {

    @Inject
    ConversationsDAO conversationsDAO;
    @Inject
    ParticipantsDAO participantsDAO;

    private String conversationId;
    private Observable<DataConversation> conversationStream;
    private PublishSubject<List<DataUser>> selectedStream;

    public AddChatMembersScreenPresenterImpl(Context context, String conversationId) {
        super(context);
        this.conversationId = conversationId;
        conversationStream = conversationsDAO.getConversation(conversationId).first().replay().autoConnect();
        selectedStream = PublishSubject.create();
    }

    @Override
    public void attachView(ChatMembersScreen view) {
        super.attachView(view);
        getView().setTitle(R.string.chat_add_new_members_title);
        connectToCandidates();
        connectSelectedCandidates();
    }

    private void connectToCandidates() {
        cursorObservable = participantsDAO
                .getNewParticipantsCandidates(conversationId)
                .compose(bindViewIoToMainComposer())
                .replay(1)
                .autoConnect();

        cursorObservable.subscribe(this::showContacts);
    }

    private void connectSelectedCandidates() {
        Observable.combineLatest(
                conversationStream,
                selectedStream.asObservable(),
                (conversation, users) -> new Pair<>(conversation, users)
        )
                .compose(bindViewIoToMainComposer())
                .subscribe(pair -> {
                    // show conversation name edit text only for single chats that will turn to become group chats
                    if (!pair.first.getType().equals(CHAT)) return;
                    if (pair.second.isEmpty()) {
                        slideOutConversationNameEditText();
                    } else {
                        slideInConversationNameEditText();
                    }
                });
    }

    @Override
    public void onSelectedUsersStateChanged(List<DataUser> selectedContacts) {
        super.onSelectedUsersStateChanged(selectedContacts);
        selectedStream.onNext(selectedContacts);
    }

    private void tryCreateChat() {
        List<DataUser> newChatUsers = getViewState().getSelectedContacts();
        if (newChatUsers == null || newChatUsers.isEmpty()) {
            Toast.makeText(getContext(), R.string.new_chat_toast_no_users_selected_error,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isConnectionPresent()) {
            showAbsentConnectionMessage(getContext());
            return;
        }
        // TODO: 1/28/16 improve logic with getViewState().getSelectedContacts();
        conversationStream
                .flatMap(conversation -> participantsDAO
                        .getParticipantsEntities(conversation.getId())
                        .first()
                        .flatMap(currentUsers -> chatDelegate.modifyConversation(
                                conversation, currentUsers, newChatUsers, getView().getConversationName())))
                .doOnNext(newConversation -> {
                    List<DataParticipant> relationships =
                            Queryable.from(newChatUsers).map(user ->
                                    new DataParticipant(newConversation.getId(), user.getId(), Participant.Affiliation.MEMBER))
                                    .toList();
                    // we are participants too and if conversation is group then we're owner otherwise we're member
                    if (newConversation.getType().equals(ConversationType.CHAT)) {
                        relationships.add(new DataParticipant(newConversation.getId(), user.getId(), Participant.Affiliation.OWNER));
                    }

                    participantsDAO.save(relationships);
                    conversationsDAO.save(newConversation);
                })
                .compose(bindViewIoToMainComposer())
                .subscribe(newConversation -> {
                    History.Builder history = Flow.get(getContext()).getHistory().buildUpon();
                    history.pop();
                    history.pop();
                    history.push(new ChatPath(newConversation.getId()));
                    Flow.get(getContext()).setHistory(history.build(), Flow.Direction.FORWARD);
                });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Menu
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onToolbarMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                tryCreateChat();
                return true;
        }
        return false;
    }
}
