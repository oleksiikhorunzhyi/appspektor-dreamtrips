package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.ProtocolException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;

import rx.Observable;
import rx.Subscriber;

public class XmppSendWithResponseObservable implements Observable.OnSubscribe<Stanza> {

    private final XMPPConnection connection;
    private final IQ iq;

    public static Observable<Stanza> send(XMPPConnection connection, IQ iq) {
        return Observable.create(new XmppSendWithResponseObservable(connection, iq));
    }

    private XmppSendWithResponseObservable(XMPPConnection connection, IQ iq) {
        this.connection = connection;
        this.iq = iq;
    }

    @Override
    public void call(Subscriber<? super Stanza> subscriber) {
        try {
            connection
                    .sendIqWithResponseCallback(iq, packet -> {
                        subscriber.onNext(packet);
                        subscriber.onCompleted();
                    }, exception -> subscriber.onError(new ProtocolException(exception)));
        } catch (SmackException.NotConnectedException e) {
            subscriber.onError(new ConnectionException(e));
        }
    }
}
