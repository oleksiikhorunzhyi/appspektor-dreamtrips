package com.messenger.delegate.chat.typing;

import com.messenger.entities.DataConversation;
import com.messenger.messengerservers.chat.ChatState;
import com.messenger.storage.dao.ConversationsDAO;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Observable;

public class ChatStateDelegate {

    public static final int START_TYPING_DELAY = 1000;
    public static final int STOP_TYPING_DELAY = 2000;

    private ConversationsDAO conversationsDAO;
    private ActionPipe<ChatStateAction> chatStateActionPipe;

    private Observable<DataConversation> conversationObservable;

    private boolean typing;

    @Inject
    public ChatStateDelegate(Janet janet, ConversationsDAO conversationsDAO) {
        this.chatStateActionPipe = janet.createPipe(ChatStateAction.class);
        this.conversationsDAO = conversationsDAO;
    }

    public void init(String conversationId) {
        conversationObservable = conversationsDAO.getConversation(conversationId)
                .take(1)
                .cacheWithInitialCapacity(1);
    }

    public Observable<String> connectTypingStartAction(Observable<CharSequence> inputObservable) {
        return inputObservable
                .skip(1)
                .filter(text -> text.length() > 0)
                .throttleFirst(START_TYPING_DELAY, TimeUnit.MILLISECONDS)
                .filter(text -> !typing)
                .flatMap(text -> conversationObservable)
                .doOnNext(dataConversation -> {
                    typing = true;
                    chatStateActionPipe.send(new ChatStateAction(dataConversation, ChatState.COMPOSING));
                })
                .map(dataConversation -> ChatState.COMPOSING);
    }

    public Observable<String> connectTypingStopAction(Observable<CharSequence> inputObservable) {
       return inputObservable
                .skip(1)
                .debounce(STOP_TYPING_DELAY, TimeUnit.MILLISECONDS)
                .flatMap(text -> conversationObservable)
                .doOnNext(dataConversation -> {
                    typing = false;
                    chatStateActionPipe.send(new ChatStateAction(dataConversation, ChatState.PAUSE));
                })
                .map(dataConversation -> ChatState.PAUSE);
    }

}
