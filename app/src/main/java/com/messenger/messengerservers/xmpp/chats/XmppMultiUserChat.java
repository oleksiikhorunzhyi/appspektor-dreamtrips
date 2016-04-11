package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.packets.ChangeAvatarExtension;
import com.messenger.messengerservers.xmpp.packets.LeavePresence;
import com.messenger.messengerservers.xmpp.packets.StatusMessagePacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class XmppMultiUserChat extends XmppChat implements MultiUserChat {
    @Nullable
    private org.jivesoftware.smackx.muc.MultiUserChat chat;

    private String userId;
    private boolean isOwner;

    public XmppMultiUserChat(XmppServerFacade facade, String roomId, String userId, boolean isOwner) {
        super(facade, roomId);
        this.isOwner = isOwner;
        this.userId = userId;
        connectToFacade();
    }

    public void setConnection(@NonNull AbstractXMPPConnection connection) {
        String jid = JidCreatorHelper.obtainGroupJid(roomId);
        chat = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(jid);

        if (!chat.isJoined()) {
            try {
                chat.createOrJoin(userId);
            } catch (IllegalStateException | XMPPException.XMPPErrorException | SmackException e) {
                Timber.e(e, "SetConnection");
            }
        }
    }

    @Override
    public Observable<Message> send(Message message) {
        message.setFromId(userId);
        return super.send(message);
    }

    @Override
    protected void trySendSmackMessage(org.jivesoftware.smack.packet.Message message) throws SmackException.NotConnectedException {
        if (chat == null) throw new SmackException.NotConnectedException();

        chat.sendMessage(message);
    }

    @Override
    protected StatusMessagePacket createStatusMessage(String messageId) {
        return new StatusMessagePacket(messageId, Status.DISPLAYED,
                JidCreatorHelper.obtainGroupJid(roomId), org.jivesoftware.smack.packet.Message.Type.groupchat);
    }

    @Override
    public void invite(List<String> usersId) {
        if (!isOwner)
            throw new IllegalAccessError("You are not owner of chat");

        if (!initializedAndConnected()) return;

        try {
            for (String user : usersId) {
                chat.invite(JidCreatorHelper.obtainUserJid(user), null);
            }
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e, "Error when invite users");
        }
    }

    @Override
    public Observable<List<String>> kick(List<String> userIds) {
        if (!isOwner)
            throw new IllegalAccessError("You are not owner of chat. You cannot kick someone");
        return Observable.<List<String>>create(subscriber -> {
            subscriber.onStart();

            try {
                if (initializedAndConnected()) {
                    List <String> jids = Queryable.from(userIds).map(id -> JidCreatorHelper.obtainUserJid(id)).toList();
                    chat.revokeMembership(jids);
                    subscriber.onNext(userIds);
                }
                subscriber.onCompleted();
            } catch (XMPPException.XMPPErrorException | SmackException.NoResponseException | SmackException.NotConnectedException e) {
                Timber.e(e, "Error");
                subscriber.onError(e);
            }

        }).subscribeOn(Schedulers.io());
    }

    @Override
    public void join(String userId) {
        try {
            if (!initializedAndConnected()) return;
            chat.join(userId);
        } catch (SmackException | XMPPException.XMPPErrorException e) {
            Timber.e(e, "Error");
        }
    }

    @Override
    public void leave() {
        if (isOwner)
            throw new IllegalAccessError("You are an owner of chat. You cannot leave");

        if (!initializedAndConnected()) return;
        try {
            LeavePresence leavePresence = new LeavePresence();
            leavePresence.setTo(chat.getRoom() + "/" + chat.getNickname());
            facade.getConnection().sendStanza(leavePresence);
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e, "Error");
        }
    }

    @Override
    public Observable<MultiUserChat> setSubject(String subject) {
        if (!isOwner)
            throw new IllegalAccessError("You are not owner of chat");

        return Observable.create(new Observable.OnSubscribe<MultiUserChat>() {
            @Override
            public void call(Subscriber<? super MultiUserChat> subscriber) {
                subscriber.onStart();
                if (!initializedAndConnected()) {
                    subscriber.onError(new ConnectionException());
                    return;
                }

                try {
                    if (!TextUtils.isEmpty(subject) && TextUtils.getTrimmedLength(subject) > 0) {
                        chat.changeSubject(subject);
                    }
                    subscriber.onNext(XmppMultiUserChat.this);
                    subscriber.onCompleted();
                } catch (XMPPException.XMPPErrorException | SmackException e) {
                    // TODO: 1/5/16 implement exception wrapper
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<MultiUserChat> setAvatar(String avatar) {
        // TODO March 17, 2016 Extract observable creation, preconditions and error processing logic
        // to common place
        if (!isOwner)
            throw new IllegalAccessError("You are not owner of chat");

        return Observable.create(subscriber -> {
            subscriber.onStart();
            if (!initializedAndConnected()) {
                subscriber.onError(new ConnectionException());
                return;
            }

            try {
                org.jivesoftware.smack.packet.Message message
                        = new org.jivesoftware.smack.packet.Message();
                message.addExtension(new ChangeAvatarExtension(avatar));
                chat.sendMessage(message);

                subscriber.onNext(XmppMultiUserChat.this);
                subscriber.onCompleted();
            } catch (SmackException e) {
                // TODO: 1/5/16 implement exception wrapper
                subscriber.onError(e);
            }
        });
    }

    private boolean initializedAndConnected(){
        AbstractXMPPConnection connection = facade.getConnection();
        return chat != null && connection != null && connection.isConnected() && connection.isAuthenticated();
    }
}
