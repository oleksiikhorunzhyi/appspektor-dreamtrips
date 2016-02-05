package com.messenger.ui.presenter;

import android.content.Context;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Toast;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.Participant;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.view.add_member.ChatMembersScreen;
import com.messenger.ui.view.chat.ChatPath;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.worldventures.dreamtrips.R;

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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
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
        List<String> newChatUserIds = Queryable.from(newChatUsers).map(u -> u.getId()).toList();
        conversationStream
                .flatMap(conversation -> participantsDAO
                        .getParticipants(conversation.getId()).first()
                        .map(cursor -> Queryable.from(SqlUtils.convertToList(DataUser.class, cursor)).map(DataUser::getId).toList())
                        .map(currentUsers -> {
                            DataConversation newConversation = chatDelegate.modifyConversation(
                                    conversation, currentUsers, newChatUserIds, getView().getConversationName()
                            );
                            Queryable.from(newChatUsers).forEachR(u ->
                                            new DataParticipant(newConversation.getId(), u.getId(), Participant.Affiliation.MEMBER).save()
                            );
                            ContentUtils.insert(DataConversation.CONTENT_URI, newConversation);
                            return newConversation;
                        }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
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
