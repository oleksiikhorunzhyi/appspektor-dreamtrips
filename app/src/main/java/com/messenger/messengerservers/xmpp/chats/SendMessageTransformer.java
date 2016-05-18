package com.messenger.messengerservers.xmpp.chats;

import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.xmpp.XmppGlobalEventEmitter;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;

import java.util.UUID;

import rx.Observable;
import rx.functions.Func1;

class SendMessageTransformer implements Observable.Transformer<Message, Message> {
    private XmppGlobalEventEmitter emitter;
    private final Func1<org.jivesoftware.smack.packet.Message, Observable<Void>> sendAction;
    private final XmppMessageConverter messageConverter;

    public SendMessageTransformer(XmppGlobalEventEmitter emitter, Func1<org.jivesoftware.smack.packet.Message, Observable<Void>> sendAction) {
        messageConverter = new XmppMessageConverter();
        this.emitter = emitter;
        this.sendAction = sendAction;
    }

    @Override
    public Observable<Message> call(Observable<Message> messageObservable) {
        return messageObservable.doOnNext(message -> message.setStatus(MessageStatus.SENDING))
                .doOnNext(message -> {
                    if (message.getId() == null) message.setId(UUID.randomUUID().toString());
                    emitter.interceptPreOutgoingMessages(message);
                })
                .flatMap(message ->
                        sendAction.call(messageConverter.convert(message))
                                .doOnError(throwable -> {
                                    message.setStatus(MessageStatus.ERROR);
                                    emitter.interceptErrorMessage(message);
                                })
                                .doOnNext(aVoid -> {
                                    message.setStatus(MessageStatus.SENT);
                                    emitter.interceptOutgoingMessages(message);
                                })
                                .map(o -> message)
                );
    }
}
