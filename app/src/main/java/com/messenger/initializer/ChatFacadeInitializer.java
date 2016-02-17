package com.messenger.initializer;

import android.text.TextUtils;
import android.util.Pair;

import com.messenger.delegate.LoaderDelegate;
import com.messenger.delegate.UserProcessor;
import com.messenger.entities.DataAttachment;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.ConversationIdHelper;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.User;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;
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
    @Inject
    Lazy<DataUser> currentUser;
    //
    @Inject
    DreamSpiceManager spiceManager;
    //
    private UserProcessor userProcessor;
    private final ConversationIdHelper conversationIdHelper = new ConversationIdHelper();
    private LoaderDelegate loaderDelegate;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        userProcessor = new UserProcessor(usersDAO, spiceManager);
        loaderDelegate = new LoaderDelegate(messengerServerFacade, userProcessor,
                conversationsDAO, participantsDAO, messageDAO, usersDAO, attachmentDAO);

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
                        conversationsDAO.save(conversation);
                    });
        });
        emitter.addInvitationListener((conversationId) -> {
            Timber.i("Chat invited :: chat=%s", conversationId);
            loadConversation(conversationId)
                    .doOnError(throwable -> Timber.d(throwable, ""))
                    .subscribe();
        });
        emitter.addOnChatJoinedListener((participant, isOnline) -> {
            Timber.i("Chat joined :: participant=%s", participant);

            Observable.just(new DataParticipant(participant))
                    .subscribeOn(Schedulers.io())
                    .doOnNext(participantsDAO::save)
                    .flatMap(p -> usersDAO.getUserById(p.getUserId()).first())

                    .flatMap(cachedUser -> {
                        if (cachedUser != null) return just(singletonList(cachedUser));
                        else {
                            return userProcessor.connectToUserProvider(just(singletonList(createUser(participant.getUserId()))));
                        }
                    })
                    .doOnNext(users -> from(users).filter(u -> u.isOnline() != isOnline).forEachR(u -> {
                        u.setOnline(isOnline);
                        usersDAO.save(u);
                    }))
                    .doOnError(throwable -> Timber.d(throwable, ""))
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

    private Observable<List<DataUser>> loadConversation(String conversationId) {
        return Observable.just(conversationId)
                .flatMap(convId -> {
                    conversationsDAO.save(new DataConversation.Builder()
                            .id(convId)
                            .lastActiveDate(System.currentTimeMillis())
                            .status(ConversationStatus.PRESENT)
                            .type(conversationIdHelper.obtainType(convId, currentUser.get().getId()))
                            .build()
                    );
                    return loaderDelegate.loadParticipants(conversationId);
                });
    }
}
