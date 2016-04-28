package com.messenger.messengerservers.xmpp.loaders;

import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.providers.ConversationListProvider;
import com.messenger.messengerservers.xmpp.providers.ConversationProvider;
import com.messenger.messengerservers.xmpp.stanzas.ConversationIQ;
import com.messenger.messengerservers.xmpp.stanzas.ObtainConversationIQ;
import com.messenger.messengerservers.xmpp.stanzas.ConversationListIQ;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.Arrays;

import timber.log.Timber;

public class XmppConversationLoader extends BaseXmppConversationLoader {

    private final String conversationId;

    public XmppConversationLoader(XmppServerFacade facade, String conversationId) {
        super(facade);
        this.conversationId = conversationId;
        ProviderManager.addIQProvider(
                ConversationIQ.ELEMENT, ConversationIQ.NAMESPACE,
                new ConversationProvider(facade.getGson())
        );
    }

    @Override
    public void load() {
        try {
            loadConversation();
        } catch (SmackException.NotConnectedException e) {
            Timber.e(e, "Can't load conversations");
        }
    }

    private void loadConversation() throws SmackException.NotConnectedException {
        facade.getConnection().sendStanzaWithResponseCallback(new ObtainConversationIQ(conversationId),
                (stanza) -> stanza instanceof ConversationIQ,
                (stanzaPacket) -> {
                    Conversation conversation = ((ConversationIQ) stanzaPacket).getConversation();
                    obtainParticipantsAndReport(Arrays.asList(conversation));
                });
    }
}
