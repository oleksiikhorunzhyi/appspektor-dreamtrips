package com.messenger.messengerservers.xmpp.chats;

import android.text.TextUtils;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.ProtocolException;
import com.messenger.messengerservers.chat.GroupChat;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.extensions.ChangeAvatarExtension;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.LeavePresence;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.StatusMessageStanza;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class XmppGroupChat extends XmppChat implements GroupChat {
    private Observable<MultiUserChat> chatObservable;

    private final String userId;

    private final Action1<Throwable> defaultOnErrorAction = throwable -> Timber.e(throwable, "");
    private final Action1<Object> defaultOnNextAction = o -> {};
    private final ChatPreconditions chatPreconditions;

    public XmppGroupChat(XmppServerFacade facade, String roomId, boolean isOwner) {
        super(facade, roomId);
        this.userId = facade.getUsername();
        chatPreconditions = new ChatPreconditions(isOwner);
    }

    @Override
    protected void prepareChatObservable(XmppServerFacade facade) {
        chatObservable = facade.getConnectionObservable()
                .map(this::createChat)
                .cacheWithInitialCapacity(1);
    }

    private MultiUserChat createChat(XMPPConnection connection) {
        String jid = JidCreatorHelper.obtainGroupJid(roomId);
        MultiUserChat chat = MultiUserChatManager
                .getInstanceFor(connection).getMultiUserChat(jid);

        if (!chat.isJoined()) {
            try {
                chat.createOrJoin(userId);
            } catch (IllegalStateException | XMPPException.XMPPErrorException | SmackException e) {
                Timber.e(e, "SetConnection");
            }
        }
        return chat;
    }

    @Override
    public Observable<Message> send(Message message) {
        message.setFromId(userId);
        return super.send(message);
    }

    @Override
    protected Observable<Void> trySendSmackMessage(org.jivesoftware.smack.packet.Message message) {
        return chatActionObservable(chat -> chat.sendMessage(message));
    }

    @Override
    protected StatusMessageStanza createStatusMessage(String messageId) {
        return new StatusMessageStanza(messageId, Status.DISPLAYED,
                JidCreatorHelper.obtainGroupJid(roomId), org.jivesoftware.smack.packet.Message.Type.groupchat);
    }

    @Override
    public void invite(List<String> usersId) {
        chatPreconditions.checkUserIsOwner();

        chatActionObservable(chat -> {
            for (String user : usersId) {
                chat.invite(JidCreatorHelper.obtainUserJid(user), null);
            }
        }).subscribe(defaultOnNextAction, defaultOnErrorAction);
    }

    @Override
    public Observable<List<String>> kick(List<String> userIds) {
        chatPreconditions.checkUserIsOwner();

        return chatActionObservable(chat -> {
            List<String> jids = Queryable.from(userIds).map(JidCreatorHelper::obtainUserJid).toList();
            chat.revokeMembership(jids);
        }).map(aVoid -> userIds);
    }

    @Override
    public void join(String userId) {
        chatActionObservable(chat -> chat.join(userId))
                .subscribe(defaultOnNextAction, defaultOnErrorAction);
    }

    @Override
    public void leave() {
        chatPreconditions.checkUserIsNotOwner();

        facade.getConnectionObservable()
                .take(1).withLatestFrom(chatObservable.take(1), Pair::new)
                .flatMap(connectionWithChat -> Observable.create(subscriber ->
                        sendLeaveStanza(connectionWithChat.first, connectionWithChat.second, subscriber))
                )
                .subscribeOn(Schedulers.io())
                .subscribe(defaultOnNextAction, defaultOnErrorAction);
    }

    private void sendLeaveStanza(XMPPConnection connection, MultiUserChat chat, Subscriber subscriber) {
        try {
            LeavePresence leavePresence = new LeavePresence();
            leavePresence.setTo(chat.getRoom() + "/" + chat.getNickname());
            connection.sendStanza(leavePresence);
            subscriber.onCompleted();
        } catch (SmackException.NotConnectedException e) {
            subscriber.onError(new ConnectionException(e));
        }
    }


    @Override
    public Observable<GroupChat> setSubject(String subject) {
        chatPreconditions.checkUserIsOwner();

        return chatActionObservable(chat -> {
            if (!TextUtils.isEmpty(subject) && TextUtils.getTrimmedLength(subject) > 0) {
                chat.changeSubject(subject);
            }
        }).map(aVoid -> XmppGroupChat.this);
    }

    @Override
    public Observable<GroupChat> setAvatar(String avatar) {
        chatPreconditions.checkUserIsOwner();

        return chatActionObservable(chat -> {
            org.jivesoftware.smack.packet.Message message
                    = new org.jivesoftware.smack.packet.Message();
            message.addExtension(new ChangeAvatarExtension(avatar));
            chat.sendMessage(message);
        }).map(aVoid -> XmppGroupChat.this);
    }


    private Observable<Void> chatActionObservable(ChatAction chatAction) {
        return chatObservable
                .take(1)
                .flatMap(chat -> Observable.create(subscriber -> {
                    try {
                        chatAction.call(chat);
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    } catch (SmackException.NotConnectedException e) {
                        subscriber.onError(new ConnectionException(e));
                    } catch (XMPPException.XMPPErrorException | SmackException e) {
                        subscriber.onError(new ProtocolException(e));
                    }
                }));
    }

    private interface ChatAction {

        void call(MultiUserChat chat) throws XMPPException.XMPPErrorException, SmackException;
    }
}
