package com.messenger.delegate;

import android.support.annotation.NonNull;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.OnLoadedListener;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
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
    DecomposeMessagesHelper decomposeMessagesHelper;

    @Inject
    public LoaderDelegate(@ForApplication Injector injector) {
        injector.inject(this);
    }

    public void synchronizeCache(@NotNull OnSynchronized listener) {
        Observable
                .zip(loadConversations(), loadContacts(), (o, o2) -> Boolean.TRUE)
                .onErrorReturn(e -> Boolean.FALSE)
                .subscribe(listener::onSynchronized, t -> Timber.e(t, "Error while synchronizing cache"));
    }

    public Observable<Void> loadConversation(String conversationId) {
        return loadConversations(messengerServerFacade.getLoaderManager().createConversationLoader(conversationId));
    }

    public Observable<Void> loadConversations() {
        return loadConversations(messengerServerFacade.getLoaderManager().createConversationsLoader());
    }

    @NonNull
    private Observable<Void> loadConversations(Loader<Conversation> conversationLoader) {
        Observable<List<MessengerUser>> loader = Observable.<List<MessengerUser>>create(subscriber -> {
            conversationLoader.setOnEntityLoadedListener(new SubscriberLoaderListener<List<MessengerUser>, Conversation>(subscriber) {
                @Override
                protected List<MessengerUser> process(List<Conversation> data) {
                    return processConversationsData(data);
                }
            });
            conversationLoader.load();
        });
        return userProcessor.connectToUserProvider(loader)
                .map(var -> (Void) null);
    }

    private List<MessengerUser> processConversationsData(List<Conversation> data) {
        final long syncTime = System.currentTimeMillis();
        List<DataConversation> convs = from(data).map(DataConversation::new).toList();
        from(convs).forEachR(conversation -> conversation.setSyncTime(syncTime));

        List<Message> serverMessages = from(data).map(Conversation::getLastMessage).notNulls().toList();
        DecomposeMessagesHelper.Result result = decomposeMessagesHelper.decomposeMessages(serverMessages);
        from(result.messages).forEachR(msg -> msg.setSyncTime(System.currentTimeMillis()));

        List<DataParticipant> relationships = new ArrayList<>();
        if (!data.isEmpty()) {
            from(data)
                    .filter(conversation -> conversation.getParticipants() != null)
                    .map(Conversation::getParticipants)
                    .map(participants -> from(participants).map(DataParticipant::new).toList())
                    .forEachR(relationships::addAll);
            from(relationships).forEachR(relationship -> relationship.setSyncTime(syncTime));
        }

        conversationsDAO.save(convs);
        boolean singleConversationData = data.size() == 1;
        if (!singleConversationData) {
            conversationsDAO.deleteBySyncTime(syncTime);
        }
        decomposeMessagesHelper.saveDecomposeMessage(result);
        participantsDAO.save(relationships);
        if (singleConversationData) {
            participantsDAO.deleteBySyncTime(syncTime, data.get(0).getId());
        } else {
            participantsDAO.deleteBySyncTime(syncTime);
        }

        List<MessengerUser> messengerUsers = data.isEmpty() ? Collections.emptyList() : from(relationships)
                .map((elem, idx) -> new MessengerUser(elem.getUserId()))
                .distinct()
                .toList();
        return messengerUsers;
    }

    public Observable<List<DataUser>> loadContacts() {
        Observable<List<MessengerUser>> loader = Observable.<List<MessengerUser>>create(subscriber -> {
            Loader<MessengerUser> contactLoader = messengerServerFacade.getLoaderManager().createContactLoader();
            contactLoader.setOnEntityLoadedListener(new SubscriberLoaderListener<List<MessengerUser>, MessengerUser>(subscriber) {
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

    private static abstract class SubscriberLoaderListener<R, I> implements OnLoadedListener<I> {

        private Subscriber subscriber;

        public SubscriberLoaderListener(Subscriber subscriber) {
            this.subscriber = subscriber;
        }

        protected abstract R process(List<I> entities);

        @Override
        public void onLoaded(List<I> entities) {
            R result = process(entities);
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
