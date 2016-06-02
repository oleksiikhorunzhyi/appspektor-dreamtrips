package com.messenger.delegate;


import android.text.TextUtils;

import com.messenger.delegate.conversation.LoadConversationDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataParticipant;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.constant.Affiliation;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.Collections;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class GroupChatEventDelegate {

    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    ConversationsDAO conversationsDAO;
    @Inject
    ParticipantsDAO participantsDAO;
    @Inject
    UsersDAO usersDAO;
    @Inject
    SessionHolder<UserSession> currentUserSession;
    @Inject
    LoadConversationDelegate loadConversationDelegate;

    @Inject
    public GroupChatEventDelegate(@ForApplication Injector injector) {
        injector.inject(this);
    }

    public void onChatInvited(String conversationId) {
        if (currentUserSession.get() == null || currentUserSession.get().get() == null
                || !currentUserSession.get().isPresent()) return;

        loadConversationDelegate.loadConversationFromNetwork(conversationId);
    }

    public void onSubjectChanged(String conversationId, String subject){
        conversationsDAO.getConversation(conversationId).first()
                .subscribeOn(Schedulers.io())
                .filter(c -> c != null && !TextUtils.equals(c.getSubject(), subject))
                .subscribe(conversation -> {
                    conversation.setSubject(subject);
                    conversationsDAO.save(conversation);
                }, throwable -> Timber.d(throwable, ""));
    }

    public void onAvatarChanged(String conversationId, String avatar) {
        conversationsDAO.getConversation(conversationId).take(1)
                .subscribeOn(Schedulers.io())
                .filter(c -> c != null && !TextUtils.equals(c.getAvatar(), avatar))
                .subscribe(conversation -> {
                    conversation.setAvatar(avatar);
                    conversationsDAO.save(conversation);
                }, e -> Timber.d(e, "Could not save avatar to conversation"));
    }

    public void onChatLeft(String conversationId, String userId) {
        Timber.i("User left :: chat=%s , user=%s", conversationId, userId);

        handleRemovingMember(conversationId, userId, ConversationStatus.LEFT);
    }

    public void onKicked(String conversationId, String userId) {
        Timber.i("User kicked :: chat=%s , user=%s", conversationId, userId);

        handleRemovingMember(conversationId, userId, ConversationStatus.KICKED);
    }

    private void handleRemovingMember(String conversationId, String userId, @ConversationStatus.Status String status) {
        Observable.fromCallable(() -> removeFromConversation(conversationId, userId))
                .subscribeOn(Schedulers.io())
                .flatMap(participant -> {
                    if (TextUtils.equals(messengerServerFacade.getUsername(), participant.getUserId())) {
                        return setConversationStatus(conversationId, status);
                    } else {
                        return Observable.empty();
                    }
                })
                .subscribe(c -> {}, e -> Timber.e(e, ""));
    }

    private DataParticipant removeFromConversation(String conversationId, String userId) {
        DataParticipant participant = new DataParticipant(conversationId, userId, Affiliation.NONE);
        participantsDAO.save(Collections.singletonList(participant));
        return participant;
    }

    private Observable<DataConversation> setConversationStatus(String conversationId,
                                                               @ConversationStatus.Status String status) {
        return conversationsDAO.getConversation(conversationId)
                .compose(new NonNullFilter<>())
                .take(1)
                .doOnNext(conversation -> {
                    conversation.setStatus(status);
                    conversationsDAO.save(conversation);
                });
    }
}
