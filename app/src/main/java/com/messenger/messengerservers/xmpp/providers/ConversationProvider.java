package com.messenger.messengerservers.xmpp.providers;

import android.util.Log;

import com.google.gson.Gson;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.xmpp.entities.MessageBody;
import com.messenger.messengerservers.xmpp.packets.ConversationsPacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class ConversationProvider extends IQProvider<ConversationsPacket> {

    @Override
    public ConversationsPacket parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        ConversationsPacket conversationsPacket = new ConversationsPacket();

        String elementName;
        Message message = null;
        Conversation conversation = null;

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "chat":
                            String subject = parser.getAttributeValue("", "subject");
                            String type = parser.getAttributeValue("", "type");
                            String thread = parser.getAttributeValue("", "thread");
                            conversation = new Conversation(thread, subject, type.toLowerCase());
                            break;
                        case "last-message":
                            String from = parser.getAttributeValue("", "from");
                            long timestamp = ParserUtils.getLongAttribute(parser, "time");
                            String messageId = parser.getAttributeValue("", "client_msg_id");
                            String messageBody = StringEscapeUtils.unescapeXml(parser.nextText());

                            MessageBody stanzaMessageBody = new Gson().fromJson(messageBody, MessageBody.class);
                            message = new Message.Builder()
                                    .id(messageId)
                                    .conversationId(conversation.getId())
                                    .text(stanzaMessageBody.getText())
                                    .locale(new Locale(stanzaMessageBody.getLocale()))
                                    .date(new Date(timestamp * 1000))
                                    .from(JidCreatorHelper.obtainUser(from))
                                    .build();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "chat":
                            //noinspection all
                            conversation.setLastMessage(message);
                            conversationsPacket.addConversation(conversation);
                            break;
                        case "list":
                            done = true;
                            break;
                    }
                    break;
            }
        }
        return conversationsPacket;
    }
}
