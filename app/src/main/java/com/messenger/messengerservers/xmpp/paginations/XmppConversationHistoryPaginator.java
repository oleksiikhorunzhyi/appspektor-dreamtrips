package com.messenger.messengerservers.xmpp.paginations;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ProviderManager;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.paginations.PagePagination;
import com.messenger.messengerservers.xmpp.packets.ConversationsPacket;
import com.messenger.messengerservers.xmpp.packets.MessagePagePacket;
import com.messenger.messengerservers.xmpp.packets.ObtainMessageListPacket;
import com.messenger.messengerservers.xmpp.providers.MessagePageProvider;

public class XmppConversationHistoryPaginator extends PagePagination<Message> {
    public static final int MAX_MESSAGE = 20;

    private AbstractXMPPConnection connection;
    private String conversationId;

    public XmppConversationHistoryPaginator(AbstractXMPPConnection connection, String conversationId) {
        this.connection = connection;
        this.conversationId = conversationId;
    }

    @Override
    public void loadPage(int page) {
        ObtainMessageListPacket packet = new ObtainMessageListPacket();
        packet.setMax(MAX_MESSAGE);
        packet.setConversationId(conversationId);
        packet.setPage(page);
        Log.i("Send XMPP Packet: ", packet.toString());

        try {
            ProviderManager.addIQProvider(MessagePagePacket.IQ_ELEMENT, MessagePagePacket.NAMESPACE, new MessagePageProvider());
            connection.sendStanzaWithResponseCallback(packet,
                    (stanza) -> stanza instanceof ConversationsPacket,
                    (stanzaPacket) -> {
                        onEntityLoadedListener.onLoaded(((MessagePagePacket) stanzaPacket).getMessages());
                        ProviderManager.removeIQProvider(MessagePagePacket.IQ_ELEMENT, MessagePagePacket.NAMESPACE);
                    });
        } catch (SmackException.NotConnectedException e) {
            Log.i("XmppMessagePagePaginate",  "Loading error", e);
        }
    }

}
