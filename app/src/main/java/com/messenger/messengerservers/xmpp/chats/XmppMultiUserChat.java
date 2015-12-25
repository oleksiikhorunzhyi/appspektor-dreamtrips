package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.Status;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.packets.StatusMessagePacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class XmppMultiUserChat extends MultiUserChat implements ConnectionClient {

    private static final String TAG = "MultiUserChat";

    @Nullable
    private org.jivesoftware.smackx.muc.MultiUserChat chat;

    private final User user;
    private boolean isOwner;
    private final String roomId;
    private PresenceListener presenceListener;
    private SubjectUpdatedListener subjectUpdatedListener;
    private AbstractXMPPConnection connection;
    private XmppMessageConverter messageConverter;

    public XmppMultiUserChat(XmppServerFacade facade, String roomId, User user, boolean isOwner) {
        this.user = user;
        this.roomId = roomId;
        this.isOwner = isOwner;
        //
        messageConverter = new XmppMessageConverter();
        facade.addAuthorizationListener(new ClientConnectionListener(facade, this));
        if (facade.isAuthorized()) {
            setConnection(facade.getConnection());
        }
    }

    @SuppressWarnings("all")
    private void setListeners() {
        // TODO: 12/11/15 add future implementation
        presenceListener = presence -> notifyParticipantsChanged(new ArrayList<>());
        chat.addParticipantListener(presenceListener);

        subjectUpdatedListener = (subject, from) -> notifySubjectUpdateListeners(subject);
        chat.addSubjectUpdatedListener(subjectUpdatedListener);
        org.jivesoftware.smack.packet.Message packet;
    }

    @Override
    public void sendMessage(Message message) {
        if (chat == null || connection == null || !connection.isConnected() || !connection.isAuthenticated())
            throw new IllegalStateException("Your are not authorized");

        try {
            org.jivesoftware.smack.packet.Message stanzaPacket = messageConverter.convert(message);
            stanzaPacket.setStanzaId(UUID.randomUUID().toString());
            stanzaPacket.setThread(roomId);
            stanzaPacket.setFrom(JidCreatorHelper.obtainUserJid(user.getId()));
            chat.sendMessage(stanzaPacket);
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    @Override
    public void changeMessageStatus(com.messenger.messengerservers.entities.Message message, @Status.MessageStatus String status) {
        StatusMessagePacket statusMessagePacket = new StatusMessagePacket(message.getId(), status,
                JidCreatorHelper.obtainGroupJid(roomId), org.jivesoftware.smack.packet.Message.Type.groupchat);
        try {
            Log.e("Stanza send", statusMessagePacket.toString());
            connection.sendStanza(statusMessagePacket);
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e, TAG);
        }
    }

    @Override
    public void invite(List<User> users) {
        if (!isOwner)
            throw new IllegalAccessError("You are not owner of chat");

        if (chat == null) return;
        try {
            for (User user : users) {
                chat.invite(JidCreatorHelper.obtainUserJid(user.getId()), null);
            }
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    @Override
    public void kick(List<User> users) {
        if (!isOwner)
            throw new IllegalAccessError("You are not owner of chat. You cannot kick someone");

        for (User user : users) {
            if (this.user.getId().equals(user.getId())) continue;

            try {
                chat.kickParticipant(JidCreatorHelper.obtainUserJid(user.getId()), null);
            } catch (XMPPException.XMPPErrorException | SmackException e) {
                Log.e(TAG, "Error ", e);
            }
        }
    }

    @Override
    public void join(User user) {
        try {
            if (chat == null) return;
            chat.join(user.getId());
        } catch (SmackException | XMPPException.XMPPErrorException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    @Override
    public void leave() {
        if (isOwner)
            throw new IllegalAccessError("You are an owner of chat. You cannot leave");

        if (chat == null) return;
        try {
            chat.leave();
        } catch (SmackException.NotConnectedException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    @Override
    public void setCurrentState(ChatState state) {
    }

    @Override
    public void setSubject(String subject) {
        if (!isOwner)
            throw new IllegalAccessError("You are not owner of chat");

        if (chat == null) return;

        try {
            chat.changeSubject(subject);
        } catch (XMPPException.XMPPErrorException | SmackException e) {
            Log.e(TAG, "Xmpp Error", e);
        }
    }

    @Override
    public void close() {
        super.close();
        if (chat == null) return;
        chat.removeParticipantListener(presenceListener);
        chat.removeSubjectUpdatedListener(subjectUpdatedListener);
    }

    @Override
    public void setConnection(@NonNull AbstractXMPPConnection connection) {
        this.connection = connection;
        String jid = JidCreatorHelper.obtainGroupJid(roomId);
        chat = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(jid);

        if (!chat.isJoined()) {
            try {
                try {
                    Log.e("MUC Create ", jid);
                    chat.createOrJoin(user.getId());
                } catch (IllegalStateException e) {
                } // cause the method is synchronized var in library not volatile

                setListeners();
            } catch (XMPPException.XMPPErrorException | SmackException e) {
                Log.e(TAG, "Error ", e);
            }
        }
    }
}
