package com.messenger.delegate;

import android.support.annotation.NonNull;

import com.messenger.entities.DataConversation;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.loaders.ContactsLoader;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.storage.dao.ConversationsDAO;
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
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;

public class LoaderDelegate {
    @Inject MessengerServerFacade messengerServerFacade;
    @Inject UserProcessor userProcessor;

    @Inject ConversationsDAO conversationsDAO;
    @Inject ParticipantsDAO participantsDAO;
    @Inject UsersDAO usersDAO;
    @Inject DecomposeMessagesHelper decomposeMessagesHelper;

    @Inject LoaderDelegate(@ForApplication Injector injector) {
        injector.inject(this);
    }

    public void synchronizeCache(@NotNull OnSynchronized listener) {
        Observable
                .zip(loadConversations(), loadContacts(), (o, o2) -> Boolean.TRUE)
                .onErrorReturn(e -> Boolean.FALSE)
                .subscribe(listener::onSynchronized, t -> Timber.e(t, "Error while synchronizing cache"));
    }

    public Observable<Void> loadConversation(String conversationId) {
        return loadConversations(messengerServerFacade.getLoaderManager()
                .createConversationLoader(conversationId).load().map(Collections::singletonList));
    }

    public Observable<Void> loadConversations() {
        return loadConversations(messengerServerFacade.getLoaderManager()
                .createConversationsLoader().load());
    }

    @NonNull
    private Observable<Void> loadConversations(Observable<List<Conversation>> conversationObservable) {
        return userProcessor.connectToUserProvider(conversationObservable
                .map(this::processConversationsData))
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

        return data.isEmpty() ? Collections.emptyList() : from(relationships)
                .map((elem, idx) -> new MessengerUser(elem.getUserId()))
                .distinct()
                .toList();
    }

    public Observable<List<DataUser>> loadContacts() {
        ContactsLoader contactsLoader = messengerServerFacade.getLoaderManager().createContactLoader();
        Observable<List<MessengerUser>> contactsObservable = contactsLoader.getContactsObservable()
                .doOnNext(contacts -> usersDAO.deleteFriends());
        return userProcessor.connectToUserProvider(contactsObservable);
    }

    public interface OnSynchronized {
        void onSynchronized(boolean syncResult);
    }
}
