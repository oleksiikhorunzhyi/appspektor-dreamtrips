package com.messenger.messengerservers.xmpp.stanzas;

public class RevertClearChatIQ extends BaseClearChatIQ {
    public static final String ELEMENT_REMOVE_CLEARING = "remove-clear";

    public RevertClearChatIQ(String conversationId) {
        super(conversationId);
    }

    @Override
    protected void addChildElement(IQChildElementXmlStringBuilder xml) {
        xml.emptyElement(ELEMENT_REMOVE_CLEARING);
    }
}
