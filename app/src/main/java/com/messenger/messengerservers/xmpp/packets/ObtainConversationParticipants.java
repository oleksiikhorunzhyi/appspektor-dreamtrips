package com.messenger.messengerservers.xmpp.packets;

import org.jivesoftware.smack.packet.IQ;

public class ObtainConversationParticipants extends IQ {

    public static final String NAMESPACE = "http://jabber.org/protocol/muc#admin";
    public static final String ELEMENT_QUERY = "query";

    public ObtainConversationParticipants() {
        super(ELEMENT_QUERY, NAMESPACE);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.append("<item affiliation='any'/>");
        return xml;
    }
}
