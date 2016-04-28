package com.messenger.messengerservers.xmpp.stanzas;

import com.messenger.messengerservers.model.Conversation;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;


public class ConversationListIQ extends IQ {

    public static final String NAMESPACE = "urn:xmpp:archive";
    public static final String ELEMENT_LIST = "list";

    private List<Conversation> conversations = new ArrayList<>();

    public ConversationListIQ() {
        super(ELEMENT_LIST, NAMESPACE);
        conversations = new ArrayList<>();
    }

    public void addConversations(List<Conversation> conversations) {
        this.conversations.clear();
        this.conversations.addAll(conversations);
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }
}
