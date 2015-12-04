package com.messenger.messengerservers.xmpp.chats;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.util.List;

import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;

public class XmppMultiUserChat extends MultiUserChat {
    private static final String TAG = "MultiUserChat";
    private final AbstractXMPPConnection connection;
    private final org.jivesoftware.smackx.muc.MultiUserChat chat;

    public XmppMultiUserChat(AbstractXMPPConnection connection, User user) {
        this.connection = connection;
        // TODO: 11/27/15 create jid
        chat = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(JidCreatorHelper.obtainGroupJid(user));

        try {
            chat.join("TEST_ROOM");
        } catch (XMPPException.XMPPErrorException | SmackException e) {
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
            Log.e(TAG, "Error ", e);
        }
    }

    @Override
    public void setCurrentState(ChatState state) {
    }

    @Override
    public void leave() {
        try {
            chat.leave();
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    @Override
    public void join(User user) {
        try {
            chat.join(user.getUserName());
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    @Override
    public void invite(List<User> users) {
        try {
            for (User user : users) {
                chat.invite(JidCreatorHelper.obtainJid(user), "Invite");
            }
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "Error ", e);
        }
    }
}
