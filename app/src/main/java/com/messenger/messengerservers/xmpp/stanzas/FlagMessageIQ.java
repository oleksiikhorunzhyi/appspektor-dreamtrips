package com.messenger.messengerservers.xmpp.stanzas;

import org.jivesoftware.smack.packet.IQ;

public class FlagMessageIQ extends IQ {

    public static final String NAMESPACE = "worldventures.com#user";
    public static final String ELEMENT_QUERY = "query";

    public static final String FLAG_MESSAGES_ELEMENT_NAME = "flag-messages";
    public static final String MESSAGE_ELEMENT_NAME = "message";

    public static final String MESSAGE_ID_ATTRIBUTE = "client_msg_id";
    public static final String RESULT_ATTRIBUTE = "result";
    public static final String REASON_ID_ATTRIBUTE = "reason_id";

    private String reasonId;
    private String reasonDescription;

    private String messageId;
    private String result;

    public FlagMessageIQ() {
        super(ELEMENT_QUERY, NAMESPACE);
    }

    public FlagMessageIQ(String messageId, String reasonId, String reasonDescription) {
        super(ELEMENT_QUERY, NAMESPACE);
        this.messageId = messageId;
        this.reasonId = reasonId;
        this.reasonDescription = reasonDescription;
        setType(Type.set);
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();
        xml.openElement(FLAG_MESSAGES_ELEMENT_NAME);
        //
        xml.halfOpenElement(MESSAGE_ELEMENT_NAME);
        xml.attribute(MESSAGE_ID_ATTRIBUTE, messageId);
        xml.attribute(REASON_ID_ATTRIBUTE, reasonId);
        xml.rightAngleBracket();
        //
        xml.append(reasonDescription);
        //
        xml.closeElement(MESSAGE_ELEMENT_NAME);
        //
        xml.closeElement(FLAG_MESSAGES_ELEMENT_NAME);
        return xml;
    }
}
