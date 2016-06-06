package com.messenger.messengerservers.xmpp.stanzas.outgoing;

import org.jivesoftware.smack.packet.IQ;

public class ObtainMessageListIQ extends IQ {

    private static final String NAMESPACE = "urn:xmpp:archive";
    private static final String ELEMENT_RETRIVE = "retrieve";

    private String conversationId;
    private int page = 1;
    private int max = 20;
    private long since = 0;

    public ObtainMessageListIQ() {
        super(ELEMENT_RETRIVE, NAMESPACE);
        setType(Type.get);
        setStanzaId("page" + page);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
        setPacketID("page" + page);
    }

    public long getSince() {
        return since;
    }

    public void setSinceSec(long since) {
        this.since = since;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.attribute("with", conversationId);
        xml.rightAngleBracket();
        xml.append("<set xmlns='http://jabber.org/protocol/rsm'>");
        xml.append("<max>").append(Integer.toString(max)).append("</max>");
        if (page != 1) {
            xml.append("<before>").append(Long.toString(since)).append("</before>");
        }
        xml.append("</set>");
        return xml;
    }


}
