package com.messenger.messengerservers.xmpp.packets;

import com.messenger.messengerservers.model.Message;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;
import java.util.List;

public class MessagePagePacket extends IQ {

    public static final String NAMESPACE = "urn:xmpp:archive";
    public static final String ELEMENT_CHAT = "chat";

    private List<Message> messages;

    public MessagePagePacket() {
        super(ELEMENT_CHAT, NAMESPACE);
        messages = new ArrayList<>();
    }

    public void add(Message message) {
        messages.add(message);
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        return null;
    }
}
