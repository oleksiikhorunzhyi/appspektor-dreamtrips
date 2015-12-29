package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.Status;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.packets.LeavePresence;
import com.messenger.messengerservers.xmpp.packets.StatusMessagePacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;
import com.worldventures.dreamtrips.modules.trips.model.Schedule;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func0;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class XmppMultiUserChat extends MultiUserChat implements ConnectionClient {

    private static final String TAG = "MultiUserChat";

    @Nullable
    private org.jivesoftware.smackx.muc.MultiUserChat chat;

    private final String userId;
    private boolean isOwner;
    private final String roomId;
    private PresenceListener presenceListener;
    private SubjectUpdatedListener subjectUpdatedListener;
    private AbstractXMPPConnection connection;
    private XmppMessageConverter messageConverter;

    public XmppMultiUserChat(XmppServerFacade facade, String roomId, String userId, boolean isOwner) {
        this.userId = userId;
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
//        chat.addParticipantStatusListener()
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
            stanzaPacket.setFrom(JidCreatorHelper.obtainUserJid(userId));
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
    public Observable<List<User>> kick(List<User> users) {
        if (!isOwner)
            throw new IllegalAccessError("You are not owner of chat. You cannot kick someone");
        return Observable.<List<User>>create(subscriber -> {
            subscriber.onStart();

            try {
                if (chat != null) {
                    chat.revokeMembership(Queryable
                            .from(users)
                            .map(element -> JidCreatorHelper.obtainUserJid(element.getId()))
                            .toList());

                    subscriber.onNext(users);
                }
                subscriber.onCompleted();
            } catch (XMPPException.XMPPErrorException | SmackException.NoResponseException | SmackException.NotConnectedException e) {
                Log.e(TAG, "Error ", e);
                subscriber.onError(e);
            }

        }).subscribeOn(Schedulers.io());
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
//            chat.leave();
            LeavePresence leavePresence = new LeavePresence();
            leavePresence.setTo(chat.getRoom() + "/" + chat.getNickname());
            connection.sendStanza(leavePresence);
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
                    chat.createOrJoin(userId);
                } catch (IllegalStateException e) {
                    Timber.e(e, "SetConnection");
                } // cause the method is synchronized var in library not volatile

                setListeners();
            } catch (XMPPException.XMPPErrorException | SmackException e) {
                Log.e(TAG, "Error ", e);
            }
        }
    }
}
