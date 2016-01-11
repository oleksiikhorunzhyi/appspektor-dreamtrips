package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.messenger.messengerservers.ChatState;
import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.packets.StatusMessagePacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppUtils;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;

import rx.Observable;
import timber.log.Timber;

public class XmppSingleUserChat extends SingleUserChat implements ConnectionClient {

    private static final String TAG = "SingleUserChat";
    private final String companionId;
    private String roomId;

    @Nullable
    private ChatStateManager chatStateManager;
    @Nullable
    private Chat chat;
    private AbstractXMPPConnection connection;
    private XmppServerFacade facade;

    private final ChatStateListener messageListener = new ChatStateListener() {
        @Override
        public void stateChanged(Chat chat, org.jivesoftware.smackx.chatstates.ChatState state) {
            handleChangeState(XmppUtils.convertState(state), companionId);
        }

        @Override
        public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
        }
    };

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
    public void setCurrentState(ChatState state) {
        if (chatStateManager == null) return;
        try {
            chatStateManager.setCurrentState(XmppUtils.convertState(state), chat);
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e, "Send status error");
        }
    }

    @Override
    public Observable<com.messenger.messengerservers.entities.Message> send(com.messenger.messengerservers.entities.Message message) {
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
    public Observable<Message> sendReadStatus(Message message) {
        return Observable.just(message)
                .compose(new StatusMessageTranformer(new StatusMessagePacket(message.getId(), Status.DISPLAYED,
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
    public void close() {
        super.close();
        if (chat == null) return;
        chat.removeMessageListener(messageListener);
    }

    @Override
    public void setConnection(@NonNull AbstractXMPPConnection connection) {
        this.connection = connection;
        chatStateManager = ChatStateManager.getInstance(connection);

        String userJid = connection.getUser().split("/")[0];
        String companionJid = null;

        if (companionId != null) {
            companionJid = JidCreatorHelper.obtainUserJid(companionId);
        }
        if (roomId == null) {
            if (companionJid == null) throw new Error();
            roomId = ThreadCreatorHelper.obtainThreadSingleChat(userJid, companionJid);
        }

        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        Chat existingChat = chatManager.getThreadChat(roomId);

        if (existingChat == null) {
            if (companionJid == null) {
                companionJid = JidCreatorHelper
                        .obtainUserJid(
                                roomId
                                        .replace(userJid.split("@")[0], "")
                                        .replace("_", "")
                                                //// TODO: 12/15/15  remove after implemented social graph
                                        .replace("yu", "y_u"));
            }
            chat = chatManager.createChat(companionJid, roomId, null);
        } else {
            chat = existingChat;
        }

        chat.addMessageListener(messageListener);
    }
}
