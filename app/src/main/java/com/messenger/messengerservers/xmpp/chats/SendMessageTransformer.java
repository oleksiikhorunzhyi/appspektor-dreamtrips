package com.messenger.messengerservers.xmpp.chats;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.xmpp.XmppGlobalEventEmitter;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;

import org.jivesoftware.smack.SmackException;

import java.util.UUID;

import rx.Observable;

class SendMessageTransformer implements Observable.Transformer<Message, Message> {
    private XmppGlobalEventEmitter emitter;
    private final SendAction<org.jivesoftware.smack.packet.Message> sendAction;
    private final XmppMessageConverter messageConverter;

    public SendMessageTransformer(XmppGlobalEventEmitter emitter, SendAction<org.jivesoftware.smack.packet.Message> sendAction) {
        messageConverter = new XmppMessageConverter();
        this.emitter = emitter;
        this.sendAction = sendAction;
    }

    @Override
    public Observable<Message> call(Observable<Message> messageObservable) {
        return messageObservable.doOnNext(message1 -> message1.setStatus(Message.Status.SENDING))
                .doOnNext(emitter::interceptOutgoingMessages)
                .flatMap(message1 -> Observable.<com.messenger.messengerservers.entities.Message>create(subscriber -> {
                    try {
                        subscriber.onStart();

                        org.jivesoftware.smack.packet.Message stanzaPacket = messageConverter.convert(message1);
                        stanzaPacket.setStanzaId(UUID.randomUUID().toString());
                        sendAction.call(stanzaPacket);
                        message1.setStatus(Message.Status.SENT);
                        subscriber.onNext(message1);
                    } catch (SmackException.NotConnectedException e) {
                        message1.setStatus(Message.Status.ERROR);
                        subscriber.onNext(message1);
                    } catch (Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                    subscriber.onCompleted();
                }))
                .doOnNext(emitter::interceptOutgoingMessages);
    }
}
