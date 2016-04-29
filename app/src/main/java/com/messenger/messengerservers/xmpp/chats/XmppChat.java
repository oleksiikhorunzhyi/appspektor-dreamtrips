package com.messenger.messengerservers.xmpp.chats;

import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.chat.Chat;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.StatusMessageStanza;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;

import rx.Observable;
import rx.schedulers.Schedulers;

public abstract class XmppChat implements Chat {

    protected final XmppServerFacade facade;
    protected String roomId;

    public XmppChat(XmppServerFacade facade, String roomId) {
        this.facade = facade;
        this.roomId = roomId;
    }

    protected void connectToFacade(){
        synchronized (this.facade) {
            if (facade.isAuthorized()) {
                setConnection(facade.getConnection());
            }
            facade.addAuthorizationListener(authorizeListener);
        }
    }

    protected abstract void setConnection(AbstractXMPPConnection connection);

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

    protected abstract void trySendSmackMessage(org.jivesoftware.smack.packet.Message message) throws SmackException.NotConnectedException;

    @Override
    public Observable<String> sendReadStatus(String messageId) {
        return Observable.just(messageId)
                .compose(new StatusMessageTransformer(createStatusMessage(messageId),
                        stanza -> {
                            AbstractXMPPConnection connection = facade.getConnection();
                            if (connection != null) {
                                connection.sendStanza(stanza);
                            } else {
                                throw new ConnectionException();
                            }
                        }));
    }

    protected abstract StatusMessageStanza createStatusMessage(String messageId);

    @Override
    public void close() {
        facade.removeAuthorizationListener(authorizeListener);
    }

    //////////////////////////////////////////////////////
    ////// Listeners
    //////////////////////////////////////////////////////

    private AuthorizeListener authorizeListener = new AuthorizeListener() {
        @Override
        public void onSuccess() {
            super.onSuccess();
            setConnection(facade.getConnection());
        }
    };
}
