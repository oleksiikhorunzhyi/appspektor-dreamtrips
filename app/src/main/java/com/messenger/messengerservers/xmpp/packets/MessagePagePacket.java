package com.messenger.messengerservers.xmpp.packets;

import org.jivesoftware.smack.packet.IQ;

import java.util.List;

import com.messenger.messengerservers.entities.Message;

public class MessagePagePacket extends IQ{

    public static final String NAMESPACE = "urn:xmpp:archive";
    public static final String ELEMENT_LIST = "list";

    private List<Message> messages;

    public MessagePagePacket(){
        super(ELEMENT_LIST, NAMESPACE);
    }

    public void add(Message message){
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
