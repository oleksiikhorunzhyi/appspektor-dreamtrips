package com.messenger.delegate;

import com.messenger.entities.DataConversation;
import com.messenger.storage.dao.ConversationsDAO;

import javax.inject.Inject;

import rx.Observable;
import rx.observables.ConnectableObservable;
import timber.log.Timber;

public class LoadConversationDelegate {

    ConversationsDAO conversationsDAO;
    LoaderDelegate loaderDelegate;

    @Inject
    public LoadConversationDelegate(ConversationsDAO conversationsDAO, LoaderDelegate loaderDelegate) {
        this.conversationsDAO = conversationsDAO;
        this.loaderDelegate = loaderDelegate;
    }

    public Observable<DataConversation> loadConversationFromNetwork(String conversationId) {
        ConnectableObservable<DataConversation> observable = loaderDelegate.loadConversation(conversationId)
                .flatMap(aVoid -> loadFromDatabase(conversationId)).publish();
        observable.subscribe(conversation -> {}, e -> Timber.e(e, "Could not load conversation from network"));
        observable.connect();
        return observable;
    }

    private Observable<DataConversation> loadFromDatabase(String conversationId) {
        return conversationsDAO.getConversation(conversationId).take(1);
    }
}
