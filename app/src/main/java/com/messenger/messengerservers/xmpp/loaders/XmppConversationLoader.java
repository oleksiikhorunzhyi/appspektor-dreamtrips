package com.messenger.messengerservers.xmpp.loaders;

import android.util.Log;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.packets.ConversationsPacket;
import com.messenger.messengerservers.xmpp.packets.ObtainConversationListPacket;
import com.messenger.messengerservers.xmpp.providers.ConversationProvider;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.List;

public class XmppConversationLoader extends Loader<Conversation> {

    private static final int MAX_CONVERSATIONS = 30;
    private final XmppServerFacade facade;

    public XmppConversationLoader(XmppServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public void load() {
        ObtainConversationListPacket packet = new ObtainConversationListPacket();
        packet.setMax(MAX_CONVERSATIONS);
        packet.setType(IQ.Type.get);
        Log.i("Send XMPP Packet: ", packet.toString());

        try {
            ProviderManager.addIQProvider(ConversationsPacket.ELEMENT_LIST, ConversationsPacket.NAMESPACE, new ConversationProvider());
            facade.getConnection().sendStanzaWithResponseCallback(packet,
                    (stanza) -> stanza instanceof ConversationsPacket,
                    (stanzaPacket) -> {
                        List<Conversation> conversations = ((ConversationsPacket) stanzaPacket).getConversations();

                        if(persister != null){
                            persister.save(conversations);
                        }

                        if (onEntityLoadedListener != null) {
                            onEntityLoadedListener.onLoaded(conversations);
                        }
                        ProviderManager.removeIQProvider(ConversationsPacket.ELEMENT_LIST, ConversationsPacket.NAMESPACE);
                    });
        } catch (SmackException.NotConnectedException e) {
            Log.i("XmppConversationLoader", "Loading error", e);
        }
    }
}
