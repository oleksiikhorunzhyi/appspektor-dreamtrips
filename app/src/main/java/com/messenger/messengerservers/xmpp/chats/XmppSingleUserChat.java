package com.messenger.messengerservers.xmpp.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.packets.StatusMessagePacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.ThreadCreatorHelper;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message.Type;

public class XmppSingleUserChat extends XmppChat implements SingleUserChat {

    @Nullable
    private Chat chat;
    private final String companionId;

    public XmppSingleUserChat(final XmppServerFacade facade, @Nullable String companionId, @Nullable String roomId) {
        super(facade, roomId);
        this.companionId = companionId;
        connectToFacade();
    }

    @Override
    protected void trySendSmackMessage(org.jivesoftware.smack.packet.Message message) throws SmackException.NotConnectedException {
        if (chat == null) throw new SmackException.NotConnectedException();

        chat.sendMessage(message);
    }

    @Override
    protected StatusMessagePacket createStatusMessage(String messageId) {
        return new StatusMessagePacket(messageId, Status.DISPLAYED,
                JidCreatorHelper.obtainUserJid(companionId), Type.chat);
    }

    public void setConnection(@NonNull AbstractXMPPConnection connection) {
        String userJid = facade.getUsername();
        String companionJid = null;

        if (companionId != null) {
            companionJid = JidCreatorHelper.obtainUserJid(companionId);
        }
        if (roomId == null) {
            if (companionJid == null) throw new Error();
            roomId = ThreadCreatorHelper.obtainThreadSingleChatFromJids(userJid, companionJid);
        }

        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        chat = chatManager.getThreadChat(roomId);

        if (chat == null) {
            if (companionJid == null) {
                companionJid = ThreadCreatorHelper.obtainCompanionFromSingleChat(roomId, userJid);
            }
            chat = chatManager.createChat(companionJid, roomId, null);
        }
    }

}
