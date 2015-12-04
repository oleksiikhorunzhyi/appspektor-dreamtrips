package com.messenger.messengerservers.xmpp.loaders;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.xmpp.packets.ConversationsPacket;
import com.messenger.messengerservers.xmpp.packets.ObtainConversationListPacket;
import com.messenger.messengerservers.xmpp.providers.ConversationProvider;

public class XmppConversationLoader extends Loader<Conversation> {

    private static final int MAX_CONVERSATIONS = 30;

    private final AbstractXMPPConnection connection;

    public XmppConversationLoader(AbstractXMPPConnection connection) {
        this.connection = connection;
    }

    @Override
    public void load(){
        ObtainConversationListPacket packet = new ObtainConversationListPacket();
        packet.setMax(MAX_CONVERSATIONS);
        packet.setStanzaId("req1");
        packet.setType(IQ.Type.get);
        Log.i("Send XMPP Packet: ", packet.toString());

        try {
            ProviderManager.addIQProvider(ConversationsPacket.ELEMENT_LIST, ConversationsPacket.NAMESPACE, new ConversationProvider());
            connection.sendStanzaWithResponseCallback(packet,
                    (stanza) -> stanza instanceof ConversationsPacket,
                    (stanzaPacket) -> {
                        onEntityLoadedListener.onLoaded(((ConversationsPacket) stanzaPacket).getConversations());
                        ProviderManager.removeIQProvider(ConversationsPacket.ELEMENT_LIST, ConversationsPacket.NAMESPACE);
                    });
        } catch (SmackException.NotConnectedException e) {
            Log.i("XmppConversationLoader",  "Loading error", e);
        }
    }
}
