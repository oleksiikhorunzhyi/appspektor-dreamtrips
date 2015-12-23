package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;
import com.messenger.messengerservers.xmpp.util.XmppUtils;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;

import java.util.UUID;

import timber.log.Timber;


public class XmppSingleUserChat extends SingleUserChat implements ConnectionClient {

    private static final String TAG = "SingleUserChat";
    private final String companionId;
    private String thread;

    @Nullable
    private ChatStateManager chatStateManager;
    @Nullable
    private Chat chat;
    private AbstractXMPPConnection connection;
    private XmppMessageConverter messageConverter;

    private final ChatStateListener messageListener = new ChatStateListener() {
        @Override
        public void stateChanged(Chat chat, org.jivesoftware.smackx.chatstates.ChatState state) {
            handleChangeState(XmppUtils.convertState(state), companionId);
        }

        @Override
        public void processMessage(Chat chat, Message message) {
            User user = new User(chat.getParticipant());
            handleReceiveMessage(message.getBody(), user);
        }
    };

    public XmppSingleUserChat(final XmppServerFacade facade, @Nullable String companionId, @Nullable String thread) {
        this.companionId = companionId;
        this.thread = thread;
        //
        messageConverter = new XmppMessageConverter();
        facade.addAuthorizationListener(new ClientConnectionListener(facade, this));
        if (facade.isAuthorized()) {
            setConnection(facade.getConnection());
        }
    }

    @Override
    public void setCurrentState(ChatState state) {
        if (chatStateManager == null) return;
        try {
            chatStateManager.setCurrentState(XmppUtils.convertState(state), chat);
        } catch (SmackException.NotConnectedException e) {
            Timber.e(TAG, e);
        }
    }

    @Override
    public void sendMessage(com.messenger.messengerservers.entities.Message message) {
        if (connection == null || !connection.isConnected() || !connection.isAuthenticated())
            throw new IllegalStateException("Your are not authorized");

        try {
            Message stanzaPacket = messageConverter.convert(message);
            stanzaPacket.setStanzaId(UUID.randomUUID().toString());
            chat.sendMessage(stanzaPacket);
        } catch (SmackException.NotConnectedException e) {
            Timber.e(TAG, e);
        }
    }

    @Override
    public void close() {
        super.close();
        if (chat == null) return;
        chat.removeMessageListener(messageListener);
    }

    @Override
    public void setConnection(@NonNull AbstractXMPPConnection connection) {
        this.connection = connection;
        chatStateManager = ChatStateManager.getInstance(connection);

        String userJid = connection.getUser().split("/")[0];
        String companionJid = null;

        if (companionId != null) {
            companionJid = JidCreatorHelper.obtainUserJid(companionId);
        }
        if (thread == null) {
            if (companionJid == null) throw new Error();
            thread = ThreadCreatorHelper.obtainThreadSingleChat(userJid, companionJid);
        }

        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        Chat existingChat = chatManager.getThreadChat(thread);

        if (existingChat == null) {
            if (companionJid == null) {
                companionJid = JidCreatorHelper
                        .obtainUserJid(
                                thread
                                        .replace(userJid.split("@")[0], "")
                                        .replace("_", "")
                                                //// TODO: 12/15/15  remove after implemented social graph
                                        .replace("yu", "y_u"));
            }
            chat = chatManager.createChat(companionJid, thread, null);
        } else {
            chat = existingChat;
        }

        chat.addMessageListener(messageListener);
    }
}
