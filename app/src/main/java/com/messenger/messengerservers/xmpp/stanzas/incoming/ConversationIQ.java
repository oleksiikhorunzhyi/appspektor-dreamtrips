package com.messenger.messengerservers.xmpp.stanzas.incoming;

import com.messenger.messengerservers.model.Conversation;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;

public class ConversationIQ extends DiscoverInfo {

    public static final String NAMESPACE = DiscoverInfo.NAMESPACE;
    public static final String ELEMENT = DiscoverInfo.QUERY_ELEMENT;

    private Conversation conversation;

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}

