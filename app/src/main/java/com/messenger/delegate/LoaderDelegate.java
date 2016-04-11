package com.messenger.delegate;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.OnLoadedListener;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.LocationDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.PhotoDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.util.DecomposeMessagesHelper;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;

public class LoaderDelegate {

    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    UserProcessor userProcessor;

    @Inject
    ConversationsDAO conversationsDAO;
    @Inject
    ParticipantsDAO participantsDAO;
    @Inject
    MessageDAO messageDAO;
    @Inject
    UsersDAO usersDAO;
    @Inject
    AttachmentDAO attachmentDAO;
    @Inject
    PhotoDAO photoDAO;
    @Inject
    LocationDAO locationDAO;

    @Inject
    public LoaderDelegate(@ForApplication Injector injector) {
        injector.inject(this);
    }

    public void synchronizeCache(@NotNull OnSynchronized listener) {
        Observable
                .zip(loadConversations(), loadContacts(), (o, o2) -> Boolean.TRUE)
                .onErrorReturn(e -> Boolean.FALSE)
                .subscribe(listener::onSynchronized);
    }

    public Observable<Void> loadConversations() {
        Observable<List<MessengerUser>> loader = Observable.<List<MessengerUser>>create(subscriber -> {
            Loader<Conversation> conversationLoader = messengerServerFacade.getLoaderManager().createConversationLoader();
            conversationLoader.setOnEntityLoadedListener(new SubscriberLoaderListener<Conversation, MessengerUser>(subscriber) {
                @Override
                protected List<MessengerUser> process(List<Conversation> data) {
                    final long syncTime = System.currentTimeMillis();
                    List<DataConversation> convs = from(data).map(DataConversation::new).toList();
                    from(convs).forEachR(conversation -> conversation.setSyncTime(syncTime));

                    List<Message> serverMessages = from(data).map(conv -> conv.getLastMessage()).notNulls().toList();
                    DecomposeMessagesHelper.DecomposedMessagesResult decomposedMessagesResult = DecomposeMessagesHelper.decomposeMessages(serverMessages);
                    from(decomposedMessagesResult.messages).forEachR(msg -> msg.setSyncTime(System.currentTimeMillis()));

                    List<DataParticipant> relationships = new ArrayList<>();
                    if (!data.isEmpty()) {
                        from(data)
                                .filter(conversation -> conversation.getParticipants() != null)
                                .map(conversation -> conversation.getParticipants())
                                .map(participants -> from(participants).map(DataParticipant::new).toList())
                                .forEachR(bunchRelationships -> relationships.addAll(bunchRelationships));
                        from(relationships).forEachR(relationship -> relationship.setSyncTime(syncTime));
                    }

                    conversationsDAO.save(convs);
                    conversationsDAO.deleteBySyncTime(syncTime);

                    photoDAO.save(decomposedMessagesResult.photoAttachments);
                    locationDAO.save(decomposedMessagesResult.locationAttachments);
                    attachmentDAO.save(decomposedMessagesResult.attachments);
                    messageDAO.save(decomposedMessagesResult.messages);
                    
                    participantsDAO.save(relationships);
                    participantsDAO.deleteBySyncTime(syncTime);

                    List<MessengerUser> messengerUsers = data.isEmpty() ? Collections.emptyList() : from(relationships)
                            .map((elem, idx) -> new MessengerUser(elem.getUserId()))
                            .distinct()
                            .toList();
                    Timber.d("ConversationLoader %s", messengerUsers);
                    return messengerUsers;

                }
            });
            conversationLoader.load();
        });
        return userProcessor.connectToUserProvider(loader)
                .map(var -> (Void) null);
    }

    public Observable<List<DataUser>> loadContacts() {
        Observable<List<MessengerUser>> loader = Observable.<List<MessengerUser>>create(subscriber -> {
            Loader<MessengerUser> contactLoader = messengerServerFacade.getLoaderManager().createContactLoader();
            contactLoader.setOnEntityLoadedListener(new SubscriberLoaderListener<MessengerUser, MessengerUser>(subscriber) {
                @Override
                protected List<MessengerUser> process(List<MessengerUser> entities) {
                    usersDAO.deleteFriends();
                    Timber.d("ContactLoader %s", entities);
                    return entities;
                }
            });
            contactLoader.load();
        });
        return userProcessor.connectToUserProvider(loader);
    }

    public Observable<List<DataUser>> loadParticipants(String conversationId) {
        return userProcessor.connectToUserProvider(messengerServerFacade.getLoaderManager().createParticipantsLoader()
                .load(conversationId)
                .doOnNext(participants -> participantsDAO.save(from(participants).map(DataParticipant::new).toList()))
                .map(participants -> from(participants).map(p -> new MessengerUser(p.getUserId())).toList()))
                .doOnNext(usersDAO::save);
    }

    private static abstract class SubscriberLoaderListener<I, R> implements OnLoadedListener<I> {

        private Subscriber<? super List<R>> subscriber;

        public SubscriberLoaderListener(Subscriber<? super List<R>> subscriber) {
            this.subscriber = subscriber;
        }

        protected abstract List<R> process(List<I> entities);

        @Override
        public void onLoaded(List<I> entities) {
            List<R> result = process(entities);
            if (subscriber.isUnsubscribed()) return;
            //
            subscriber.onNext(result);
            subscriber.onCompleted();
        }

        @Override
        public void onError(Exception e) {
            if (subscriber.isUnsubscribed()) return;
            subscriber.onError(e);
        }
    }


    public interface OnSynchronized {
        void onSynchronized(boolean syncResult);
    }
}
