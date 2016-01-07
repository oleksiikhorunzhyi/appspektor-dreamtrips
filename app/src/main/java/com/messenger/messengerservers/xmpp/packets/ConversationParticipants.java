package com.messenger.messengerservers.xmpp.packets;


import com.messenger.messengerservers.entities.Participant;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;

public class ConversationParticipants extends IQ {

    public static final String NAMESPACE = "http://jabber.org/protocol/muc#admin";
    public static final String ELEMENT_QUERY = "query";

    private List<Participant> participants;
    private Participant owner;
    private boolean abandoned;

    public ConversationParticipants() {
        super(ELEMENT_QUERY, NAMESPACE);
        participants = new ArrayList<>();
    }

    public Participant getOwner() {
        return owner;
    }

    public void setOwner(Participant owner) {
        this.owner = owner;
    }

    public boolean isAbandoned() {
        return abandoned;
    }

    public void setAbandoned(boolean abandoned) {
        this.abandoned = abandoned;
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }
}
