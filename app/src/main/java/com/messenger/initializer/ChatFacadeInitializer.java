package com.messenger.initializer;

import android.text.TextUtils;
import android.util.Pair;

import com.messenger.delegate.LoaderDelegate;
import com.messenger.delegate.UserProcessor;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import java.util.Date;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;
import static com.messenger.messengerservers.entities.Participant.Affiliation.MEMBER;
import static java.util.Collections.singletonList;
import static rx.Observable.just;

public class ChatFacadeInitializer implements AppInitializer {

    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    ConversationsDAO conversationsDAO;
    @Inject
    ParticipantsDAO participantsDAO;
    @Inject
    UsersDAO usersDAO;
    //
    @Inject
    DreamSpiceManager spiceManager;
    //
    private UserProcessor userProcessor;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        userProcessor = new UserProcessor(spiceManager);
        //
        GlobalEventEmitter emitter = messengerServerFacade.getGlobalEventEmitter();
        //
        emitter.addGlobalMessageListener(new GlobalMessageListener() {
            @Override
            public void onReceiveMessage(Message message) {
                conversationsDAO.incrementUnreadField(message.getConversationId());
                message.setDate(new Date());
                message.setStatus(Message.Status.SENT);
                message.save();
            }

            @Override
            public void onSendMessage(Message message) {
                message.setDate(new Date());
                message.save();
            }
        });
        emitter.addOnSubjectChangesListener((conversationId, subject) -> {
            conversationsDAO.getConversation(conversationId).first()
                    .filter(c -> c != null && !TextUtils.equals(c.getSubject(), subject))
                    .compose(new IoToMainComposer<>())
                    .subscribe(conversation -> {
                        conversation.setSubject(subject);
                        conversation.save();
                    });
        });
        emitter.addOnChatCreatedListener((conversationId, createLocally) -> {
            Timber.i("Chat created :: chat=%s", conversationId);
            if (createLocally) return;
            conversationsDAO.getConversation(conversationId).first()
                    .filter(conversation -> conversation == null)
                    .flatMap(conversation -> {
                        LoaderDelegate loaderDelegate = new LoaderDelegate(messengerServerFacade, userProcessor, conversationsDAO);
                        return loaderDelegate.loadConversations();
                    })
                    .subscribe();
        });
        emitter.addOnChatJoinedListener((conversationId, userId) -> {
            Timber.i("Chat joined :: chat=%s , user=%s", conversationId, userId);
            usersDAO.getUserById(userId)
                    .flatMap(cachedUser -> {
                        if (cachedUser != null) return just(singletonList(cachedUser));
                        else return userProcessor.connectToUserProvider(just(singletonList(new User(userId))));
                    })
                    .doOnNext(users -> from(users).forEachR(u -> {
                        if (u.isOnline()) return;
                        u.setOnline(true);
                        u.save();
                    }))
                    .flatMap(users -> participantsDAO.getParticipants(conversationId)
                            .map(c -> SqlUtils.convertToList(ParticipantsRelationship.class, c))
                            .map(list -> from(list).map(ParticipantsRelationship::getUserId).contains(userId))
                            .first()
                    )
                    .filter(isAlreadyConnected -> !isAlreadyConnected)
                    .doOnNext(isAlreadyConnected -> {
                        new ParticipantsRelationship(conversationId, userId, MEMBER).save();
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        });
        emitter.addOnChatLeftListener((conversationId, userId) -> {
            Timber.i("Chat left :: chat=%s , user=%s", conversationId, userId);
            Observable.zip(
                    conversationsDAO.getConversation(conversationId).compose(new NonNullFilter<>()),
                    usersDAO.getUserById(userId).compose(new NonNullFilter<>()),
                    (conversation, user) -> new Pair<>(conversation, user)
            )
                    .subscribeOn(Schedulers.io()).first()
                    .subscribe(pair -> {
                        if (messengerServerFacade.getOwner().equals(pair.second)) {
                            conversationsDAO.deleteConversation(pair.first.getId());
                            participantsDAO.delete(pair.first.getId());
                        } else {
                            participantsDAO.delete(pair.first.getId(), pair.second.getId());
                        }
                    });
        });
    }
}
