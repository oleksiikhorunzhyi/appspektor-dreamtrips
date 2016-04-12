package com.messenger.messengerservers.xmpp.chats;

import com.messenger.messengerservers.constant.MessageStatus;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.xmpp.XmppGlobalEventEmitter;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;

import java.util.UUID;

import rx.Observable;
import timber.log.Timber;

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
        return messageObservable.doOnNext(message -> message.setStatus(MessageStatus.SENDING))
                .doOnNext(message -> {
                    if (message.getId() == null) message.setId(UUID.randomUUID().toString());
                    emitter.interceptPreOutgoingMessages(message);
                })
                .flatMap(message -> Observable.<Message>create(subscriber -> {
                    try {
                        sendAction.call(messageConverter.convert(message));
                    } catch (Throwable throwable) {
                        Timber.e(throwable, "send message");
                        subscriber.onError(throwable);
                    }
                    message.setStatus(MessageStatus.SENT);
                    subscriber.onNext(message);
                    subscriber.onCompleted();
                }))
                    .doOnNext(emitter::interceptOutgoingMessages);
                }
    }
