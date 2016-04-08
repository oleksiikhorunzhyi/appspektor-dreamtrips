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
import com.messenger.messengerservers.event.JoinedEvent;
import com.messenger.messengerservers.listeners.GlobalMessageListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.messengerservers.model.Participant;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;
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
    SessionHolder<UserSession> sessionHolder;
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
                dataMessage.setSyncTime(time);
                List<DataAttachment> attachments = DataAttachment.fromMessage(message);
                if (!attachments.isEmpty()) attachmentDAO.save(attachments);
                messageDAO.save(dataMessage);
                conversationsDAO.incrementUnreadField(message.getConversationId());
                conversationsDAO.updateDate(message.getConversationId(), time);
            }

            @Override
            public void onPreSendMessage(Message message) {
                long time = System.currentTimeMillis();
                message.setDate(time);
                DataMessage dataMessage = new DataMessage(message);
                dataMessage.setSyncTime(time);

                List<DataAttachment> attachments = DataAttachment.fromMessage(message);
                if (!attachments.isEmpty()) attachmentDAO.save(attachments);

                messageDAO.save(dataMessage);
                conversationsDAO.updateDate(message.getConversationId(), time);
            }

            @Override
            public void onSendMessage(Message message) {
                // for error messages we set max date with purpose to show these on bottom of
                // the messages list is selected and sorted by syncTime
                long time;
                if (message.getStatus() == MessageStatus.ERROR) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, maximumYear);
                    time = calendar.getTimeInMillis();
                } else {
                    time = System.currentTimeMillis();
                }
                messageDAO.updateStatus(message.getId(), message.getStatus(), time);
                conversationsDAO.updateDate(message.getConversationId(), time);
            }
        });

        emitter.addOnSubjectChangesListener((conversationId, subject) -> {
            conversationsDAO.getConversation(conversationId).first()
                    .subscribeOn(Schedulers.io())
                    .filter(c -> c != null && !TextUtils.equals(c.getSubject(), subject))
                    .subscribe(conversation -> {
                        conversation.setSubject(subject);
                        conversationsDAO.save(conversation);
                    }, throwable -> Timber.d(throwable, ""));
        });

        emitter.addOnAvatarChangeListener(this::onAvatarChanged);

        emitter.addInvitationListener((conversationId) -> {
            Timber.i("Chat invited :: chat=%s", conversationId);
            loadConversation(conversationId)
                    .subscribe(dataUsers -> {}, throwable -> Timber.d(throwable, ""));
        });

        emitter.createChatJoinedObservable()
                .subscribeOn(Schedulers.io())
                .buffer(3, TimeUnit.SECONDS)
                .filter(joinedEvents -> !joinedEvents.isEmpty())
                .onBackpressureBuffer()
                .doOnNext(this::saveNewParticipants)
                .map(this::filterNotExistedUsersAndUpdateExisted)
                .flatMap(users -> userProcessor.connectToUserProvider(just(users)))
                .doOnNext(usersDAO::save)
                .subscribe(dataUsers -> {}, throwable -> Timber.d(throwable, ""));

        emitter.addOnChatLeftListener((conversationId, userId, leave) -> {
            Timber.i("Chat left :: chat=%s , user=%s", conversationId, userId);
            Observable.zip(
                    conversationsDAO.getConversation(conversationId).compose(new NonNullFilter<>()).first(),
                    usersDAO.getUserById(userId).compose(new NonNullFilter<>()).first(),
                    (conversation, user) -> new Pair<>(conversation, user)
            )
                    .subscribeOn(Schedulers.io()).first()
                    .subscribe(pair -> {
                        DataConversation conversation = pair.first;
                        DataUser dataUser = pair.second;
                        participantsDAO.delete(conversation.getId(), dataUser.getId());
                        if (TextUtils.equals(messengerServerFacade.getUsername(), dataUser.getId())) { // if it is owner action
                            conversation.setStatus(leave ? ConversationStatus.LEFT : ConversationStatus.KICKED);
                            conversationsDAO.save(conversation);
                        }
                    }, throwable -> Timber.d(throwable, ""));
        });
    }

    private MessengerUser createUser(Participant participant, boolean isOnline) {
        MessengerUser messengerUser = new MessengerUser(participant.getUserId());
        messengerUser.setOnline(isOnline);
        return messengerUser;
    }

    private Observable<List<DataUser>> loadConversation(String conversationId) {
        return Observable.just(conversationId)
                .flatMap(convId -> {
                    conversationsDAO.save(new DataConversation.Builder()
                            .id(convId)
                            .lastActiveDate(System.currentTimeMillis())
                            .status(ConversationStatus.PRESENT)
                            .type(conversationIdHelper.obtainType(convId, getCurrentUserId()))
                            .build()
                    );
                    return loaderDelegate.loadParticipants(conversationId);
                });
    }

    private String getCurrentUserId() {
        if (!sessionHolder.get().isPresent()) return "";
        return sessionHolder.get().get().getUser().getUsername();
    }

    private void saveNewParticipants(List<JoinedEvent> joinedEvents) {
        List<DataParticipant> participants = from(joinedEvents).map(e -> new DataParticipant(e.getParticipant())).toList();
        participantsDAO.save(participants);
    }

    private List<MessengerUser> filterNotExistedUsersAndUpdateExisted(List<JoinedEvent> joinedEvents) {
        List<DataUser> existedUsers = new ArrayList<>(joinedEvents.size());
        List<MessengerUser> newMessengerUsers = new ArrayList<>(joinedEvents.size());

        for (JoinedEvent e: joinedEvents) {
            Participant participant = e.getParticipant();
            DataUser cachedUser = usersDAO.getUserById(participant.getUserId()).toBlocking().first();
            if (cachedUser != null) {
                cachedUser.setOnline(e.isOnline());
                existedUsers.add(cachedUser);
            }
            else {
                newMessengerUsers.add(createUser(participant, joinedEvents.isEmpty()));
            }
        }
        usersDAO.save(existedUsers);
        return newMessengerUsers;
    }

    private void onAvatarChanged(String conversationId, String avatar) {
        conversationsDAO.getConversation(conversationId).first()
                .subscribeOn(Schedulers.io())
                .filter(c -> c != null && !TextUtils.equals(c.getAvatar(), avatar))
                .subscribe(conversation -> {
                    conversation.setAvatar(avatar);
                    conversationsDAO.save(conversation);
                }, e -> Timber.d(e, "Could not save avatar to conversation"));
    }
}
