package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;

import com.messenger.messengerservers.xmpp.packets.ChatStateExtension;

import org.jivesoftware.smack.packet.Message;

import rx.Observable;

class ChatStateTransformer implements Observable.Transformer<String, Message> {
    private final SendAction<org.jivesoftware.smack.packet.Message> sendAction;

    ChatStateTransformer(@NonNull SendAction<Message> sendAction) {
        this.sendAction = sendAction;
    }

    @Override
    public Observable<Message> call(Observable<String> stringObservable) {
        return stringObservable
                .map(ChatState -> {
                    Message message = new org.jivesoftware.smack.packet.Message();
                    message.addExtension(new ChatStateExtension(ChatState));
                    return message;
                })
                .flatMap(message -> Observable.create(subscriber -> {
                    try {
                        sendAction.call(message);
                        subscriber.onNext(message);
                        subscriber.onCompleted();
                    } catch (Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                }));
    }
}
