package com.messenger.messengerservers.xmpp.packets;

import org.jivesoftware.smack.packet.IQ;

public class ObtainMessageListPacket extends IQ {

    private static final String NAMESPACE = "urn:xmpp:archive";
    private static final String ELEMENT_RETRIVE = "retrieve";

    private String conversationId;
    private int page = 1;
    private int max = 20;
    private int sinceSec = 0;

    public ObtainMessageListPacket() {
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

    public int getSinceSec() {
        return sinceSec;
    }

    public void setSinceSec(int sinceSec) {
        this.sinceSec = sinceSec;
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
            xml.append("<before>").append(Integer.toString(sinceSec)).append("</before>");
        }
        xml.append("</set>");
        return xml;
    }


}
