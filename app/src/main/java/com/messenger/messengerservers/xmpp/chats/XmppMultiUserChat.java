package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.packets.ChatStateExtension;
import com.messenger.messengerservers.xmpp.packets.LeavePresence;
import com.messenger.messengerservers.xmpp.packets.StatusMessagePacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class XmppMultiUserChat extends MultiUserChat implements ConnectionClient {

    private static final String TAG = "MultiUserChat";

    @Nullable
    private org.jivesoftware.smackx.muc.MultiUserChat chat;

    private XmppServerFacade facade;
    private final String userId;
    private boolean isOwner;
    private final String roomId;
    private PresenceListener presenceListener;
    private SubjectUpdatedListener subjectUpdatedListener;
    private AbstractXMPPConnection connection;

    public XmppMultiUserChat(XmppServerFacade facade, String roomId, String userId, boolean isOwner) {
        this.facade = facade;
        this.userId = userId;
        this.roomId = roomId;
        this.isOwner = isOwner;

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
    public Observable<Message> send(Message message) {
        return Observable.just(message)
                .doOnNext(msg -> {
                    msg.setFromId(userId);
                    msg.setConversationId(roomId);
                })
                .compose(new SendMessageTransformer(facade.getGlobalEventEmitter(), smackMsg -> {
                    if (chat != null) {
                        chat.sendMessage(smackMsg);
                        return true;
                    }
                    return false;
                }));
    }

    @Override
    public Observable<Message> sendReadStatus(Message message) {
        return Observable.just(message)
                .compose(new StatusMessageTransformer(new StatusMessagePacket(message.getId(), Status.DISPLAYED,
                        JidCreatorHelper.obtainGroupJid(roomId), org.jivesoftware.smack.packet.Message.Type.groupchat),
                        stanza -> {
                            if (connection != null) {
                                connection.sendStanza(stanza);
                                return true;
                            }
                            return false;
                        }));
    }

    @Override
    public void invite(List<User> users) {
        if (!isOwner)
            throw new IllegalAccessError("You are not owner of chat");

        if (!initializedAndConnected()) return;

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
                if (initializedAndConnected()) {
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
            if (!initializedAndConnected()) return;
            chat.join(user.getId());
        } catch (SmackException | XMPPException.XMPPErrorException e) {
            Log.e(TAG, "Error ", e);
        }
    }

    @Override
    public void leave() {
        if (isOwner)
            throw new IllegalAccessError("You are an owner of chat. You cannot leave");

        if (!initializedAndConnected()) return;
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
    public void setCurrentState(@ChatState.State String state) {
        Observable.just(state)
                .subscribeOn(Schedulers.io())
                .compose(new ChatStateTransformer(message -> {
                    if (chat != null) {
                        chat.sendMessage(message);
                        return true;
                    }
                    return false;
                })).subscribe();
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

    private boolean initializedAndConnected(){
        return chat != null && connection != null && connection.isConnected() && connection.isAuthenticated();
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
