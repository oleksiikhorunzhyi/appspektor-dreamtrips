package com.messenger.messengerservers.xmpp.packets;

import com.messenger.messengerservers.xmpp.entities.ConversationWithLastMessage;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;


public class ConversationsPacket extends IQ {

    public static final String NAMESPACE = "urn:xmpp:archive";
    public static final String ELEMENT_LIST = "list";

    private List<ConversationWithLastMessage> conversations;

    public ConversationsPacket() {
        super(ELEMENT_LIST, NAMESPACE);
        conversations = new ArrayList<>();
    }

    public void addConversation(ConversationWithLastMessage conversation) {
        conversations.add(conversation);
    }

    public List<ConversationWithLastMessage> getConversations() {
        return conversations;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }
}
