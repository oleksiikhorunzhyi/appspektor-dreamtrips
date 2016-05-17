package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;

import com.messenger.messengerservers.xmpp.extensions.ChatStateExtension;

import org.jivesoftware.smack.packet.Message;

import rx.Observable;
import rx.functions.Func1;

class ChatStateTransformer implements Observable.Transformer<String, Void> {
    private final Func1<Message, Observable<Void>> sendAction;

    ChatStateTransformer(@NonNull Func1<Message, Observable<Void>> sendAction) {
        this.sendAction = sendAction;
    }

    @Override
    public Observable<Void> call(Observable<String> stringObservable) {
        return stringObservable
                .map(chatState -> {
                    Message message = new org.jivesoftware.smack.packet.Message();
                    message.addExtension(new ChatStateExtension(chatState));
                    return message;
                })
                .flatMap(sendAction::call);
    }
}
