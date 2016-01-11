package com.messenger.messengerservers.xmpp.util;

import com.google.gson.Gson;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.xmpp.entities.MessageBody;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Locale;

public final class XmppMessageConverter {

    private Gson gson;

    public XmppMessageConverter() {
        gson = new Gson();
    }

    public org.jivesoftware.smack.packet.Message convert(Message message) {
        Locale locale = message.getLocale() ;
        MessageBody messageBody = new MessageBody.Builder()
                //// // TODO: 1/9/16 remove this checking!!!
                .locale(locale == null ? Locale.getDefault().toString() : locale.toString())
                .text(message.getText())
                .build();
        String bodyJson = new Gson().toJson(messageBody);

        org.jivesoftware.smack.packet.Message smackMessage = new org.jivesoftware.smack.packet.Message();
        smackMessage.setStanzaId(message.getId());
        smackMessage.setFrom(JidCreatorHelper.obtainUserJid(message.getFromId()));
        smackMessage.setThread(message.getConversationId());
        smackMessage.setBody(bodyJson);

        return smackMessage;
    }

    public Message convert(org.jivesoftware.smack.packet.Message message) {
        String body = StringEscapeUtils.unescapeXml(message.getBody());
        MessageBody stanzaMessageBody = gson.fromJson(body, MessageBody.class);

        Message.Builder builder = new Message.Builder()
                .text(stanzaMessageBody.getText())
                .conversationId(message.getThread())
                .id(message.getStanzaId())
                .locale(new Locale(stanzaMessageBody.getLocale()));

        if (message.getTo() != null) {
            builder.to(JidCreatorHelper.obtainId(message.getTo()));
        }
        if (message.getFrom() != null) {
            builder.from(parseUserId(message));
        }

        return builder.build();
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
