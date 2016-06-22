package com.messenger.delegate.chat.typing;

import com.messenger.entities.DataTyping;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.ConnectionStatus;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.TypingDAO;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

@Singleton
public class TypingManager {
    private final TypingDAO typingDAO;

    @Inject public TypingManager(MessengerServerFacade serverFacade, TypingDAO typingDAO) {
        this.typingDAO = typingDAO;

        serverFacade.getStatusObservable()
                .filter(this::filterConnectionStatus)
                .subscribe(connectionStatus -> clearCache());
    }

    private boolean filterConnectionStatus(ConnectionStatus connectionStatus) {
        return connectionStatus == ConnectionStatus.DISCONNECTED || connectionStatus == ConnectionStatus.ERROR;
    }

    public void clearCache() {
        executeObservable(typingDAO::deleteAll)
                .subscribe();
    }

    public void userStartTyping(String conversationId, String userId) {
        executeObservable(() -> typingDAO.save(new DataTyping(conversationId, userId)))
                .subscribe();
    }

    public void userStopTyping(String conversationId, String userId) {
        executeObservable(() -> typingDAO.deleteById(DataTyping.generateId(conversationId, userId)))
                .subscribe();
    }

    public void userOffline(String userId) {
        executeObservable(() -> typingDAO.deleteByUserId(userId))
                .subscribe();
    }

    public Observable<List<DataUser>> getTypingObservable(String conversationId) {
        return typingDAO.getTypingUser(conversationId)
                .doOnNext(Collections::sort);
    }

    protected Observable<Void> executeObservable(Action0 action) {
        return Observable.<Void>create(subscriber -> {
            action.call();
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io());
    }
}
