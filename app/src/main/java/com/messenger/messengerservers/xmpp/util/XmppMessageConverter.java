package com.messenger.messengerservers.xmpp.util;

import com.google.gson.Gson;
import com.messenger.delegate.MessageBodyParser;
import com.messenger.messengerservers.constant.MessageType;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.xmpp.extensions.SystemMessageExtension;

public final class XmppMessageConverter {

    private Gson gson;
    private MessageBodyParser messageBodyParser;

    public XmppMessageConverter() {
        gson = new Gson();
    }

    public XmppMessageConverter(Gson gson) {
        this.gson = gson;
        messageBodyParser = new MessageBodyParser(gson);
    }

    public org.jivesoftware.smack.packet.Message convert(Message message) {
        String bodyJson = gson.toJson(message.getMessageBody());

        org.jivesoftware.smack.packet.Message smackMessage = new org.jivesoftware.smack.packet.Message();
        smackMessage.setStanzaId(message.getId());
        smackMessage.setFrom(JidCreatorHelper.obtainUserJid(message.getFromId()));
        smackMessage.setThread(message.getConversationId());
        smackMessage.setBody(bodyJson);

        return smackMessage;
    }

    public Message convert(org.jivesoftware.smack.packet.Message message) {
        Message.Builder builder = new Message.Builder()
                .messageBody(messageBodyParser.parseMessageBody(message.getBody()))
                .conversationId(message.getThread())
                .id(message.getStanzaId())
                .type(MessageType.MESSAGE);

        if (message.getTo() != null) {
            builder.toId(JidCreatorHelper.obtainId(message.getTo()));
        }
        if (message.getFrom() != null) {
            builder.fromId(parseUserId(message));
        }
        return builder.build();
    }

    public Message convertSystemMessage(org.jivesoftware.smack.packet.Message message,
                                        SystemMessageExtension systemMessageExtension) {
        Message.Builder builder = new Message.Builder()
                .conversationId(JidCreatorHelper.obtainId(message.getFrom()))
                .id(message.getStanzaId());
        if (systemMessageExtension.getTo() != null) {
            builder.toId(JidCreatorHelper.obtainId(systemMessageExtension.getTo()));
        }
        return builder
                .fromId(JidCreatorHelper.obtainId(systemMessageExtension.getFrom()))
                .type(ParseUtils.parseMessageType(systemMessageExtension.getType()))
                .date(systemMessageExtension.getTimestamp())
                .build();
    }

    private String parseUserId(org.jivesoftware.smack.packet.Message message){
        switch (message.getType()){
            case chat:
                return JidCreatorHelper.obtainId(message.getFrom());
            case groupchat:
                return JidCreatorHelper.obtainUserIdFromGroupJid(message.getFrom());
        }
        return null;
    }

}
