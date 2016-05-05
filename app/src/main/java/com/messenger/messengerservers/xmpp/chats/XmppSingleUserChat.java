package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.StatusMessageStanza;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;

import rx.Observable;

public class XmppSingleUserChat extends XmppChat implements SingleUserChat {
    private Observable<Chat> chatObservable;

    private final String companionId;

    public XmppSingleUserChat(final XmppServerFacade facade, @Nullable String companionId, @Nullable String roomId) {
        super(facade, roomId);
        this.companionId = companionId;
    }

    @Override
    protected void prepareChatObservable(XmppServerFacade facade) {
        chatObservable = facade.getConnectionObservable()
                .map(this::createChat)
                .cacheWithInitialCapacity(1);
    }

    @Override
    protected Observable<Void> trySendSmackMessage(Message message) {
        return chatObservable
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
        String userJid = facade.getUsername();
        String companionJid = null;

        if (companionId != null) {
            companionJid = JidCreatorHelper.obtainUserJid(companionId);
        }
        if (roomId == null) {
            if (companionJid == null) throw new Error();
            roomId = ThreadCreatorHelper.obtainThreadSingleChatFromJids(userJid, companionJid);
        }

        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        Chat chat = chatManager.getThreadChat(roomId);

        if (chat == null) {
            if (companionJid == null) {
                companionJid = ThreadCreatorHelper.obtainCompanionFromSingleChat(roomId, userJid);
            }
            chat = chatManager.createChat(companionJid, roomId, null);
        }

        return chat;
    }

}
