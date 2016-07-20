package com.messenger.messengerservers.xmpp.chats;

import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.chat.ChatState;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.StatusMessageStanza;

import rx.Observable;
import rx.schedulers.Schedulers;

public abstract class XmppChat implements Chat {

    protected final XmppServerFacade facade;
    protected final String roomId;

    public XmppChat(XmppServerFacade facade, String roomId) {
        this.facade = facade;
        this.roomId = roomId;
    }

    @Override
    public Observable<Message> send(Message message) {
        return Observable.just(message)
                .doOnNext(msg -> msg.setConversationId(roomId))
                .compose(new SendMessageTransformer(facade.getGlobalEventEmitter(),
                        this::trySendSmackMessage));

    }

    @Override
    public Observable<String> setCurrentState(@ChatState.State String state) {
        return Observable.just(state)
                .subscribeOn(Schedulers.io())
                .compose(new ChatStateTransformer(this::trySendSmackMessage))
                .map(message -> state);
    }

    protected abstract Observable<Void> trySendSmackMessage(org.jivesoftware.smack.packet.Message message);

    @Override
    public Observable<String> sendReadStatus(String messageId) {
        return facade.getConnectionObservable()
                .take(1)
                .compose(new StatusMessageTransformer(createStatusMessage(messageId)));
    }

    protected abstract StatusMessageStanza createStatusMessage(String messageId);
}
