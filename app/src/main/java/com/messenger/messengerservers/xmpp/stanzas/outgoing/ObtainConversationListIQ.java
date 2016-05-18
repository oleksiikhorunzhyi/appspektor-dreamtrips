package com.messenger.messengerservers.xmpp.stanzas.outgoing;

import org.jivesoftware.smack.packet.IQ;


public class ObtainConversationListIQ extends IQ {

    private static final String NAMESPACE = "urn:xmpp:archive";
    private static final String ELEMENT = "list";

    private int max = 10;

    public ObtainConversationListIQ() {
        super(ELEMENT, NAMESPACE);
        setType(Type.get);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder buf) {
        buf.rightAngleBracket();
        buf.append("<set xmlns='http://jabber.org/protocol/rsm'>");
        buf.append("<max>").append(Integer.toString(max)).append("</max>");
        buf.append("</set>");
        return buf;
    }
}
