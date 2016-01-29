package com.messenger.delegate;

import com.messenger.entities.Conversation;
import com.messenger.entities.Message;
import com.messenger.entities.ParticipantsRelationship;
import com.messenger.entities.User;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.OnLoadedListener;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;

public class LoaderDelegate {

    private final MessengerServerFacade messengerServerFacade;
    private final UserProcessor userProcessor;

    private final ConversationsDAO conversationsDAO;
    private final ParticipantsDAO participantsDAO;
    private final MessageDAO messageDAO;
    private final UsersDAO usersDAO;

    public LoaderDelegate(MessengerServerFacade messengerServerFacade, UserProcessor userProcessor,
                          ConversationsDAO conversationsDAO, ParticipantsDAO participantsDAO, MessageDAO messageDAO, UsersDAO usersDAO) {
        this.messengerServerFacade = messengerServerFacade;
        this.userProcessor = userProcessor;
        this.conversationsDAO = conversationsDAO;
        this.participantsDAO = participantsDAO;
        this.messageDAO = messageDAO;
        this.usersDAO = usersDAO;
    }

    public void synchronizeCache(@NotNull OnSynchronized listener) {
        Observable
                .zip(loadConversations(), loadContacts(), (o, o2) -> Boolean.TRUE)
                .onErrorReturn(e -> Boolean.FALSE)
                .subscribe(listener::onSynchronized);
    }

    public Observable<Void> loadConversations() {
        Observable<List<com.messenger.messengerservers.model.User>> loader = Observable.<List<com.messenger.messengerservers.model.User>>create(subscriber -> {
            Loader<com.messenger.messengerservers.model.Conversation> conversationLoader = messengerServerFacade.getLoaderManager().createConversationLoader();
            conversationLoader.setOnEntityLoadedListener(new SubscriberLoaderListener<com.messenger.messengerservers.model.Conversation, com.messenger.messengerservers.model.User>(subscriber) {
                @Override
                protected List<com.messenger.messengerservers.model.User> process(List<com.messenger.messengerservers.model.Conversation> data) {
                    final long syncTime = System.currentTimeMillis();
                    List<Conversation> convs = from(data).map(Conversation::new).toList();
                    from(convs).forEachR(conversation -> conversation.setSyncTime(syncTime));

                    List<Message> messages = from(data)
                            .filter(c -> c.getLastMessage() != null)
                            .map(c -> new Message(c.getLastMessage())).notNulls().toList();

                    List<ParticipantsRelationship> relationships = data.isEmpty() ? Collections.emptyList() : from(data)
                            .filter(c -> c.getParticipants() != null)
                            .mapMany(d -> from(d.getParticipants()).map(ParticipantsRelationship::new))
                            .toList();
                    from(relationships).forEachR(relationship -> relationship.setSyncTime(syncTime));

                    conversationsDAO.save(convs);
                    conversationsDAO.deleteBySyncTime(syncTime);
                    messageDAO.save(messages);
                    participantsDAO.save(relationships);
                    participantsDAO.deleteBySyncTime(syncTime);

                    List<com.messenger.messengerservers.model.User> users = data.isEmpty() ? Collections.emptyList() : from(relationships)
                            .map((elem, idx) -> new com.messenger.messengerservers.model.User(elem.getUserId()))
                            .distinct()
                            .toList();
                    Timber.d("ConversationLoader %s", users);
                    return users;

                }
            });
            conversationLoader.load();
        });
        return userProcessor.connectToUserProvider(loader).map(users -> {
            Timber.d("ConversationLoader %s", users);
            return (Void) null;
        });
    }

    public Observable<List<User>> loadContacts() {
        Observable<List<com.messenger.messengerservers.model.User>> loader = Observable.<List<com.messenger.messengerservers.model.User>>create(subscriber -> {
            Loader<com.messenger.messengerservers.model.User> contactLoader = messengerServerFacade.getLoaderManager().createContactLoader();
            contactLoader.setOnEntityLoadedListener(new SubscriberLoaderListener<com.messenger.messengerservers.model.User, com.messenger.messengerservers.model.User>(subscriber) {
                @Override
                protected List<com.messenger.messengerservers.model.User> process(List<com.messenger.messengerservers.model.User> entities) {
                    usersDAO.deleteFriends();
                    Timber.d("ContactLoader %s", entities);
                    return entities;
                }
            });
            contactLoader.load();
        });
        return userProcessor.connectToUserProvider(loader);
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
