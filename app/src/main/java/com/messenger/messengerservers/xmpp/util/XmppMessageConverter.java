package com.messenger.messengerservers.xmpp.util;

import com.google.gson.Gson;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.xmpp.entities.MessageBody;

import java.util.Locale;

public final class XmppMessageConverter {

    private XmppMessageConverter() {

    }

    public static org.jivesoftware.smack.packet.Message convert(Message message) {
        MessageBody messageBody = new MessageBody.Builder()
                .locale(message.getLocale().toString())
                .text(message.getText())
                .build();
        String bodyJson = new Gson().toJson(messageBody);

        org.jivesoftware.smack.packet.Message smackMessage = new org.jivesoftware.smack.packet.Message();
        smackMessage.setFrom(JidCreatorHelper.obtainUserJid(message.getFrom().getUserName()));
        smackMessage.setBody(bodyJson);

        return smackMessage;
    }

    public static Message convert(org.jivesoftware.smack.packet.Message message) {
        MessageBody stanzaMessageBody = new Gson().fromJson(message.getBody(), MessageBody.class);

        Message.Builder builder = new Message.Builder()
                .text(stanzaMessageBody.getText())
                .conversationId(message.getThread())
                .id(message.getStanzaId())
                .locale(new Locale(stanzaMessageBody.getLocale()));

        if (message.getTo() != null) {
            builder.to(JidCreatorHelper.obtainUser(message.getTo()));
        }
        if (message.getFrom() != null) {
            builder.from(JidCreatorHelper.obtainUser(message.getFrom()));
        }

        return builder.build();
    }

}
