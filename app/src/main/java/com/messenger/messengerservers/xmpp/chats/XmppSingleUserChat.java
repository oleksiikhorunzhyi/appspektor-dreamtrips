package com.messenger.messengerservers.xmpp.chats;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;

import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;
import com.messenger.messengerservers.xmpp.util.XmppUtils;

public class XmppSingleUserChat extends SingleUserChat {
    private static final String TAG = "SingleUserChat";
    private final ChatStateManager chatStateManager;
    private final AbstractXMPPConnection connection;
    private final Chat chat;

    private final ChatStateListener messageListener = new ChatStateListener() {
        @Override
        public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
            User user = new User(chat.getParticipant());
            handleReceiveMessage(XmppMessageConverter.convert(message), user);
        }

        @Override
        public void stateChanged(Chat chat, org.jivesoftware.smackx.chatstates.ChatState state) {
            handleChangeState(XmppUtils.convertState(state));
        }
    };

    public XmppSingleUserChat(AbstractXMPPConnection connection, User companion) {
        this.connection = connection;
        chatStateManager = ChatStateManager.getInstance(connection);
        String companionJid = JidCreatorHelper.obtainJid(companion);
        String userJid = connection.getUser().split("/")[0];
        String thread = JidCreatorHelper.obtainThreadSingleChat(userJid, companionJid);

        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        Chat existingChat = chatManager.getThreadChat(thread);
        if (existingChat == null){
            chat = chatManager.createChat(companionJid, thread, null);
        } else {
            chat = existingChat;
        }

        chat.addMessageListener(messageListener);
    }

    @Override
    public void setCurrentState(ChatState state) {
        try {
            chatStateManager.setCurrentState(XmppUtils.convertState(state), chat);
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    @Override
    public void sendMessage(Message message) {
        if (!connection.isConnected() || !connection.isAuthenticated())
            throw new IllegalStateException("Your are not authorized");

        try {
            chat.sendMessage(XmppMessageConverter.convert(message));
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "Sending message error", e);
        }
    }

    @Override
    public void close() {
        super.close();
        chat.removeMessageListener(messageListener);
        chat.close();
    }
}
