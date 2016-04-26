package com.messenger.delegate;


import android.text.TextUtils;
import android.util.Pair;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

import dagger.Lazy;
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
    LoaderDelegate loaderDelegate;
    @Inject
    Lazy<CreateConversationHelper> createConversationHelperLazy;

    @Inject
    public GroupChatEventDelegate(@ForApplication Injector injector) {
        injector.inject(this);
    }

    public void onChatInvited(String conversationId) {
        if (currentUserSession.get() == null || currentUserSession.get().get() == null
                || !currentUserSession.get().isPresent()) return;

        String currentUserId = currentUserSession.get().get().getUser().getUsername();

        createConversationHelperLazy.get().createConversation(conversationId, currentUserId)
                .flatMap(conversation -> {
                    conversationsDAO.save(conversation);
                    return loaderDelegate.loadParticipants(conversationId);
                }).subscribe(dataUsers -> {}, throwable -> Timber.d(throwable, ""));
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

    public void onChatLeft(String conversationId, String userId, boolean leave){
        Timber.i("Chat left :: chat=%s , user=%s", conversationId, userId);
        Observable.zip(
                conversationsDAO.getConversation(conversationId).compose(new NonNullFilter<>()).take(1),
                usersDAO.getUserById(userId).compose(new NonNullFilter<>()).take(1),
                (conversation, user) -> new Pair<>(conversation, user)
        )
                .subscribeOn(Schedulers.io())
                .subscribe(pair -> {
                    DataConversation conversation = pair.first;
                    DataUser dataUser = pair.second;
                    participantsDAO.delete(conversation.getId(), dataUser.getId());
                    if (TextUtils.equals(messengerServerFacade.getUsername(), dataUser.getId())) { // if it is owner action
                        conversation.setStatus(leave ? ConversationStatus.LEFT : ConversationStatus.KICKED);
                        conversationsDAO.save(conversation);
                    }
                }, throwable -> Timber.d(throwable, ""));
    }

}
