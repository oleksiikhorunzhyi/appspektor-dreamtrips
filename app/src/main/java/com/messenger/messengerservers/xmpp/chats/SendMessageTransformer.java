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
        return messageObservable.doOnNext(message -> message.setStatus(Message.Status.SENDING))
                .doOnNext(message -> {
                    message.setId(UUID.randomUUID().toString());
                    emitter.interceptOutgoingMessages(message);
                })
                .flatMap(message -> Observable.<com.messenger.messengerservers.entities.Message>create(subscriber -> {
                    try {
                        org.jivesoftware.smack.packet.Message stanzaPacket = messageConverter.convert(message);

                        message.setStatus(sendAction.call(stanzaPacket) ? Message.Status.SENT : Message.Status.ERROR);
                    } catch (SmackException.NotConnectedException e) {
                        message.setStatus(Message.Status.ERROR);
                        subscriber.onNext(message);
                    } catch (Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                }))
                .doOnNext(emitter::interceptOutgoingMessages);
    }
}
