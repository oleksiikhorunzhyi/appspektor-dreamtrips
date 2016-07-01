package com.messenger.messengerservers.xmpp.stanzas;

import org.jivesoftware.smack.packet.IQ;

public abstract class BaseClearChatIQ extends IQ {
    public static final String NAMESPACE = "worldventures.com#user-clear-chat";
    public static final String ELEMENT_CONVERSATION_ID = "conversation-id";
    public static final String ELEMENT_QUERY = "query";

    private final String chatId;

    public BaseClearChatIQ(String chatId,  String userJid) {
        super(ELEMENT_QUERY, NAMESPACE);
        setType(Type.set);
        setTo(userJid);
        this.chatId = chatId;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.openElement(ELEMENT_CONVERSATION_ID).append(chatId).closeElement(ELEMENT_CONVERSATION_ID);
        addChildElement(xml);

        return xml;
    }

    protected abstract void addChildElement(IQChildElementXmlStringBuilder xml);

    public String getChatId() {
        return chatId;
    }
}
