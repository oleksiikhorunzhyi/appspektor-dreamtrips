package com.messenger.messengerservers.xmpp.chats;

import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.xmpp.packets.StatusMessagePacket;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Stanza;

import rx.Observable;

class StatusMessageTranformer implements Observable.Transformer<Message, Message> {
    private final StatusMessagePacket statusMessagePacket;
    private final SendAction<Stanza> sendAction;

    StatusMessageTranformer(StatusMessagePacket statusMessagePacket, SendAction<Stanza> sendAction) {
        this.statusMessagePacket = statusMessagePacket;
        this.sendAction = sendAction;
    }

    @Override
    public Observable<Message> call(Observable<Message> messageObservable) {
        return messageObservable
                .flatMap(message -> Observable.<Message>create(subscriber -> {
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
