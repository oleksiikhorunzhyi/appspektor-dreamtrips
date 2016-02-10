package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.packets.StatusMessagePacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class XmppSingleUserChat extends SingleUserChat implements ConnectionClient {
    private final String companionId;
    private String roomId;

    @Nullable
    private Chat chat;
    private AbstractXMPPConnection connection;
    private XmppServerFacade facade;

    public XmppSingleUserChat(final XmppServerFacade facade, @Nullable String companionId, @Nullable String roomId) {
        this.facade = facade;
        this.companionId = companionId;
        this.roomId = roomId;
        facade.addAuthorizationListener(new ClientConnectionListener(facade, this));
        if (facade.isAuthorized()) {
            setConnection(facade.getConnection());
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
                .doOnError(throwable -> Timber.e(throwable, "setCurrentState %s", state))
                .subscribe();
    }

    @Override
    public Observable<Message> send(Message message) {
        return Observable.just(message)
                .doOnNext(msg -> msg.setConversationId(roomId))
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
                .compose(new StatusMessageTranformer(new StatusMessagePacket(messageId, Status.DISPLAYED,
                        JidCreatorHelper.obtainUserJid(companionId), org.jivesoftware.smack.packet.Message.Type.chat),
                        stanza -> {
                            if (connection != null) {
                                connection.sendStanza(stanza);
                                return true;
                            }
                            return false;
                        }));
    }

    @Override
    public void setConnection(@NonNull AbstractXMPPConnection connection) {
        this.connection = connection;

        String userJid = connection.getUser().split("/")[0];
        String companionJid = null;

        if (companionId != null) {
            companionJid = JidCreatorHelper.obtainUserJid(companionId);
        }
        if (roomId == null) {
            if (companionJid == null) throw new Error();
            roomId = ThreadCreatorHelper.obtainThreadSingleChatFromJids(userJid, companionJid);
        }

        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        Chat existingChat = chatManager.getThreadChat(roomId);

        if (existingChat == null) {
            if (companionJid == null) {
                companionJid = ThreadCreatorHelper.obtainCompanionFromSingleChat(roomId, userJid);
            }
            chat = chatManager.createChat(companionJid, roomId, null);
        } else {
            chat = existingChat;
        }
    }
}
