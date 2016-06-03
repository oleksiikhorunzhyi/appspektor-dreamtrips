package com.messenger.delegate.chat;

import android.text.TextUtils;
import android.util.Pair;

import com.messenger.delegate.conversation.LoadConversationDelegate;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.OnChatStateChangedListener;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.helper.ConversationHelper;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class ChatTypingDelegate {

    private final GlobalEventEmitter messengerGlobalEmitter;
    private final LoadConversationDelegate loadConversationDelegate;
    private final UsersDAO usersDAO;

    private PublishSubject<ChatChangeStateEvent> chatStateStream = PublishSubject.create();

    @Inject
    public ChatTypingDelegate(MessengerServerFacade messengerServerFacade, UsersDAO usersDAO,
                              LoadConversationDelegate loadConversationDelegate) {
        this.messengerGlobalEmitter = messengerServerFacade.getGlobalEventEmitter();
        this.loadConversationDelegate = loadConversationDelegate;
        this.usersDAO = usersDAO;
    }

    public Observable<Pair<ChatChangeStateEvent, DataUser>> connectChatTypingStream(String conversationId) {
        final OnChatStateChangedListener listener = (convId, userId, state) -> {
            chatStateStream.onNext(new ChatChangeStateEvent(userId, convId, state));
        };
        messengerGlobalEmitter.addOnChatStateChangedListener(listener);

        Observable<DataConversation> conversationObservable = loadConversationDelegate
                .loadConversationFromDb(conversationId)
                .subscribeOn(Schedulers.io())
                .cacheWithInitialCapacity(1);

        return chatStateStream.asObservable()
                .onBackpressureBuffer()
                .filter(chatChangeStateEvent -> TextUtils.equals(chatChangeStateEvent.conversationId, conversationId))
                .filter(event -> ConversationHelper.isPresent(conversationObservable.toBlocking().first()))
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
