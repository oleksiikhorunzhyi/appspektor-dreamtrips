package com.messenger.initializer;

import android.text.TextUtils;
import android.util.Pair;

import com.messenger.delegate.LoaderDelegate;
import com.messenger.delegate.UserProcessor;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataParticipant;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.User;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;
import static com.messenger.messengerservers.model.Participant.Affiliation.MEMBER;
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
    @Inject
    MessageDAO messageDAO;
    @Inject
    AttachmentDAO attachmentDAO;
    //
    @Inject
    DreamSpiceManager spiceManager;
    //
    private UserProcessor userProcessor;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        userProcessor = new UserProcessor(usersDAO, spiceManager);
        //
        GlobalEventEmitter emitter = messengerServerFacade.getGlobalEventEmitter();
        //
        int maximumYear = Calendar.getInstance().getMaximum(Calendar.YEAR);
        //
        emitter.addGlobalMessageListener(new GlobalMessageListener() {
            @Override
            public void onReceiveMessage(Message message) {
                long time = System.currentTimeMillis();
                message.setDate(time);
                message.setStatus(MessageStatus.SENT);

                DataMessage dataMessage = new DataMessage(message);
                dataMessage.setSyncTime(System.currentTimeMillis());
                List<DataAttachment> attachments = DataAttachment.fromMessage(message);
                if (!attachments.isEmpty()) attachmentDAO.save(attachments);
                messageDAO.save(dataMessage);
                conversationsDAO.incrementUnreadField(message.getConversationId());
                conversationsDAO.updateDate(message.getConversationId(), time);
            }

            @Override
            public void onPreSendMessage(Message message) {
                long time = System.currentTimeMillis();
                messageDAO.updateStatus(message.getId(), message.getStatus(), time);
                conversationsDAO.updateDate(message.getConversationId(), time);
            }

            @Override
            public void onSendMessage(Message message) {
                // for error messages we set max date with purpose to show these on bottom of
                // the messages list is selected and sorted by syncTime
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, maximumYear);
                long time = message.getStatus() == MessageStatus.ERROR ? calendar.getTimeInMillis() : System.currentTimeMillis();
                message.setDate(time);

                DataMessage dataMessage = new DataMessage(message);
                dataMessage.setSyncTime(time);
                List<DataAttachment> attachments = DataAttachment.fromMessage(message);
                if (!attachments.isEmpty()) attachmentDAO.save(attachments);
                messageDAO.save(dataMessage);
                conversationsDAO.updateDate(message.getConversationId(), time);
            }
        });

        emitter.addOnSubjectChangesListener((conversationId, subject) -> {
            conversationsDAO.getConversation(conversationId).first()
                    .filter(c -> c != null && !TextUtils.equals(c.getSubject(), subject))
                    .compose(new IoToMainComposer<>())
                    .doOnError(throwable -> Timber.d(throwable, ""))
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
                    .doOnError(throwable -> Timber.d(throwable, ""))
                    .flatMap(conversation -> {
                        LoaderDelegate loaderDelegate = new LoaderDelegate(messengerServerFacade, userProcessor,
                                conversationsDAO, participantsDAO, messageDAO, usersDAO, attachmentDAO);
                        return loaderDelegate.loadConversations();
                    })
                    .subscribe();
        });
        emitter.addInvitationListener((conversationId) -> {
            Timber.i("Chat invited :: chat=%s", conversationId);
            conversationsDAO.getConversation(conversationId).first()
                    .filter(conversation -> conversation != null // in other case addOnChatCreatedListener will be called
                            && !TextUtils.equals(conversation.getStatus(), ConversationStatus.PRESENT))
                    .flatMap(conversation -> {
                        LoaderDelegate loaderDelegate = new LoaderDelegate(messengerServerFacade, userProcessor,
                                conversationsDAO, participantsDAO, messageDAO, usersDAO, attachmentDAO);
                        return loaderDelegate.loadConversations();
                    })
                    .doOnError(throwable -> Timber.d(throwable, ""))
                    .subscribe();
        });
        emitter.addOnChatJoinedListener((conversationId, userId, isOnline) -> {
            Timber.i("Chat joined :: chat=%s , user=%s", conversationId, userId);
            usersDAO.getUserById(userId)
                    .first()
                    .flatMap(cachedUser -> {
                        if (cachedUser != null) return just(singletonList(cachedUser));
                        else
                            return userProcessor.connectToUserProvider(just(singletonList(createUser(userId))));
                    })
                    .doOnNext(users -> from(users).filter(u -> u.isOnline() != isOnline).forEachR(u -> {
                        u.setOnline(isOnline);
                        u.save();
                    }))
                    .flatMap(users -> participantsDAO.getParticipants(conversationId).first()
                                    .map(c -> SqlUtils.convertToList(DataParticipant.class, c))
                                    .map(list -> from(list).map(DataParticipant::getUserId).contains(userId))
                    )
                    .filter(isAlreadyConnected -> !isAlreadyConnected)
                    .doOnNext(isAlreadyConnected -> {
                        new DataParticipant(conversationId, userId, MEMBER).save();
                    })
                    .doOnError(throwable -> Timber.d(throwable, ""))
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        });
        emitter.addOnChatLeftListener((conversationId, userId, leave) -> {
            Timber.i("Chat left :: chat=%s , user=%s", conversationId, userId);
            Observable.zip(
                    conversationsDAO.getConversation(conversationId).compose(new NonNullFilter<>()).first(),
                    usersDAO.getUserById(userId).compose(new NonNullFilter<>()).first(),
                    (conversation, user) -> new Pair<>(conversation, user)
            )
                    .subscribeOn(Schedulers.io()).first()
                    .doOnError(throwable -> Timber.d(throwable, ""))
                    .subscribe(pair -> {
                        DataConversation conversation = pair.first;
                        participantsDAO.delete(conversation.getId(), pair.second.getId());
                        if (TextUtils.equals(messengerServerFacade.getUsername(), pair.second.getId())) { // if it is owner action
                            conversation.setStatus(leave ? ConversationStatus.LEFT : ConversationStatus.KICKED);
                            conversationsDAO.save(Collections.singletonList(conversation));
                        }
                    });
        });
    }

    private User createUser(String userId) {
        return new User(userId);
    }
}
