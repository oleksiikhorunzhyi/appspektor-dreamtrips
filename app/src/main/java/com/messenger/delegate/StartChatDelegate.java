package com.messenger.delegate;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.converter.UserConverter;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.Participant;
import com.messenger.entities.DataParticipant;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.dreamtrips.modules.common.model.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class StartChatDelegate {

    private final UsersDAO usersDAO;
    private final ParticipantsDAO participantsDAO;
    private final ConversationsDAO conversationsDAO;
    private final ChatDelegate chatDelegate;

    public StartChatDelegate(UsersDAO usersDAO, ParticipantsDAO participantsDAO, ConversationsDAO conversationsDAO,
                             ChatDelegate chatDelegate) {
        this.usersDAO = usersDAO;
        this.participantsDAO = participantsDAO;
        this.conversationsDAO = conversationsDAO;
        this.chatDelegate = chatDelegate;
    }

    public void startSingleChat(User user, @NotNull Action1<DataConversation> crossingAction) {
        if (user.getUsername() == null) return;

        usersDAO.getUserById(user.getUsername())
                .subscribeOn(Schedulers.io())
                .first()
                .map(participant -> {
                    if (participant == null) {
                        participant = UserConverter.convert(user);
                        usersDAO.save(Collections.singletonList(participant));
                    }
                    return participant;
                })
                .flatMap(this::startSingleChatObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(crossingAction, throwable -> Timber.e(throwable, "Error"));
    }

    public void startSingleChat(DataUser user, @NotNull Action1<DataConversation> crossingAction) {
        startSingleChatObservable(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(crossingAction, throwable -> Timber.e(throwable, "Error"));
    }

    private Observable<DataConversation> startSingleChatObservable(DataUser participant) {
        DataConversation conversation = chatDelegate.getExistingSingleConversation(participant.getId());
        if (conversation != null) return Observable.just(conversation);

        return chatDelegate.createNewConversation(Collections.singletonList(participant), "")
                .doOnNext(dataConversation -> {
                    //there is no owners in single chat
                    DataParticipant relationship = new DataParticipant(dataConversation.getId(), participant.getId(), Participant.Affiliation.MEMBER);

                    participantsDAO.save(Collections.singletonList(relationship));
                    conversationsDAO.save(Collections.singletonList(dataConversation));
                });
    }

    public void startNewGroupChat(String ownerId,
                                  List<DataUser> participant,
                                  @Nullable String subject, @NotNull Action1<DataConversation> crossingAction) {
        chatDelegate.createNewConversation(participant, subject)
                .subscribeOn(Schedulers.io())
                .doOnNext(conversation -> {
                    conversation.setOwnerId(ownerId);
                    List<DataParticipant> relationships = Queryable.from(participant).map(user ->
                            new DataParticipant(conversation.getId(), user.getId(), Participant.Affiliation.MEMBER)).toList();
                    // we are participants too and if conversation is group then we're owner otherwise we're member
                    relationships.add(new DataParticipant(conversation.getId(), ownerId, Participant.Affiliation.OWNER));

                    participantsDAO.save(relationships);
                    conversationsDAO.save(Collections.singletonList(conversation));
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(crossingAction, throwable -> Timber.d(throwable, "Error"));
    }

}
