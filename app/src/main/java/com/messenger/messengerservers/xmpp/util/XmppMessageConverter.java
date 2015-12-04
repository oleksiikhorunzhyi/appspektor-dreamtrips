package com.messenger.messengerservers.xmpp.util;

import com.google.gson.Gson;

import java.util.Locale;

import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.xmpp.entities.MessageBody;

public final class XmppMessageConverter {

    private XmppMessageConverter(){

    }

    public static org.jivesoftware.smack.packet.Message convert(Message message){
        MessageBody messageBody = new MessageBody.Builder()
                .locale(message.getLocale().toString())
                .text(message.getText())
                .build();
        String bodyJson = new Gson().toJson(messageBody);

        org.jivesoftware.smack.packet.Message smackMessage = new org.jivesoftware.smack.packet.Message();
        smackMessage.setFrom(JidCreatorHelper.obtainJid(message.getFrom()));
        smackMessage.setBody(bodyJson);

        return smackMessage;
    }

    public static Message convert(org.jivesoftware.smack.packet.Message message){
        MessageBody stanzaMessageBody = new Gson().fromJson(message.getBody(), MessageBody.class);

        return new Message.Builder()
                .from(JidCreatorHelper.obtainUser(message.getFrom()))
                .to(JidCreatorHelper.obtainUser(message.getTo()))
                .text(stanzaMessageBody.getText())
                .locale(new Locale(stanzaMessageBody.getLocale()))
                .build();
    }

}
