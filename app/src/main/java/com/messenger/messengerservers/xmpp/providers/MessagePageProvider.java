package com.messenger.messengerservers.xmpp.providers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.xmpp.entities.MessageBody;
import com.messenger.messengerservers.xmpp.packets.MessagePagePacket;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.ParserUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class MessagePageProvider extends IQProvider<MessagePagePacket> {

    private Gson gson;

    public MessagePageProvider() {
        gson = new Gson();
    }

    @Override
    public MessagePagePacket parse(XmlPullParser parser, int initialDepth) throws XmlPullParserException, IOException, SmackException {
        MessagePagePacket messagePagePacket = new MessagePagePacket();

        String elementName;
        Message.Builder messageBuilder = null;

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "from":
                        case "to":
                            long timestamp = ParserUtils.getLongAttribute(parser, "secs");
                            String jid = parser.getAttributeValue("", "jid");
                            String messageId = parser.getAttributeValue("", "client_msg_id");
                            messageBuilder = new Message.Builder()
                                    .id(messageId)
                                    //// TODO: 12/18/15 today attribute secs is millisecond
                                    .date(new Date(timestamp))
                                    .from(JidCreatorHelper.obtainUserId(jid));
                            break;
                        case "body":
                            String messageBody = StringEscapeUtils.unescapeXml(parser.nextText());

                            MessageBody stanzaMessageBody = null;
                            try {
                                stanzaMessageBody = gson.fromJson(messageBody, MessageBody.class);
                            } catch (JsonSyntaxException ignore){}

                            if (stanzaMessageBody == null || stanzaMessageBody.getLocale() == null || stanzaMessageBody.getText() == null){
                                messageBuilder = null;
                            } else {
                                messageBuilder.text(stanzaMessageBody.getText())
                                        .locale( new Locale(stanzaMessageBody.getLocale()));
                            }

                    }
                    break;
                case XmlPullParser.END_TAG:
                    elementName = parser.getName();
                    switch (elementName) {
                        case "to":
                        case "from":
                            if (messageBuilder == null) continue;
                            Message message = messageBuilder.build();
                            if (message.getText() == null) continue;
                            messagePagePacket.add(message);
                            break;
                        case "chat":
                            done = true;
                            break;
                    }
                    break;
            }
        }
        return messagePagePacket;
    }
}
