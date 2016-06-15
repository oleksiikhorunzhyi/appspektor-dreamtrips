package com.messenger.messengerservers.xmpp.chats;

import android.util.Pair;

import com.messenger.messengerservers.ConnectionException;
import com.messenger.messengerservers.ProtocolException;
import com.messenger.messengerservers.chat.AccessConversationDeniedException;
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
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import timber.log.Timber;

public class XmppGroupChat extends XmppChat implements GroupChat {

    private final String userId;
    private final ChatPreconditions chatPreconditions;

    private final Action1<Throwable> defaultOnErrorAction = throwable -> Timber.e(throwable, "");
    private final Action1<Object> defaultOnNextAction = o -> {};

    public XmppGroupChat(XmppServerFacade facade, String roomId, boolean isOwner) {
        super(facade, roomId);
        this.userId = facade.getUsername();
        chatPreconditions = new ChatPreconditions(isOwner);
    }

    protected Observable<MultiUserChat> provideChatObservable() {
        return facade.getConnectionObservable()
                .flatMap(connection ->
                        Observable.fromCallable(() -> createChat(connection)));
    }

    private MultiUserChat createChat(XMPPConnection connection)
            throws ProtocolException, AccessConversationDeniedException {
        String jid = JidCreatorHelper.obtainGroupJid(roomId);
        MultiUserChat chat = MultiUserChatManager
                .getInstanceFor(connection).getMultiUserChat(jid);

        if (chat.isJoined()) return chat;

        try {
            chat.createOrJoin(userId);
        } catch (XMPPException.XMPPErrorException e) {
            XMPPError error = e.getXMPPError();
            if (error != null && error.getType() == XMPPError.Type.AUTH) {
                throw new AccessConversationDeniedException();
            } else {
                throw new ProtocolException(e);
            }
        } catch (SmackException e) {
            throw new ProtocolException(e);
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
    public Observable<GroupChat> kick(String userId) {
        chatPreconditions.checkUserIsOwner();

        return chatActionObservable(chat -> {
            String jid = JidCreatorHelper.obtainUserJid(userId);
            chat.revokeMembership(jid);
        }).map(aVoid -> this);
    }

    @Override
    public void join(String userId) {
        chatActionObservable(chat -> chat.join(userId))
                .subscribe(defaultOnNextAction, defaultOnErrorAction);
    }

    @Override
    public Observable<GroupChat> leave() {
        chatPreconditions.checkUserIsNotOwner();

        return facade.getConnectionObservable()
                .take(1)
                .withLatestFrom(provideChatObservable().take(1), Pair::new)
                .flatMap(connectionWithChat -> Observable.create(new Observable.OnSubscribe<GroupChat>() {
                    @Override
                    public void call(Subscriber<? super GroupChat> subscriber) {
                        sendLeaveStanza(connectionWithChat.first, connectionWithChat.second, subscriber);
                    }
                }));
    }

    private void sendLeaveStanza(XMPPConnection connection, MultiUserChat chat, Subscriber<? super GroupChat> subscriber) {
        LeavePresence leavePresence = new LeavePresence();
        leavePresence.setTo(chat.getRoom() + "/" + chat.getNickname());

        try {
            connection.sendStanza(leavePresence);
            subscriber.onNext(this);
            subscriber.onCompleted();
        } catch (SmackException.NotConnectedException e) {
            subscriber.onError(new ConnectionException(e));
        }
    }

    @Override
    public Observable<GroupChat> setSubject(String subject) {
        chatPreconditions.checkUserIsOwner();

        return chatActionObservable(chat -> chat.changeSubject(subject))
                .map(aVoid -> XmppGroupChat.this);
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
        return provideChatObservable().take(1)
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
