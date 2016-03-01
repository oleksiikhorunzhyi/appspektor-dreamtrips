package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
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

public class XmppMultiUserChat extends MultiUserChat {
    @Nullable
    private org.jivesoftware.smackx.muc.MultiUserChat chat;

    private final XmppServerFacade facade;
    private final String userId;
    private boolean isOwner;
    private final String roomId;
    private AbstractXMPPConnection connection;


    private AuthorizeListener authorizeListener = new AuthorizeListener() {
        @Override
        public void onSuccess() {
            super.onSuccess();
            setConnection(facade.getConnection());
        }
    };

    public XmppMultiUserChat(XmppServerFacade facade, String roomId, String userId, boolean isOwner) {
        this.facade = facade;
        this.userId = userId;
        this.roomId = roomId;
        this.isOwner = isOwner;

        synchronized (this.facade) {
            if (facade.isAuthorized()) {
                setConnection(facade.getConnection());
            }
            facade.addAuthorizationListener(authorizeListener);
        }
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
    public Observable<String> sendReadStatus(String messageId) {
        return Observable.just(messageId)
                .compose(new StatusMessageTransformer(new StatusMessagePacket(messageId, Status.DISPLAYED,
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
                    chat.revokeMembership(Queryable
                            .from(userIds)
                            .map(id -> JidCreatorHelper.obtainUserJid(id))
                            .toList());

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
            connection.sendStanza(leavePresence);
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e, "Error");
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
                }))
                .subscribe(message -> {}, throwable -> Timber.e(throwable, "setCurrentState %s", state));
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
            } catch (XMPPException.XMPPErrorException | SmackException e) {
                Timber.e(e, "Error");
            }
        }
    }

    @Override
    public void close() {
        super.close();
        facade.removeAuthorizationListener(authorizeListener);
    }
}
