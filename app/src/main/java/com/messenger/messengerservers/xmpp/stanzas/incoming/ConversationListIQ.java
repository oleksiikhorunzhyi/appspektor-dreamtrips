package com.messenger.messengerservers.xmpp.stanzas.incoming;

import com.messenger.messengerservers.model.Conversation;

import org.jivesoftware.smack.packet.IQ;

import java.util.List;

public class ConversationListIQ extends IQ {

    public static final String NAMESPACE = "urn:xmpp:archive";
    public static final String ELEMENT_LIST = "list";

    private final List<Conversation> conversations;

    public ConversationListIQ(List<Conversation> conversations) {
        super(ELEMENT_LIST, NAMESPACE);
        this.conversations = conversations;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }
}
