package com.messenger.delegate.chat.typing;

import com.messenger.messengerservers.chat.ChatState;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Observable;

public class ChatStateDelegate {

    public static final int START_TYPING_DELAY = 1000;
    public static final int STOP_TYPING_DELAY = 2000;

    private ActionPipe<ChatStateCommand> chatStateActionPipe;

    private String conversationId;

    private boolean typing;

    @Inject
    public ChatStateDelegate(Janet janet) {
        this.chatStateActionPipe = janet.createPipe(ChatStateCommand.class);
    }

    public void init(String conversationId) {
        this.conversationId = conversationId;
    }

    public Observable<String> connectTypingStartAction(Observable<String> inputObservable) {
        return inputObservable
                .skip(1)
                .filter(text -> text.length() > 0)
                .throttleFirst(START_TYPING_DELAY, TimeUnit.MILLISECONDS)
                .filter(text -> !typing)
                .doOnNext(dataConversation -> {
                    typing = true;
                    chatStateActionPipe.send(new ChatStateCommand(conversationId, ChatState.COMPOSING));
                })
                .map(dataConversation -> ChatState.COMPOSING);
    }

    public Observable<String> connectTypingStopAction(Observable<String> inputObservable) {
       return inputObservable
                .skip(1)
                .debounce(STOP_TYPING_DELAY, TimeUnit.MILLISECONDS)
                .doOnNext(dataConversation -> {
                    typing = false;
                    chatStateActionPipe.send(new ChatStateCommand(conversationId, ChatState.PAUSE));
                })
                .map(dataConversation -> ChatState.PAUSE);
    }

}
