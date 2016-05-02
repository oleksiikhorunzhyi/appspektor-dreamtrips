package com.messenger.messengerservers.xmpp.loaders;

import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.stanzas.incoming.ConversationListIQ;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.ObtainConversationListIQ;
import com.messenger.messengerservers.xmpp.providers.ConversationListProvider;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.List;

import timber.log.Timber;

public class XmppConversationListLoader extends BaseXmppConversationLoader {

    private static final int MAX_CONVERSATIONS = 512;

    public XmppConversationListLoader(XmppServerFacade facade) {
        super(facade);
        ProviderManager.addIQProvider(
                ConversationListIQ.ELEMENT_LIST, ConversationListIQ.NAMESPACE,
                new ConversationListProvider(facade.getGson())
        );
    }

    @Override
    public void load() {
        ObtainConversationListIQ packet = new ObtainConversationListIQ();
        packet.setMax(MAX_CONVERSATIONS);
        try {
            loadConversations(packet);
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e, "Can't load conversations");
        }
    }

    private void loadConversations(ObtainConversationListIQ packet) throws SmackException.NotConnectedException {
        facade.getConnection().sendStanzaWithResponseCallback(packet,
                (stanza) -> stanza instanceof ConversationListIQ,
                (stanzaPacket) -> {
                    List<Conversation> conversations = ((ConversationListIQ) stanzaPacket).getConversations();
                    obtainParticipantsAndReport(conversations);
                });
    }
}
