package com.messenger.messengerservers.xmpp.stanzas.incoming;


import android.support.annotation.NonNull;

import com.messenger.messengerservers.model.Participant;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;

public class ConversationParticipantsIQ extends IQ {

    public static final String NAMESPACE = "http://jabber.org/protocol/muc#admin";
    public static final String ELEMENT_QUERY = "query";

    private final List<Participant> participants = new ArrayList<>();

    public ConversationParticipantsIQ() {
        super(ELEMENT_QUERY, NAMESPACE);
    }

    public void addParticipant(Participant participant) {
        // TODO: 2/2/16 remove this condition
        if (!participants.contains(participant)) {
            participants.add(participant);
        }
    }

    @NonNull
    public List<Participant> getParticipants() {
        return participants;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }
}
