package com.messenger.messengerservers.xmpp.chats;

import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.xmpp.stanzas.StatusMessageStanza;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Stanza;

import rx.Observable;

class StatusMessageTransformer implements Observable.Transformer<String, String> {
    private final StatusMessageStanza statusMessageStanza;
    private final SendAction<Stanza> sendAction;

    StatusMessageTransformer(StatusMessageStanza statusMessageStanza, SendAction<Stanza> sendAction) {
        this.statusMessageStanza = statusMessageStanza;
        this.sendAction = sendAction;
    }

    @Override
    public Observable<String> call(Observable<String> messageIdObservable) {
        return messageIdObservable
                .flatMap(message -> Observable.<String>create(subscriber -> {
                    subscriber.onStart();
                    try {
                        sendAction.call(statusMessageStanza);
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
