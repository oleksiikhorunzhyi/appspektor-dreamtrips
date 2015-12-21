package com.messenger.messengerservers.xmpp.packets;


import com.messenger.messengerservers.entities.User;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;

public class ConversationParticipants extends IQ {

    public static final String NAMESPACE = "http://jabber.org/protocol/muc#admin";
    public static final String ELEMENT_QUERY = "query";

    private String conversationId;
    private List<User> participants;
    private User owner;

    public ConversationParticipants() {
        super(ELEMENT_QUERY, NAMESPACE);
        participants = new ArrayList<>();
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public void addParticipant(User user){
        participants.add(user);
    }

    public java.util.List<User> getParticipants() {
        return participants;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }
}
