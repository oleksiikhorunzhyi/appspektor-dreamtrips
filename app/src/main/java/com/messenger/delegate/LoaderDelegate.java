package com.messenger.delegate;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ConversationData;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.entities.User$Table;
import com.messenger.messengerservers.listeners.OnLoadedListener;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.storage.dao.ConversationsDAO;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

import static com.innahema.collections.query.queriables.Queryable.from;

public class LoaderDelegate {

    private final MessengerServerFacade messengerServerFacade;
    private final UserProcessor userProcessor;
    private final ConversationsDAO conversationsDAO;

    public LoaderDelegate(MessengerServerFacade messengerServerFacade, UserProcessor userProcessor, ConversationsDAO conversationsDAO) {
        this.messengerServerFacade = messengerServerFacade;
        this.userProcessor = userProcessor;
        this.conversationsDAO = conversationsDAO;
    }

    public void synchronizeCache(@NotNull OnSynchronized listener) {
        Observable
                .zip(loadConversations(), loadContacts(), (o, o2) -> Boolean.TRUE)
                .onErrorReturn(e -> Boolean.FALSE)
                .subscribe(listener::onSynchronized);
    }

    public Observable<Void> loadConversations() {
        Observable<List<User>> loader = Observable.<List<User>>create(subscriber -> {
            Loader<ConversationData> conversationLoader = messengerServerFacade.getLoaderManager().createConversationLoader();
            conversationLoader.setOnEntityLoadedListener(new SubscriberLoaderListener<ConversationData, User>(subscriber) {
                @Override
                protected List<User> process(List<ConversationData> data) {
                    // cleanup cache
                    // // TODO: 1/8/16 clean replace this logic, cause we can creat single without internet on user profile
                    List<Conversation> conversations = conversationsDAO.getConversationsList(Conversation.Type.GROUP);
                    conversationsDAO.deleteConversations(conversations);
                    // save conversations
                    List<Conversation> convs = from(data).map(d -> d.conversation).toList();
                    List<Message> messages = from(data).map(c -> c.lastMessage).notNulls().toList();
                    TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo.withModels(convs)));
                    TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo.withModels(messages)));
                    // save relationships
                    List<ParticipantsRelationship> relationships = data.isEmpty() ? Collections.emptyList() : from(data)
                            .mapMany(d -> from(d.participants).map(p -> new ParticipantsRelationship(d.conversation.getId(), p.getUser(), p.getAffiliation())))
                            .toList();
                    TransactionManager.getInstance().addTransaction(new SaveModelTransaction(ProcessModelInfo.withModels(relationships)));
                    //
                    List<User> users = data.isEmpty() ? Collections.emptyList() : from(data).mapMany(d -> d.participants).map((elem, idx) -> elem.getUser()).distinct().toList();
                    return users;
                }
            });
            conversationLoader.load();
        });
        return userProcessor.connectToUserProvider(loader).map(users -> (Void) null);
    }

    public Observable<List<User>> loadContacts() {
        Observable<List<User>> loader = Observable.<List<User>>create(subscriber -> {
            Loader<User> contactLoader = messengerServerFacade.getLoaderManager().createContactLoader();
            contactLoader.setOnEntityLoadedListener(new SubscriberLoaderListener<User, User>(subscriber) {
                @Override
                protected List<User> process(List<User> entities) {
                    new Delete().from(User.class).where(Condition.column(User$Table.FRIEND).is(true)).queryClose();
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
