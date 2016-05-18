package com.messenger.delegate.chat;

import android.text.TextUtils;
import android.util.Pair;

import com.messenger.entities.DataUser;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.OnChatStateChangedListener;
import com.messenger.storage.dao.UsersDAO;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

public class ChatTypingDelegate {

    private final GlobalEventEmitter messengerGlobalEmitter;
    private final UsersDAO usersDAO;

    private PublishSubject<ChatChangeStateEvent> chatStateStream = PublishSubject.create();

    @Inject
    public ChatTypingDelegate(MessengerServerFacade messengerServerFacade, UsersDAO usersDAO) {
        this.messengerGlobalEmitter = messengerServerFacade.getGlobalEventEmitter();
        this.usersDAO = usersDAO;
    }

    public Observable<Pair<ChatChangeStateEvent, DataUser>> connectChatTypingStream(String conversationId) {
        final OnChatStateChangedListener listener = (convId, userId, state) -> {
            chatStateStream.onNext(new ChatChangeStateEvent(userId, convId, state));
        };
        messengerGlobalEmitter.addOnChatStateChangedListener(listener);

        return chatStateStream.asObservable()
                .onBackpressureBuffer()
                .filter(chatChangeStateEvent -> TextUtils.equals(chatChangeStateEvent.conversationId, conversationId))
                .doOnUnsubscribe(() -> messengerGlobalEmitter.removeOnChatStateChangedListener(listener))
                .map(stateEvent -> new Pair<>(stateEvent, usersDAO.getUserById(stateEvent.userId).toBlocking().first()))
                .filter(chatChangeStateEventDataUserPair -> chatChangeStateEventDataUserPair.second != null);
    }

    public static class ChatChangeStateEvent {
        public final String userId;
        public final String conversationId;
        public final String state;

        private ChatChangeStateEvent(String userId, String conversationId, String state) {
            this.userId = userId;
            this.conversationId = conversationId;
            this.state = state;
        }
    }


}
