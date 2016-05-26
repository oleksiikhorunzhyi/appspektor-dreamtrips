package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;

import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.StatusMessageStanza;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;

import rx.Observable;

public class XmppSingleUserChat extends XmppChat implements SingleUserChat {
    private final String companionId;

    public XmppSingleUserChat(final XmppServerFacade facade, @NonNull String companionId, @NonNull String roomId) {
        super(facade, roomId);
        this.companionId = companionId;
    }

    protected Observable<Chat> provideChatObservable() {
        return facade.getConnectionObservable()
                .map(this::createChat);
    }

    @Override
    protected Observable<Void> trySendSmackMessage(Message message) {
        return provideChatObservable()
                .take(1)
                .flatMap(chat -> Observable.create(subscriber -> {
                    try {
                        chat.sendMessage(message);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    } catch (SmackException.NotConnectedException e) {
                        subscriber.onError(e);
                    }
                }));
    }

    @Override
    protected StatusMessageStanza createStatusMessage(String messageId) {
        return new StatusMessageStanza(messageId, Status.DISPLAYED,
                JidCreatorHelper.obtainUserJid(companionId), Type.chat);
    }

    private Chat createChat(@NonNull XMPPConnection connection) {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        Chat chat = chatManager.getThreadChat(roomId);

        if (chat == null) {
            chat = chatManager.createChat(JidCreatorHelper.obtainUserJid(companionId), roomId, null);
        }

        return chat;
    }

}
