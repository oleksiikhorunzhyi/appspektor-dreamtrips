package com.messenger.messengerservers.xmpp.chats;

import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.xmpp.packets.StatusMessagePacket;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Stanza;

import rx.Observable;

class StatusMessageTransformer implements Observable.Transformer<String, String> {
    private final StatusMessagePacket statusMessagePacket;
    private final SendAction<Stanza> sendAction;

    StatusMessageTransformer(StatusMessagePacket statusMessagePacket, SendAction<Stanza> sendAction) {
        this.statusMessagePacket = statusMessagePacket;
        this.sendAction = sendAction;
    }

    @Override
    public Observable<String> call(Observable<String> messageIdObservable) {
        return messageIdObservable
                .flatMap(message -> Observable.<String>create(subscriber -> {
                    subscriber.onStart();
                    try {
                        sendAction.call(statusMessagePacket);
                        subscriber.onNext(message);
                        subscriber.onCompleted();
                    } catch (SmackException.NotConnectedException e) {
                        subscriber.onError(new ConnectionException(e));
                    } catch (Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                }));
    }
}
