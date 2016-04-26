package com.messenger.messengerservers.xmpp.stanzas;

import com.messenger.messengerservers.model.Conversation;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;


public class ConversationsIQ extends IQ {

    public static final String NAMESPACE = "urn:xmpp:archive";
    public static final String ELEMENT_LIST = "list";

    private List<Conversation> conversations;

    public ConversationsIQ() {
        super(ELEMENT_LIST, NAMESPACE);
        conversations = new ArrayList<>();
    }

    public void addConversation(Conversation conversation) {
        // TODO: 1/29/16 remove  if (!conversations.contains(conversation))
        if (!conversations.contains(conversation)) {
            conversations.add(conversation);
        }
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }
}
