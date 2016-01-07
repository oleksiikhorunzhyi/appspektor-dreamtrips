package com.messenger.initializer;

import android.util.Pair;

import com.messenger.delegate.LoaderDelegate;
import com.messenger.delegate.UserProcessor;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.storege.dao.ConversationsDAO;
import com.messenger.storege.dao.ParticipantsDAO;
import com.messenger.storege.dao.UsersDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.rx.NonNullFilter;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;
import static com.messenger.messengerservers.entities.Participant.Affiliation.MEMBER;

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
    private final ConversationHelper conversationHelper;
    private UserProcessor userProcessor;

    public ChatFacadeInitializer() {
        conversationHelper = new ConversationHelper();
    }

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
                message.setRead(false);
                message.save();
            }

            @Override
            public void onSendMessage(Message message) {
                message.setDate(new Date());
                message.setRead(true);
                ContentUtils.insert(Message.CONTENT_URI, message);
            }
        });
        emitter.addOnSubjectChangesListener((conversationId, subject) -> {
            conversationsDAO.getConversation(conversationId).first()
                    .subscribe(conversation -> {
                        if (conversation == null)
                            return; // TODO there should be no such situation, but sync init state is broken
                        conversation.setSubject(subject);
                        conversation.save();
                    });
        });
        emitter.addOnChatCreatedListener((conversationId, createLocally) -> {
            Timber.i("Chat created :: chat=%s", conversationId);
            conversationsDAO.getConversation(conversationId).first()
                    .flatMap(conversation -> {
                        LoaderDelegate loaderDelegate = new LoaderDelegate(messengerServerFacade, userProcessor);
                        return loaderDelegate.loadConversations();
                    })
                    .subscribe();
        });
        emitter.addOnChatJoinedListener((conversationId, userId) -> {
            Timber.i("Chat joined :: chat=%s , user=%s", conversationId, userId);
            participantsDAO
                    .getParticipants(conversationId)
                    .map(c -> SqlUtils.convertToList(ParticipantsRelationship.class, c))
                    .first()
                    .filter(list -> !from(list).map(ParticipantsRelationship::getUserId).contains(userId))
                    .flatMap(list -> usersDAO.getUserById(userId))
                    .flatMap(cachedUser -> {
                        Observable<List<User>> provider = cachedUser == null ?
                                Observable.just(Collections.emptyList()) : Observable.just(Collections.singletonList(new User(userId)));
                        return userProcessor.connectToUserProvider(provider);
                    })
                    .doOnNext(aVoid -> new ParticipantsRelationship(conversationId, userId, MEMBER).save())
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
