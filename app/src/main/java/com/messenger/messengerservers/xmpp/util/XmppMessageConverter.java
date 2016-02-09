package com.messenger.messengerservers.xmpp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.Message;
import com.messenger.messengerservers.model.MessageBody;
import com.messenger.messengerservers.xmpp.providers.GsonAttachmentAdapter;

import org.apache.commons.lang3.StringEscapeUtils;

public final class XmppMessageConverter {

    private Gson gson;

    public XmppMessageConverter() {
        gson = new GsonBuilder().registerTypeAdapter(AttachmentHolder.class, new GsonAttachmentAdapter()).create();
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
        String body = StringEscapeUtils.unescapeXml(message.getBody());
        MessageBody stanzaMessageBody = gson.fromJson(body, MessageBody.class);
        Message.Builder builder = new Message.Builder()
                .messageBody(stanzaMessageBody)
                .conversationId(message.getThread())
                .id(message.getStanzaId());

        if (message.getTo() != null) {
            builder.toId(JidCreatorHelper.obtainId(message.getTo()));
        }
        if (message.getFrom() != null) {
            builder.fromId(parseUserId(message));
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
